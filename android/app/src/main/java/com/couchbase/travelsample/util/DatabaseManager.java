package com.couchbase.travelsample.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Expression;
import com.couchbase.lite.FullTextIndexItem;
import com.couchbase.lite.Index;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;

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
    private static String dbName;
    public static String mPythonWebServerEndpoint = "http://10.0.2.2:8080/api/";
    private static String mSyncGatewayEndpoint = "blip://10.0.2.2:4984/travel-sample";

    protected DatabaseManager(Context context, boolean isGuest) {
        if (isGuest) {
            DatabaseConfiguration config = new DatabaseConfiguration(context);
            File folder = new File(String.format("%s/guest", context.getFilesDir()));
            config.setDirectory(folder);
            try {
                database = new Database("travel-sample", config);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        } else {
            File dbFile = new File(context.getFilesDir(), "travel-sample.cblite2");
            if (!dbFile.exists()) {
                DatabaseManager.installPrebuiltDatabase(context, "travel-sample.cblite2.zip");
            }
            DatabaseConfiguration config = new DatabaseConfiguration(context);
            try {
                database = new Database("travel-sample", config);
                createFTSQueryIndex();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void installPrebuiltDatabase(Context context, String filename) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(filename);
            File outFile = new File(context.getFilesDir(), filename);
            FileOutputStream out = new FileOutputStream(outFile);
            copyFile(inputStream, out);
            unpackZip(context.getFilesDir().getPath() + "/", filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFTSQueryIndex() {
        try {
            database.createIndex("descFTSIndex", Index.fullTextIndex(FullTextIndexItem.property("description")));
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private static boolean unpackZip(String path, String zipname)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                // zapis do souboru
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static void startPushAndPullReplicationForCurrentUser(String username, String password) {
        URI url = null;
        try {
            url = new URI(mSyncGatewayEndpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ReplicatorConfiguration config = new ReplicatorConfiguration(database, url);
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

    public static DatabaseManager getSharedInstance(Context context, boolean isGuest) {
        if (instance == null) {
            instance = new DatabaseManager(context, isGuest);
        }
        return instance;
    }

    public static Database getDatabase() {
        if (instance == null) {

        }
        return database;
    }
}
