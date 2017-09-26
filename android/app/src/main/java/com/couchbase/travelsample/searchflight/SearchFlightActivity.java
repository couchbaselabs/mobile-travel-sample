package com.couchbase.travelsample.searchflight;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.couchbase.travelsample.R;

public class SearchFlightActivity extends AppCompatActivity {

    private SearchFlightContract.UserActionsListener mActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);
    }
}
