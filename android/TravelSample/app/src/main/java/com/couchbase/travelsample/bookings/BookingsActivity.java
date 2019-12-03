package com.couchbase.travelsample.bookings;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.hotels.HotelsActivity;
import com.couchbase.travelsample.searchflight.SearchFlightActivity;
import com.couchbase.travelsample.util.ResultAdapter;

import java.util.List;
import java.util.Map;

public class BookingsActivity extends AppCompatActivity implements BookingsContract.View {

    private RecyclerView mRecyclerView;
    private BookingsContract.UserActionsListener mActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_search_flights);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchFlightActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.bookingsList);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);

        mActionListener = new BookingsPresenter(this);
        mActionListener.fetchUserBookings();
    }

    public void onHotelsTapped(View view) {
        Intent intent = new Intent(getApplicationContext(), HotelsActivity.class);
        startActivity(intent);
    }

    @Override
    public void showBookings(List<Map<String, Object>> bookings) {
        ResultAdapter adapter = new ResultAdapter(bookings, "name", "journey");
        adapter.setOnItemClickListener(new ResultAdapter.OnItemListener() {
            @Override
            public void OnClick(View view, int position) {

            }

            @Override
            public void OnSwipe(int position) {

            }
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.invalidate();
    }

    public void onFlightSearchTap(View view) {
        Intent intent = new Intent(getApplicationContext(), SearchFlightActivity.class);
        startActivity(intent);
    }

}
