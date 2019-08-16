package com.couchbase.travelsample.model;

import com.couchbase.lite.*;

public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;

    private DatabaseManager() {
        CouchbaseLite.init();
    }

    public void OpenGuestDatabase() {
        Database.log.getConsole().setLevel(LogLevel.INFO);

        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(Configuration.GUEST_DATABASE_DIR);
        try {
            database = new Database(Configuration.DATABASE_NAME, config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getSharedInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public static Database getDatabase() {
        return database;
    }
}
