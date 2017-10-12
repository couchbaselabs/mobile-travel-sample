package com.couchbase.travelsample.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.bookings.BookingsActivity;
import com.couchbase.travelsample.bookmarks.BookmarksActivity;
import com.couchbase.travelsample.searchflight.SearchFlightActivity;
import com.couchbase.travelsample.util.DatabaseManager;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseManager.getSharedInstance(getApplicationContext());
        DatabaseManager.startPushAndPullReplicationForCurrentUser("demo", "password");
    }



    public void onGuestLoginTapped(View view) {
        Intent intent = new Intent(getApplicationContext(), BookmarksActivity.class);
        startActivity(intent);
    }

    public void onLoginTapped(View view) {
        Intent intent = new Intent(getApplicationContext(), BookingsActivity.class);
        startActivity(intent);
    }
}
