//
// Copyright (c) 2019 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.travelsample.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.FullTextExpression;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.TravelSample;


@Singleton
public final class HotelModel {
    private final DatabaseManager dbMgr;
    private final OkHttpClient client = new OkHttpClient();

    @Inject
    public HotelModel(DatabaseManager dbMgr) { this.dbMgr = dbMgr; }

    public void searchHotelsUsingRest(String location, String description, final Consumer<List<Hotel>> completion) {
        String fullPath;
        try {
            String descriptionStr = description.equals("")
                ? "*"
                : URLEncoder.encode(description, "UTF-8").replace("+", "%20");
            String locationStr = location.equals("")
                ? "*"
                : URLEncoder.encode(location, "UTF-8").replace("+", "%20");
            fullPath = String.format("hotel/%s/%s", descriptionStr, locationStr);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        URL url;
        try { url = new URL(TravelSample.WEB_APP_ENDPOINT + fullPath); }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) { throw new IOException("Unexpected code " + response); }
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) { throw new IOException("Empty response"); }
                    String responseString = responseBody.string();
                    JSONArray data = new JSONObject(responseString).getJSONArray("data");

                    List<Hotel> hotels = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        hotels.add(Hotel.fromJSON(data.getJSONObject(i)));
                    }

                    completion.accept(hotels);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void searchHotels(String location, String description, Consumer<List<Hotel>> completion) {
        Database database = dbMgr.getDatabase();

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
            .where(Expression.property("type").equalTo(Expression.string("hotel")).and(searchExp));

        try {
            ResultSet rows = hotelSearchQuery.execute();

            List<Hotel> hotels = new ArrayList<>();
            Result row;
            while ((row = rows.next()) != null) {
                hotels.add(Hotel.fromDictionary(row.getDictionary("travel-sample")));
            }

            completion.accept(hotels);
        }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}