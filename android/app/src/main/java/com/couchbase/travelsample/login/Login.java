package com.couchbase.travelsample.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.bookings.BookingsActivity;
import com.couchbase.travelsample.bookmarks.BookmarksActivity;
import com.couchbase.travelsample.util.DatabaseManager;

public class Login extends AppCompatActivity {

    EditText usernameInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

    }



    public void onGuestLoginTapped(View view) {
        DatabaseManager.getSharedInstance(getApplicationContext(), true);

        Intent intent = new Intent(getApplicationContext(), BookmarksActivity.class);
        startActivity(intent);
    }

    public void onLoginTapped(View view) {
        DatabaseManager.getSharedInstance(getApplicationContext(), false);
        DatabaseManager.startPushAndPullReplicationForCurrentUser(usernameInput.getText().toString(),
            passwordInput.getText().toString());

        Intent intent = new Intent(getApplicationContext(), BookingsActivity.class);
        startActivity(intent);
    }
}
