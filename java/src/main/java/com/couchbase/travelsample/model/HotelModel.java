package com.couchbase.travelsample.model;

import com.couchbase.lite.*;

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

public class HotelModel {

    private static OkHttpClient client = new OkHttpClient();

    public interface Completion {
        public void complete(boolean success, List<Map<String, Object>> result);
    }

    public static void searchHotelsUsingRest(String location, String description, Completion completion) {
        String backendUrl = Configuration.PYTHON_WEB_SERVER_ENDPOINT;
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
                    completion.complete(true, hotels);
                } catch (JSONException e) {
                    e.printStackTrace();
                    completion.complete(false, null);
                }
            }
        });
    }

    public void bookmarkHotels(Map<String, Object> hotel) throws CouchbaseLiteException {
        Database database = DatabaseManager.getDatabase();

        MutableDocument hotelDoc = new MutableDocument((String) hotel.get("id"), hotel);
        database.save(hotelDoc);

        Document document = database.getDocument("user::guest");
        MutableDocument mutableCopy = null;
        if (document == null) {
            mutableCopy = new MutableDocument("user::guest");
            mutableCopy.setString("type", "bookmarkedhotels");
            mutableCopy.setArray("hotels", new MutableArray());
        }
        else {
            mutableCopy = document.toMutable();
        }
        MutableArray hotels = mutableCopy.getArray("hotels").toMutable();
        hotels.addString((String) hotel.get("id"));
        database.save(mutableCopy);
    }

    public void searchHotels(String location, String description, Completion completion) {
        Database database = DatabaseManager.getDatabase();

        Expression descExp = FullTextExpression.index("descFTSIndex").match(description);
        Expression locationExp = Expression.property("country")
                .like(Expression.string("%" + location + "%"))
                .or(Expression.property("city").like(Expression.string("%" + location + "%")))
                .or(Expression.property("state").like(Expression.string("%" + location + "%")))
                .or(Expression.property("address").like(Expression.string("%" + location + "%")));

        Expression searchExp = descExp.and(locationExp);
        Query hotelSearchQuery = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.database(database))
                .where(
                        Expression.property("type").equalTo(Expression.string("hotel"))
                                .and(searchExp)
                );

        try {
            ResultSet rows = hotelSearchQuery.execute();
            List<Map<String, Object>> hotels = new ArrayList<Map<String, Object>>();
            Result row = null;
            while ((row = rows.next()) != null) {
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("name", row.getDictionary("travel-sample").getString("name"));
                properties.put("address", row.getDictionary("travel-sample").getString("address"));
                hotels.add(properties);
            }
            completion.complete(true, hotels);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            completion.complete(false, null);
        }
    }
}