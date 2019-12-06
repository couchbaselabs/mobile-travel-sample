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
package com.couchbase.travelsample.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.couchbase.travelsample.model.Hotel;


public class RemoteStore {
    private final static Logger LOGGER = Logger.getLogger(RemoteStore.class.getName());

    public static final String WEB_APP_ENDPOINT = "http://0.0.0.0:8080/api/";
    public static String SGW_ENDPOINT = "ws://10.0.2.2:4984/travel-sample";

    private final OkHttpClient client;

    @Inject
    public RemoteStore() { client = new OkHttpClient(); }

    public void searchHotels(String location, String description, final Consumer<List<Hotel>> completion) {
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
        try { url = new URL(WEB_APP_ENDPOINT + fullPath); }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        LOGGER.log(Level.INFO, "Query: " + url);


        client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nonnull IOException e) { e.printStackTrace(); }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
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
}
