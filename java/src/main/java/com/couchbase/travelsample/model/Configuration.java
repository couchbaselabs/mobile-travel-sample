package com.couchbase.travelsample.model;

public class Configuration {
    public static final String LOG_DIR = "logs";
    public static final String DATABASE_DIR = "database";
    public static final String GUEST_DATABASE_DIR = DATABASE_DIR + "/guest";
    public static final String DATABASE_NAME = "travel-sample";
    public static final String PYTHON_WEB_SERVER_ENDPOINT = "http://localhost:8080/api/";
    public static final String SYNC_GATEWAY_ENDPOINT = "ws://localhost:4984/travel-sample";
}
