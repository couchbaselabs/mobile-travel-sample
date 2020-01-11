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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.naming.AuthenticationException;

import com.couchbase.lite.ConsoleLogger;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.FullTextIndexItem;
import com.couchbase.lite.IndexBuilder;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.LogDomain;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.util.RandomString;


@Singleton
public final class DbManager {
    private static final Logger LOGGER = Logger.getLogger(DbManager.class.getName());

    public static final String DB_DIR = "database/";
    public static final String DB_NAME = "travel-sample";
    public static final String DB_SUFFIX = ".cblite2";
    public static final String DB_ZIP = DB_NAME + DB_SUFFIX + ".zip";

    public static final String FTS_INDEX_DESC = "descFTSIndex";

    public static final String GUEST_USER = "guest";

    public static final String PROP_DOC_TYPE = "type";
    public static final String DOC_TYPE_HOTEL_BOOKMARKS = "bookmarkedhotels";
    public static final String DOC_TYPE_AIRLINE = "airline";
    public static final String DOC_TYPE_AIRPORT = "airport";
    public static final String DOC_TYPE_ROUTE = "route";
    public static final String DOC_TYPE_LANDMARK = "landmark";

    private static class ActiveQuery {
        private final Query query;
        private ListenerToken token;

        ActiveQuery(@Nonnull Query query) { this.query = query; }

        public void start(@Nonnull QueryChangeListener listener) { this.token = query.addChangeListener(listener); }

        public void stop() { query.removeChangeListener(token); }
    }

    @Nonnull
    private static final RandomString SESSION_ID_GENERATOR = new RandomString();


    @Nonnull
    private final DbExecutor exec;

    @Nonnull
    private final Map<String, Set<ActiveQuery>> sessions = new HashMap<>();

    @Nullable
    private Database database;
    @Nullable
    private String currentUser;
    @Nullable
    private ReplicatorManager replicationManager;

    @Inject
    public DbManager(@Nonnull DbExecutor exec) {
        this.exec = exec;

        CouchbaseLite.init();

        final ConsoleLogger logger = Database.log.getConsole();
        logger.setLevel(LogLevel.DEBUG);
        logger.setDomains(LogDomain.ALL_DOMAINS);
    }

    // obviously, if there is ever a logged in user named "guest"
    // this is going to fail horribly...
    public boolean isLoggedIn() { return !GUEST_USER.equals(currentUser); }

    @Nonnull
    public String startSession() {
        final String sessionId = SESSION_ID_GENERATOR.nextString(32);
        LOGGER.log(Level.INFO, "start session: " + sessionId);
        synchronized (sessions) { sessions.put(sessionId, new HashSet<>()); }
        return sessionId;
    }

    public void endSession(@Nonnull String sessionId) { exec.submit(() -> endSessionAsync(sessionId)); }

    public void close() { exec.submit(this::closeAsync); }

    @Nonnull
    String getCurrentUser() {
        if (currentUser == null) { throw new IllegalStateException("No user logged in"); }
        return currentUser;
    }

    // This is synchronous!  Don't use it from the Swing thread!
    @Nonnull
    Database getDatabase() {
        if (database == null) { throw new IllegalStateException("db used before open"); }
        return database;
    }

    // This is synchronous!  Don't use it from the Swing thread!
    @Nonnull
    MutableDocument getGuestDoc() {
        final String guestId = getCurrentUserId();
        LOGGER.log(Level.INFO, "guest user: " + guestId);
        if (!guestId.endsWith(GUEST_USER)) { throw new IllegalStateException("Not logged in as guest"); }

        final Document doc = getDatabase().getDocument(guestId);
        if (doc != null) { return doc.toMutable(); }

        final MutableDocument mDoc = new MutableDocument(guestId);
        mDoc.setString(PROP_DOC_TYPE, DOC_TYPE_HOTEL_BOOKMARKS);
        return mDoc;
    }

    // This is synchronous!  Don't use it from the Swing thread!
    @Nonnull
    MutableDocument getUserDoc() {
        final String uId = getCurrentUserId();
        final Document doc = getDatabase().getDocument(uId);
        LOGGER.log(Level.INFO, "authenticated user: " + uId);
        if (doc != null) { return doc.toMutable(); }

        LOGGER.log(Level.WARNING, "No document for user: " + currentUser);
        throw new IllegalStateException("User " + currentUser + "does not exist. Use the web app to create it");
    }

    // This is synchronous!  Don't use it from the Swing thread!
    void startLiveQuery(@Nonnull String sessionId, @Nonnull Query query, @Nonnull QueryChangeListener listener) {
        LOGGER.log(Level.INFO, "start query in session: " + sessionId);
        final ActiveQuery activeQuery;
        synchronized (sessions) {
            final Set<ActiveQuery> session = sessions.get(sessionId);
            if (session == null) { throw new IllegalStateException("No such session: " + sessionId); }
            activeQuery = new ActiveQuery(query);
            session.add(activeQuery);
        }
        activeQuery.start(listener);
    }

    // This is synchronous!  Don't use it from the Swing thread!
    void openGuestDb() throws IOException, CouchbaseLiteException {
        final DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(new File(DB_DIR, GUEST_USER).getCanonicalPath());
        LOGGER.log(Level.INFO, "guest db: " + config.getDirectory());
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
        database.createIndex(
            FTS_INDEX_DESC,
            IndexBuilder.fullTextIndex(FullTextIndexItem.property(Hotel.PROP_DESCRIPTION)));
        LOGGER.log(Level.INFO, "user db: " + config.getDirectory());


        replicationManager = new ReplicatorManager(database);
        replicationManager.start(username, password);

        currentUser = username;
    }

    @Nonnull
    private String getCurrentUserId() {
        if (currentUser == null) { throw new IllegalStateException("No user logged in"); }
        return "user::" + currentUser;
    }

    @Nullable
    private Void closeAsync() throws CouchbaseLiteException {
        if (replicationManager != null) {
            replicationManager.stop();
            replicationManager = null;
        }

        if (database != null) {
            database.close();
            database = null;
        }

        currentUser = null;

        LOGGER.log(Level.INFO, "db closed");

        return null;
    }

    @Nullable
    private Void endSessionAsync(@Nonnull String sessionId) {
        LOGGER.log(Level.INFO, "end session: " + sessionId);
        final List<ActiveQuery> queries;
        synchronized (sessions) {
            final Set<ActiveQuery> querySet = sessions.remove(sessionId);
            if (querySet == null) { return null; }
            queries = new ArrayList<>(querySet);
        }

        for (ActiveQuery activeQuery : queries) { activeQuery.stop(); }

        return null;
    }

    @Nonnull
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

                final File zip = new File(destDir, zipEntry.getName());
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
