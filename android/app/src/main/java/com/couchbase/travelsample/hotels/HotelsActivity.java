package com.couchbase.travelsample.hotels;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.util.ResultAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.couchbase.travelsample.R.string.hotels;

public class HotelsActivity extends AppCompatActivity implements HotelsContract.View {

    private HotelsContract.UserActionsListener mActionListener;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);

        mRecyclerView = findViewById(R.id.hotelsList);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);

        ResultAdapter mResultAdapter = new ResultAdapter(new ArrayList<String>());
        mResultAdapter.setOnItemClickListener(new ResultAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Log.d("App", String.valueOf(position));
            }
        });
        mRecyclerView.setAdapter(mResultAdapter);

        mActionListener = new HotelsPresenter(this);
        mActionListener.fetchHotels();

        mActionListener = new HotelsPresenter(this);
    }

    @Override
    public void showHotels(JSONArray data) {
        final List<String> hotels = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            try {
                hotels.add(data.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ResultAdapter adapter = new ResultAdapter(hotels);
                adapter.setOnItemClickListener(new ResultAdapter.OnItemClickListener() {
                    @Override
                    public void OnClick(View view, int position) {
                        Log.d("App", String.valueOf(position));
                    }
                });
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.invalidate();
            }
        });
    }
}
