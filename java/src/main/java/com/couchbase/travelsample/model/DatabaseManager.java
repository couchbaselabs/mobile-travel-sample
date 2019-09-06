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
package com.couchbase.travelsample.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.LogLevel;
import com.couchbase.travelsample.Config;


@Component
public final class DatabaseManager {
    private Database database;

    @Autowired
    public DatabaseManager() { CouchbaseLite.init(); }

    public void openGuestDatabase() {
        Database.log.getConsole().setLevel(LogLevel.INFO);

        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(Config.GUEST_DATABASE_DIR);
        try { database = new Database(Config.DATABASE_NAME, config); }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public Database getDatabase() {
        return database;
    }
}
