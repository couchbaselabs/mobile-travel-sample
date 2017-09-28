package com.couchbase.travelsample.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.couchbase.lite.Database;
import com.couchbase.travelsample.R;
import com.couchbase.travelsample.searchflight.SearchFlightActivity;
import com.couchbase.travelsample.util.DatabaseManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseManager.installPrebuiltDatabase(getApplicationContext(), "travel-sample.cblite2.zip");
        DatabaseManager.getSharedInstance(getApplicationContext());
        Database database = DatabaseManager.getDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_search_flights);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchFlightActivity.class);
                startActivity(intent);
            }
        });
    }
}
