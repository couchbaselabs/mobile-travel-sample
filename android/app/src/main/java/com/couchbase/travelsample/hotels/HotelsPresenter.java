package com.couchbase.travelsample.hotels;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void fetchHotels(String location, String description) {
        String backendUrl = DatabaseManager.mPythonWebServerEndpoint;
        String fullPath = null;
        try {
            String descriptionStr = description.equals("") ? "*" : URLEncoder.encode(description, "UTF-8").replace("+", "%20");
            String locationStr = location.equals("") ? "*" : URLEncoder.encode(location, "UTF-8").replace("+", "%20");
            fullPath = String.format("hotel/%s/%s", descriptionStr, locationStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
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
                    JSONArray data = new JSONObject(responseString).getJSONArray("data");
                    List<Map<String, Object>> hotels = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i < data.length(); i++) {
                        try {
                            Map<String, Object> properties = new HashMap<>();
                            properties.put("name", data.getJSONObject(i).getString("name"));
                            properties.put("id", data.getJSONObject(i).getString("id"));
                            properties.put("address", data.getJSONObject(i).getString("address"));
                            hotels.add(properties);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    mHotelView.showHotels(hotels);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void bookmarkHotels(Map<String, Object> hotel) {
        Database database = DatabaseManager.getDatabase();

        Document hotelDoc = new Document((String) hotel.get("id"));
        hotelDoc.set(hotel);
        try {
            database.save(hotelDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        /* 2. Look-up Guest user document. */
        Document document = database.getDocument("user::guest");
        if (document == null) {
            document = new Document("user::guest");
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("type", "bookmarkedhotels");
            properties.put("hotels", new ArrayList<>());
            document.set(properties);
        }

        document
            .getArray("hotels")
            .addString((String) hotel.get("id"));

        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryHotels(String location, String description) {
        Database database = DatabaseManager.getDatabase();

        Expression descExp = Expression.property("description").match(description);

        Expression locationExp = Expression.property("country")
            .like("%" + location + "%")
            .or(Expression.property("city").like("%" + location + "%"))
            .or(Expression.property("state").like("%" + location + "%"))
            .or(Expression.property("address").like("%" + location + "%"));

        Expression searchExp = descExp.and(locationExp);

        Query hotelSearchQuery = Query
            .select(SelectResult.all())
            .from(DataSource.database(database))
            .where(
                Expression.property("type").equalTo("hotel")
                .and(searchExp)
            );

        ResultSet rows = null;
        try {
            rows = hotelSearchQuery.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return;
        }

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Result row = null;
        while((row = rows.next()) != null) {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("name", row.getDictionary("travel-sample").getString("name"));
            properties.put("address", row.getDictionary("travel-sample").getString("address"));
            data.add(properties);
        }
        mHotelView.showHotels(data);
    }
}
