package com.couchbase.travelsample.searchflight;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SearchFlightPresenter implements SearchFlightContract.UserActionsListener{

    private final OkHttpClient client = new OkHttpClient();

    private final SearchFlightContract.View mSearchView;

    private FlightFetcher flightFetcher;

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
            mSearchView.displayError(e.toString());
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
        Document document = database.getDocument(docId);

        if (document == null) {
            Log.e("app", "User not created. Make sure you create user via web app!!");
            mSearchView.displayError("User not created. Make sure you create user via web app!!");
            return;
        }
        else {
            MutableDocument mutableCopy = database.getDocument(docId).toMutable();
            MutableArray bookings = mutableCopy.getArray("flights");
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
            mutableCopy.setArray("flights", bookings);
            try {
                database.save(mutableCopy);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fetchFlights(String origin, String destination, String from, String to) {
        if (flightFetcher != null) { return; }

        flightFetcher = new FlightFetcher(origin, destination, from, to);

        if (!flightFetcher.fetchFlights()) {
            onFlightsFetched();
        }
    }

    void onFlightsFetched() {
        FlightFetcher fetcher = flightFetcher;
        flightFetcher = null;
        mSearchView.showFlights(fetcher.getJourneys());
    }

    private class FlightFetcher {
        private final String backendUrl = DatabaseManager.mPythonWebServerEndpoint;
        private final Handler handler = new Handler();

        private final String origin;
        private final String destination;
        private final String from;
        private final String to;

        private JSONArray outboundResponse;
        private JSONArray inboundResponse;

        private int requestsOutstanding;

        private List<List<JSONObject>> journeys;

        @MainThread
        public FlightFetcher(String origin, String destination, String from, String to) {
            this.origin = origin;
            this.destination = destination;
            this.from = from;
            this.to = to;
        }

        public synchronized List<List<JSONObject>> getJourneys() {
            return journeys;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public boolean fetchFlights() {
            synchronized (this) {
                journeys = new ArrayList<>();
            }
            outboundResponse = new JSONArray();
            inboundResponse = new JSONArray();
            requestsOutstanding = 2;

            try {
                return scheduleOutbound() && scheduleInbound();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        private boolean scheduleOutbound() throws IOException {
            final String outbound;
            try {
                outbound = String.format(
                    "flightPaths/%s/%s?leave=%s",
                    URLEncoder.encode(origin, "UTF-8").replace("+", "%20"),
                    URLEncoder.encode(destination, "UTF-8").replace("+", "%20"),
                    from);
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
            URL outboundURL = null;
            try {
                outboundURL = new URL(backendUrl + outbound);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            }

            Request outboundRequest = new Request.Builder()
                .url(outboundURL)
                .build();

            client.newCall(outboundRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    responseReceived();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) { throw new IOException("Unexpected code " + response); }
                        String responseString = responseBody.string();
                        System.out.println(responseString);
                        outboundResponse = new JSONObject(responseString).getJSONArray("data");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        responseReceived();
                    }
                }
            });
            return true;
        }

        private boolean scheduleInbound() throws IOException {
            String inbound = null;
            try {
                inbound = String.format(
                    "flightPaths/%s/%s?leave=%s",
                    URLEncoder.encode(destination, "UTF-8").replace("+", "%20"),
                    URLEncoder.encode(origin, "UTF-8").replace("+", "%20"),
                    to);
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
            URL inboundURL = null;
            try {
                inboundURL = new URL(backendUrl + inbound);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            }

            Request inboundRequest = new Request.Builder()
                .url(inboundURL)
                .build();

            client.newCall(inboundRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    responseReceived();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) { throw new IOException("Unexpected code " + response); }
                        String responseString = responseBody.string();
                        inboundResponse = new JSONObject(responseString).getJSONArray("data");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        responseReceived();
                    }
                }
            });

            return true;
        }

        synchronized void responseReceived() {
            if (--requestsOutstanding > 0) { return; }

            for (int i = 0; i < outboundResponse.length(); i++) {
                for (int j = 0; j < inboundResponse.length(); j++) {
                    List<JSONObject> journey = new ArrayList<JSONObject>();
                    try {
                        journey.add(outboundResponse.getJSONObject(i));
                        journey.add(inboundResponse.getJSONObject(j));
                        journeys.add(journey);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    onFlightsFetched();
                }
            });
        }
    }

}