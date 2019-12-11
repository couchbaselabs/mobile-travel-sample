//
// Copyright (c) 2019 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.travelsample.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.naming.AuthenticationException;

import com.couchbase.lite.AbstractReplicator;
import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CBLError;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.Query;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;


@Singleton
public final class LocalStore {
    private final static Logger LOGGER = Logger.getLogger(LocalStore.class.getName());

    public static String SGW_ENDPOINT = "ws://127.0.0.1:4984/travel-sample";
    public static int LOGIN_TIMEOUT_SEC = 30;

    public static final String DB_DIR = "database/";
    public static final String GUEST_DB_DIR = DB_DIR + "guest";
    public static final String DB_NAME = "travel-sample";
    public static final String DB_SUFFIX = ".cblite2";
    public static final String DB_ZIP = DB_NAME + DB_SUFFIX + ".zip";

    static class ActiveQuery {
        final Query query;
        final ListenerToken token;

        public ActiveQuery(Query query, ListenerToken token) {
            this.query = query;
            this.token = token;
        }
    }

    // Used only from the executor thread!
    private final Set<ActiveQuery> activeQueries = new HashSet<>();

    private final DBExecutor exec;

    private Database database;
    private Replicator replicator;

    @Inject
    public LocalStore(@Nonnull DBExecutor exec) {
        this.exec = exec;

        CouchbaseLite.init();
        Database.log.getConsole().setLevel(LogLevel.DEBUG);
    }

    public void openAsGuest(@Nonnull Consumer<Void> listener) {
        exec.submit(
            this::openAsGuestAsync,
            (ok) -> listener.accept(null));
    }

    public void openWithValidation(
        @Nonnull String username,
        @Nonnull char[] password,
        @Nonnull Consumer<Exception> listener) {
        exec.submit(
            () -> openWithValidationAsync(username, password),
            (ign) -> listener.accept(null),
            listener);
    }

    public void cancelQueries() { exec.submit(this::cancelQueriesAsync); }

    public void logout() { exec.submit(this::closeAsync); }

    // Call only on the executor thread
    void registerQuery(Query query, ListenerToken token) { activeQueries.add(new ActiveQuery(query, token)); }

    // This is synchronous!  Don't use it from the Swing thread!
    Database getDatabase() {
        if (database == null) { throw new IllegalStateException("db used before open"); }
        return database;
    }

    @Nullable
    private Void cancelQueriesAsync() {
        for (ActiveQuery activeQuery : activeQueries) { activeQuery.query.removeChangeListener(activeQuery.token); }
        return null;
    }

    @Nullable
    private Void closeAsync() throws CouchbaseLiteException {
        cancelQueriesAsync();

        replicator.stop();
        replicator = null;

        database.close();
        database = null;

        return null;
    }

    private Void openAsGuestAsync() throws CouchbaseLiteException, IOException {
        final DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(new File(GUEST_DB_DIR).getCanonicalPath());
        database = new Database(DB_NAME, config);
        return null;
    }

    private Void openWithValidationAsync(@Nonnull String username, @Nonnull char[] password)
        throws IOException, CouchbaseLiteException, URISyntaxException, AuthenticationException {
        try {
            final File dbDir = new File(DB_DIR, username);

            final DatabaseConfiguration config = new DatabaseConfiguration();
            config.setDirectory(dbDir.getCanonicalPath());

            if (!new File(dbDir, DB_NAME + DB_SUFFIX).exists()) {
                Database.copy(unzipDb().getCanonicalFile(), DB_NAME, config);
            }

            database = new Database(DB_NAME, config);
            replicator = startReplication(username, password);
        }
        finally { Arrays.fill(password, (char) 0); }

        return null;
    }

    private File unzipDb() throws IOException {
        final File tmpDir = new File(DB_DIR, "__tmp");

        final File unzippedDb = new File(tmpDir, "travel-sample.cblite2");
        if (!unzippedDb.exists()) {
            if (!tmpDir.exists() && !tmpDir.mkdirs()) {
                throw new IOException("Failed creating tmp directory: " + tmpDir);
            }

            try (InputStream in = LocalStore.class.getResource(DB_ZIP).openStream()) { unzipStream(in, tmpDir); }
        }

        return unzippedDb;
    }

    private void unzipStream(InputStream in, File destDir) throws IOException {
        final byte[] buffer = new byte[1024];

        try (ZipInputStream zin = new ZipInputStream(in)) {
            while (true) {
                final ZipEntry zipEntry = zin.getNextEntry();
                if (zipEntry == null) { break; }

                File zip = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!zip.mkdirs()) { throw new IOException("Failed to unzip directory: " + zip); }
                }
                else {
                    try (FileOutputStream out = new FileOutputStream(zip)) {
                        int len;
                        while ((len = zin.read(buffer)) > 0) { out.write(buffer, 0, len); }
                    }
                }
            }
            zin.closeEntry();
        }
    }

    private Replicator startReplication(@Nonnull String username, @Nonnull char[] password)
        throws CouchbaseLiteException, IOException, AuthenticationException, URISyntaxException {
        final ReplicatorConfiguration config
            = new ReplicatorConfiguration(database, new URLEndpoint(new URI(SGW_ENDPOINT)));
        // !!! copying the password into the string is unsecure.
        config.setAuthenticator(new BasicAuthenticator(username, new String(password)));
        config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        config.setContinuous(true);

        final CouchbaseLiteException[] fail = new CouchbaseLiteException[1];
        final CountDownLatch latch = new CountDownLatch(1);
        final Replicator repl = new Replicator(config);
        final ListenerToken token = repl.addChangeListener(change -> {
            final Replicator r = change.getReplicator();
            if (!repl.equals(r)) { return; }

            final AbstractReplicator.Status status = r.getStatus();
            final AbstractReplicator.ActivityLevel state = status.getActivityLevel();
            LOGGER.log(Level.INFO, "Replicator state: " + state.name());

            switch (state) {
                case STOPPED:
                    fail[0] = status.getError();
                case BUSY:
                case IDLE:
                    latch.countDown();
                    return;
                default:
            }
        });
        repl.start();

        final boolean ok;
        try {ok = latch.await(LOGIN_TIMEOUT_SEC, TimeUnit.SECONDS); }
        catch (InterruptedException ignore) { throw new IOException("Login interrupted"); }
        finally { repl.removeChangeListener(token); }

        if (!ok) { throw new IOException("Login timeout"); }

        CouchbaseLiteException e = fail[0];
        if (e == null) { return repl; }

        if (e.getCode() != CBLError.Code.HTTP_AUTH_REQUIRED) { throw e; }

        throw new AuthenticationException("Authentication error");
    }
}

