package com.couchbase.travelsample.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
