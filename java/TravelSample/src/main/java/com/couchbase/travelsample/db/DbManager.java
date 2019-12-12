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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
import com.couchbase.lite.Document;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;


@Singleton
public final class DbManager {
    private static final Logger LOGGER = Logger.getLogger(DbManager.class.getName());

    public static final String SGW_ENDPOINT = "ws://127.0.0.1:4984/travel-sample";
    public static final int LOGIN_TIMEOUT_SEC = 30;

    public static final String DB_DIR = "database/";
    public static final String DB_NAME = "travel-sample";
    public static final String DB_SUFFIX = ".cblite2";
    public static final String DB_ZIP = DB_NAME + DB_SUFFIX + ".zip";

    public static final String GUEST_USER = "guest";
    public static final String PROP_DOC_TYPE = "type";
    public static final String TYPE_GUEST_DOC = "bookmarkedhotels";

    static class ReplicationStartListener implements ReplicatorChangeListener {
        @Nonnull
        private final CountDownLatch latch = new CountDownLatch(1);
        @Nonnull
        private final Replicator replicator;

        @Nullable
        private CouchbaseLiteException failure;

        public ReplicationStartListener(@Nonnull Replicator replicator) { this.replicator = replicator; }

        @Nonnull
        public CountDownLatch getLatch() { return latch; }

        @Nullable
        public CouchbaseLiteException getFailure() { return failure; }

        @Override
        public void changed(ReplicatorChange change) {
            if (!replicator.equals(change.getReplicator())) { return; }

            final AbstractReplicator.Status status = replicator.getStatus();
            final AbstractReplicator.ActivityLevel state = status.getActivityLevel();
            LOGGER.log(Level.INFO, "Replicator state: " + state.name());

            switch (state) {
                case CONNECTING:
                    return;
                case STOPPED:
                case OFFLINE:
                    failure = status.getError();
                default:
                    latch.countDown();
            }
        }
    }

    @Nonnull
    private final DbExecutor exec;

    @Nullable
    private Database database;
    @Nullable
    private Replicator replicator;
    @Nullable
    private String currentUser;

    @Inject
    public DbManager(@Nonnull DbExecutor exec) {
        this.exec = exec;

        CouchbaseLite.init();
        Database.log.getConsole().setLevel(LogLevel.DEBUG);
    }

    @Nonnull
    public String getCurrentUser() {
        if (currentUser == null) { throw new IllegalStateException("No user logged in"); }
        return currentUser;
    }


    @Nonnull
    public MutableDocument getGuestDoc() {
        final String guestId = getCurrentUserId();
        LOGGER.log(Level.INFO, "id is: " + guestId);
        if (!guestId.endsWith(GUEST_USER)) { throw new IllegalStateException("Not logged in as guest"); }

        final Document doc = getDatabase().getDocument(guestId);
        if (doc != null) { return doc.toMutable(); }

        final MutableDocument mDoc = new MutableDocument(guestId);
        mDoc.setString(PROP_DOC_TYPE, TYPE_GUEST_DOC);
        return mDoc;
    }

    @Nonnull
    public MutableDocument getUserDoc() {
        final String uId = getCurrentUserId();
        final Document doc = getDatabase().getDocument(uId);
        if (doc != null) { return doc.toMutable(); }

        LOGGER.log(Level.WARNING, "No document for user: " + currentUser);
        throw new IllegalStateException("User " + currentUser + "does not exist. Use the web app to create it");
    }

    public void close() { exec.submit(this::closeAsync); }

    // This is synchronous!  Don't use it from the Swing thread!
    @Nonnull
    public Database getDatabase() {
        if (database == null) { throw new IllegalStateException("db used before open"); }
        return database;
    }

    @Nullable
    Void closeAsync() throws CouchbaseLiteException {
        if (replicator != null) {
            replicator.stop();
            replicator = null;
        }

        if (database != null) {
            database.close();
            database = null;
        }

        currentUser = null;

        return null;
    }

    // This is synchronous!  Don't use it from the Swing thread!
    void openGuestDb() throws IOException, CouchbaseLiteException {
        final DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(new File(DB_DIR, GUEST_USER).getCanonicalPath());
        database = new Database(DB_NAME, config);
        currentUser = GUEST_USER;
    }

    // This is synchronous!  Don't use it from the Swing thread!
    void openUserDb(@Nonnull String username, @Nonnull char[] password)
        throws IOException, CouchbaseLiteException, AuthenticationException, URISyntaxException {
        final File dbDir = new File(DB_DIR, username);

        final DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(dbDir.getCanonicalPath());

        if (!new File(dbDir, DB_NAME + DB_SUFFIX).exists()) {
            Database.copy(unzipDb().getCanonicalFile(), DB_NAME, config);
        }

        database = new Database(DB_NAME, config);
        replicator = startReplication(username, password);

        currentUser = username;
    }

    @Nonnull
    private String getCurrentUserId() {
        if (currentUser == null) { throw new IllegalStateException("No user logged in"); }
        return "user::" + currentUser;
    }

    private Replicator startReplication(@Nonnull String username, @Nonnull char[] password)
        throws CouchbaseLiteException, IOException, AuthenticationException, URISyntaxException {
        final ReplicatorConfiguration config
            = new ReplicatorConfiguration(database, new URLEndpoint(new URI(SGW_ENDPOINT)));
        // !!! copying the password into the string is unsecure.
        config.setAuthenticator(new BasicAuthenticator(username, new String(password)));
        config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        config.setContinuous(true);

        final Replicator repl = new Replicator(config);
        ReplicationStartListener listener = new ReplicationStartListener(repl);
        final ListenerToken token = repl.addChangeListener(listener);
        repl.start();

        final boolean ok;
        try {ok = listener.getLatch().await(LOGIN_TIMEOUT_SEC, TimeUnit.SECONDS); }
        catch (InterruptedException ignore) { throw new IOException("Login interrupted"); }
        finally { repl.removeChangeListener(token); }

        if (!ok) { throw new IOException("Login timeout"); }

        CouchbaseLiteException e = listener.getFailure();
        if (e == null) { return repl; }

        if (e.getCode() != CBLError.Code.HTTP_AUTH_REQUIRED) { throw e; }

        throw new AuthenticationException("Authentication error");
    }

    private File unzipDb() throws IOException {
        final File tmpDir = new File(DB_DIR, "__tmp");

        final File unzippedDb = new File(tmpDir, "travel-sample.cblite2");
        if (!unzippedDb.exists()) {
            if (!tmpDir.exists() && !tmpDir.mkdirs()) {
                throw new IOException("Failed creating tmp directory: " + tmpDir);
            }

            try (InputStream in = DbManager.class.getResource(DB_ZIP).openStream()) { unzipStream(in, tmpDir); }
        }

        return unzippedDb;
    }

    private void unzipStream(@Nonnull InputStream in, @Nonnull File destDir) throws IOException {
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
}

// !!! Use it or remove it
class QueryMgmt {
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

    public void cancelQueries() {
        //exec.submit(this::cancelQueriesAsync);
    }

    // Call only on the executor thread
    void registerQuery(Query query, ListenerToken token) { activeQueries.add(new ActiveQuery(query, token)); }

    @Nullable
    private Void cancelQueriesAsync() {
        for (ActiveQuery activeQuery : activeQueries) { activeQuery.query.removeChangeListener(activeQuery.token); }
        return null;
    }
}
