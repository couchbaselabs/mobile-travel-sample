package com.couchbase.travelsample.util;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO
 */
public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;

    protected DatabaseManager(Context context) {
        DatabaseConfiguration config = new DatabaseConfiguration(context);
        try {
            database = new Database("travel-sample", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getSharedInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    public static Database getDatabase() {
        if (instance == null) {

        }
        return database;
    }
}
