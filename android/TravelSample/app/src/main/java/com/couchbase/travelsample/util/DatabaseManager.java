package com.couchbase.travelsample.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.provider.ContactsContract;
import android.util.Log;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.FullTextIndexItem;
import com.couchbase.lite.IndexBuilder;
import com.couchbase.lite.LogFileConfiguration;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * TODO
 */
public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;
    private Context appContext = null;
    public  String currentUser = null;

    private static String dbName;
    public static String mPythonWebServerEndpoint = "http://10.0.2.2:8080/api/";
    public static String mSyncGatewayEndpoint = "ws://10.0.2.2:4984/travel-sample";


    protected DatabaseManager() {

    }
    public void initCouchbaseLite(Context context) {
        CouchbaseLite.init(context);
        appContext = context;

    }

    public void OpenGuestDatabase() {
        DatabaseConfiguration config = new DatabaseConfiguration();

        config.setDirectory(String.format("%s/guest", appContext.getFilesDir()));

        try {
            database = new Database("guest", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
    public void OpenDatabaseForUser(String username) {
        File dbFile = new File(appContext.getFilesDir()+"/"+ username, "travel-sample.cblite2");
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(String.format("%s/%s", appContext.getFilesDir(),username));
        currentUser = username;

        if (!dbFile.exists()) {
            AssetManager assetManager = appContext.getAssets();
            try {
                File path = new File(appContext.getFilesDir()+"");
                unzip(assetManager.open("travel-sample.cblite2.zip"),path);
                Database.copy(new File(appContext.getFilesDir(),"travel-sample.cblite2"), "travel-sample", config);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

        }
        try {
            database = new Database("travel-sample", config);
            createFTSQueryIndex();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    public String getCurrentUserDocId() {
        return "user::" + currentUser;
    }

    private void createFTSQueryIndex() {
        try {
            database.createIndex("descFTSIndex", IndexBuilder.fullTextIndex(FullTextIndexItem.property("description")));
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private static void unzip(InputStream in, File destination) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(in);
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            String fileName = ze.getName();
            File newFile = new File(destination, fileName);
            if (ze.isDirectory()) {
                newFile.mkdirs();
            } else {
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        in.close();
    }

    public static void startPushAndPullReplicationForCurrentUser(String username, String password) {
        URI url = null;
        try {
            url = new URI(mSyncGatewayEndpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ReplicatorConfiguration config = new ReplicatorConfiguration(database, new URLEndpoint(url));
        config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        config.setContinuous(true);
        config.setAuthenticator(new BasicAuthenticator(username, password));

        Replicator replicator = new Replicator(config);
        replicator.addChangeListener(new ReplicatorChangeListener() {
            @Override
            public void changed(ReplicatorChange change) {

                if (change.getReplicator().getStatus().getActivityLevel().equals(Replicator.ActivityLevel.IDLE)) {

                    Log.e("Replication Comp Log", "Schedular Completed");

                }
                if (change.getReplicator().getStatus().getActivityLevel().equals(Replicator.ActivityLevel.STOPPED) || change.getReplicator().getStatus().getActivityLevel().equals(Replicator.ActivityLevel.OFFLINE)) {
                    // stopReplication();
                    Log.e("Rep schedular  Log", "ReplicationTag Stopped");
                }
            }
        });
        replicator.start();
    }

//
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

    private static void enableLogging(Context context) {
        // Only in 2.5
        final File path = context.getCacheDir();

        Database.log.getFile().setConfig(new LogFileConfiguration(path.toString()));
        Database.log.getFile().setLevel(LogLevel.INFO);
        Log.e("log",path.toString());
    }
}
