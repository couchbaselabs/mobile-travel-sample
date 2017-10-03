package com.couchbase.travelsample.hotels;

import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.couchbase.travelsample.R.string.hotels;

public class HotelsPresenter implements HotelsContract.UserActionsListener {

    private OkHttpClient client = new OkHttpClient();

    private final HotelsContract.View mHotelView;

    public HotelsPresenter(@NonNull HotelsContract.View mHotelView) {
        this.mHotelView = mHotelView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void fetchHotels() {
        String backendUrl = "http://0.0.0.0:8080/api/";
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
                    JSONArray hotels = new JSONObject(responseString).getJSONArray("data");
                    mHotelView.showHotels(hotels);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
