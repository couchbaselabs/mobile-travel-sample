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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

import com.couchbase.travelsample.model.Flight;
import com.couchbase.travelsample.model.Hotel;


public class TryCb {
    private static final Logger LOGGER = Logger.getLogger(TryCb.class.getName());

    public static final String WEB_APP_ENDPOINT = "http://127.0.0.1:8080/api/";
    public static final String HOTEL_ENDPOINT = WEB_APP_ENDPOINT + "hotel/";
    public static final String FLIGHT_PATHS_ENDPOINT = WEB_APP_ENDPOINT + "flightPaths/";


    @Nonnull
    private final DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    @FunctionalInterface
    public interface JsonArrayConverter<T> {
        List<T> convert(@Nonnull JSONArray json);
    }


    private final OkHttpClient client;

    @Inject
    public TryCb() { client = new OkHttpClient(); }

    public void searchHotels(
        @Nonnull String location,
        @Nonnull String description,
        @Nonnull Consumer<List<Hotel>> receiver) {
        runQuery(
            HOTEL_ENDPOINT + encodePath(description) + "/" + encodePath(location),
            receiver,
            json -> {
                List<Hotel> flights = new ArrayList<>();
                for (int i = 0; i < json.length(); i++) { flights.add(Hotel.fromJSON(json.getJSONObject(i))); }
                return flights;
            });
    }

    public void searchFlights(
        @Nonnull String origin,
        @Nonnull String destination,
        @Nonnull Date date,
        @Nonnull Consumer<List<Flight>> receiver) {
        runQuery(
            FLIGHT_PATHS_ENDPOINT + encodePath(origin) + "/" + encodePath(destination)
                + "?leave=" + formatter.format(date),
            receiver,
            json -> {
                List<Flight> flights = new ArrayList<>();
                for (int i = 0; i < json.length(); i++) { flights.add(Flight.fromJSON(json.getJSONObject(i))); }
                return flights;
            });
    }

    private <T> void runQuery(
        @Nonnull String urlStr,
        @Nonnull Consumer<List<T>> receiver,
        @Nonnull JsonArrayConverter<T> converter) {

        final URL url;
        try { url = new URL(urlStr); }
        catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Malformed URL: " + urlStr, e);
            receiver.accept(Collections.emptyList());
            return;
        }

        LOGGER.log(Level.INFO, "query: " + url);
        client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
                throw new RuntimeException("Request failed", e);
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
                if (!response.isSuccessful()) { throw new IOException("Unsuccessful request: " + response); }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) { throw new IOException("Empty response"); }

                    List<T> data = converter.convert(new JSONObject(responseBody.string()).getJSONArray("data"));

                    SwingUtilities.invokeLater(() -> receiver.accept(data));
                }
            }
        });
    }

    private String encodePath(@Nonnull String segment) {
        if (!segment.isEmpty()) {
            try { return URLEncoder.encode(segment, "UTF-8").replace("+", "%20"); }
            catch (UnsupportedEncodingException e) {
                LOGGER.log(Level.WARNING, "Failed encoding : " + segment, e);
            }
        }
        return "*";
    }
}
