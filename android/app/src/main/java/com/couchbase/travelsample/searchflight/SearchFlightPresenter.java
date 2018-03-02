package com.couchbase.travelsample.searchflight;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.util.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SearchFlightPresenter implements SearchFlightContract.UserActionsListener{

    private OkHttpClient client = new OkHttpClient();

    private final SearchFlightContract.View mSearchView;

    public SearchFlightPresenter(@NonNull SearchFlightContract.View mSearchView) {
        this.mSearchView = mSearchView;
    }

    @Override
    public void startsWith(String prefix, String tag) {
        Database database = DatabaseManager.getDatabase();
        Query searchQuery = QueryBuilder
            .select(SelectResult.expression(Expression.property("airportname")))
            .from(DataSource.database(database))
            .where(
                Expression.property("type").equalTo(Expression.string("airport"))
                .and(Expression.property("airportname").like(Expression.string(prefix + "%")))
            );

        ResultSet rows = null;
        try {
            rows = searchQuery.execute();
        } catch (CouchbaseLiteException e) {
            Log.e("app", "Failed to run query", e);
            return;
        }

        Result row;
        List<String> data = new ArrayList<>();
        while ((row = rows.next()) != null) {
            data.add(row.getString("airportname"));
        }
        mSearchView.showAirports(data, tag);
    }

    @Override
    public void saveFlight(List<JSONObject> flights) {
        String docId = "user::demo";
        Database database = DatabaseManager.getDatabase();
        MutableDocument document = database.getDocument(docId).toMutable();
        MutableArray bookings = document.getArray("flights");
        if (bookings == null) {
            bookings = new MutableArray();
        }
        for (int i = 0; i < flights.size(); i++) {
            HashMap<String, Object> properties = new HashMap<>();
            try {
                properties.put("date", flights.get(i).getString("utc"));
                properties.put("destinationairport", flights.get(i).getString("destinationairport"));
                properties.put("flight", flights.get(i).getString("flight"));
                properties.put("price", flights.get(i).getDouble("price"));
                properties.put("name", flights.get(i).getString("name"));
                properties.put("sourceairport", flights.get(i).getString("sourceairport"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bookings.addDictionary(new MutableDictionary(properties));
        }
        document.setArray("flights", bookings);
        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void fetchFlights(String origin, String destination, String from, String to) {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final JSONArray[] outbounds = {null};
        final JSONArray[] inbounds = {new JSONArray()};
        List<List<JSONObject>> journeys = new ArrayList<>();

        String backendUrl = DatabaseManager.mPythonWebServerEndpoint;

        final String outbound;
        try {
            outbound = String.format("flightPaths/%s/%s?leave=%s",
                URLEncoder.encode(origin, "UTF-8").replace("+", "%20"),
                URLEncoder.encode(destination, "UTF-8").replace("+", "%20"),
                from);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        URL outboundURL = null;
        try {
            outboundURL = new URL(backendUrl + outbound);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        Request outboundRequest = new Request.Builder()
            .url(outboundURL)
            .build();

        client.newCall(outboundRequest).enqueue(new Callback() {
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
                    outbounds[0] = new JSONObject(responseString).getJSONArray("data");
                    countDownLatch.countDown();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        String inbound = null;
        try {
            inbound = String.format("flightPaths/%s/%s?leave=%s",
                URLEncoder.encode(destination, "UTF-8").replace("+", "%20"),
                URLEncoder.encode(origin, "UTF-8").replace("+", "%20"),
                to);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        URL inboundURL = null;
        try {
            inboundURL = new URL(backendUrl + inbound);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        Request inboundRequest = new Request.Builder()
            .url(inboundURL)
            .build();

        client.newCall(inboundRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String responseString = responseBody.string();
                    inbounds[0] = new JSONObject(responseString).getJSONArray("data");
                    countDownLatch.countDown();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            countDownLatch.await();
            for (int i = 0; i < outbounds[0].length(); i++) {
                for (int j = 0; j < inbounds[0].length(); j++) {
                    List<JSONObject> journey = new ArrayList<JSONObject>();
                    try {
                        journey.add(outbounds[0].getJSONObject(i));
                        journey.add(inbounds[0].getJSONObject(j));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    journeys.add(journey);
                }
            }
            mSearchView.showFlights(journeys);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}