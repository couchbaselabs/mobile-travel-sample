package com.couchbase.travelsample.hotels;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.util.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HotelsPresenter implements HotelsContract.UserActionsListener {

    private OkHttpClient client = new OkHttpClient();

    private final HotelsContract.View mHotelView;

    public HotelsPresenter(@NonNull HotelsContract.View mHotelView) {
        this.mHotelView = mHotelView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void fetchHotels() {
        String backendUrl = "http://10.0.2.2:8080/api/";
        String fullPath = "hotel/%2A/France";
        URL url = null;
        try {
            url = new URL(backendUrl + fullPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        Request request = new Request.Builder()
            .url(url)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String responseString = responseBody.string();
                    System.out.println(responseString);
                    JSONArray hotels = new JSONObject(responseString).getJSONArray("data");
                    mHotelView.showHotels(hotels);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void bookmarkHotels(JSONObject hotel) {
        Database database = DatabaseManager.getDatabase();

        Query searchQuery = Query
            .select(SelectResult.expression(Expression.meta().getId()))
            .from(DataSource.database(database))
            .where(
                Expression.property("type").equalTo("bookmarkedhotels")
            );

        /*
         {
         "type" : "bookmarkedhotelss"
         "hotels":["hotel1","hotel2"]
         }
        */

        ResultSet rows = null;
        try {
            rows = searchQuery.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return;
        }

        Document document = null;
        Result row = null;
        while ((row = rows.next()) != null) {
            document = database.getDocument(row.getString("_id"));
            Log.d("APP", document.toString());
        }

        if (document == null) {
            document = new Document();
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("type", "bookmarkedhotels");
            properties.put("hotels", new ArrayList<String>());
            document.set(properties);
            try {
                database.save(document);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }

        /* Get current list of hotels */
        Array hotelIds = null;
        try {
            hotelIds = document
                .getArray("hotels")
                .addString(hotel.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        document.setArray("hotels", hotelIds);

        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
