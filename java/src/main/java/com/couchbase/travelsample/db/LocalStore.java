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

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.LogLevel;


@Singleton
public final class LocalStore {
    public static final String DATABASE_DIR = "database";
    public static final String GUEST_DATABASE_DIR = DATABASE_DIR + "/guest";
    public static final String DATABASE_NAME = "travel";

    public static final String GUEST_DOC_ID = "user::guest";
    public static final String GUEST_DOC_TYPE = "bookmarkedhotels";

    private final DBExecutor exec;

    private Database database;

    @Inject
    public LocalStore(DBExecutor exec) {
        this.exec = exec;

        CouchbaseLite.init();
        Database.log.getConsole().setLevel(LogLevel.DEBUG);
    }

    public void openAsGuest() { exec.submit(this::openAsGuestAsync); }

    public void openWithValidation(String username, String password, Consumer<Boolean> listener) {
        exec.submit(
            () -> openWithValidationAsync(username),
            (ok) -> listener.accept(true),
            (e) -> listener.accept(false));
    }

    Database getDatabase() {
        if (database == null) { throw new IllegalStateException("db used before open"); }
        return database;
    }

    private boolean openWithValidationAsync(String username) throws CouchbaseLiteException {
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(username);
        database = new Database(DATABASE_NAME, config);
        return true;
    }

    @Nullable
    private Void openAsGuestAsync() throws CouchbaseLiteException {
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(GUEST_DATABASE_DIR);
        database = new Database(DATABASE_NAME, config);
        return null;
    }
}
