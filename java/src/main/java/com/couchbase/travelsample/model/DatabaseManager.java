package com.couchbase.travelsample.model;

import com.couchbase.lite.*;

public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;
    public String currentUser = null;

    private static String dbName;

    protected DatabaseManager() {

    }

    public void OpenGuestDatabase() {
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
        if (instance == null) {

        }
        return database;
    }

    private static void enableLogging() {
        Database.log.getFile().setConfig(new LogFileConfiguration(Configuration.LOG_DIR));
        Database.log.getFile().setLevel(LogLevel.INFO);
    }
}
