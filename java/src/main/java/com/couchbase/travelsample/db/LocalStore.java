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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.Query;


@Singleton
public final class LocalStore {
    public static final String DATABASE_DIR = "database";
    public static final String GUEST_DATABASE_DIR = DATABASE_DIR + "/guest";
    public static final String DATABASE_NAME = "travel";

    public static final String GUEST_DOC_ID = "user::guest";
    public static final String GUEST_DOC_TYPE = "bookmarkedhotels";

    static class ActiveQuery {
        final Query query;
        final ListenerToken token;

        public ActiveQuery(Query query, ListenerToken token) {
            this.query = query;
            this.token = token;
        }
    }

    private final DBExecutor exec;

    // Used only from the executor thread!
    private final Set<ActiveQuery> activeQueries = new HashSet<>();

    private Database database;

    @Inject
    public LocalStore(@Nonnull DBExecutor exec) {
        this.exec = exec;

        CouchbaseLite.init();
        Database.log.getConsole().setLevel(LogLevel.DEBUG);
    }

    public void openAsGuest(@Nonnull Consumer<Boolean> listener) {
        exec.submit(
            this::openAsGuestAsync,
            (ok) -> listener.accept(true),
            (e) -> listener.accept(false));
    }

    public void openWithValidation(
        @Nonnull String username,
        @Nonnull char[] password,
        @Nonnull Consumer<Boolean> listener) {
        exec.submit(
            () -> openWithValidationAsync(username, password),
            (ok) -> listener.accept(true),
            (e) -> listener.accept(false));
    }

    public void reset() { exec.submit(this::resetAsync); }

    public void close() { exec.submit(this::closeAsync); }

    boolean isOpen() { return database != null; }

    // Call only on the executor thread
    void registerQuery(Query query, ListenerToken token) { activeQueries.add(new ActiveQuery(query, token)); }

    // This is synchronous!  Don't use it from the Swing thread!
    Database getDatabase() {
        if (database == null) { throw new IllegalStateException("db used before open"); }
        return database;
    }

    @Nullable
    private Void resetAsync() {
        for (ActiveQuery activeQuery: activeQueries) { activeQuery.query.removeChangeListener(activeQuery.token); }
        return null;
    }

    @Nullable
    private Void closeAsync() throws CouchbaseLiteException {
        resetAsync();
        database.close();
        database = null;
        return null;
    }

    private boolean openAsGuestAsync() throws CouchbaseLiteException {
        final DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(GUEST_DATABASE_DIR);
        database = new Database(DATABASE_NAME, config);
        return true;
    }

    private boolean openWithValidationAsync(@Nonnull String username, @Nonnull char[] password)
        throws CouchbaseLiteException {
        try {
            final DatabaseConfiguration config = new DatabaseConfiguration();
            config.setDirectory(username);
            database = new Database(DATABASE_NAME, config);
        }
        finally {
            Arrays.fill(password, (char) 0);
        }

        return true;
    }
}
