package com.couchbase.travelsample.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.bookings.BookingsActivity;
import com.couchbase.travelsample.bookmarks.BookmarksActivity;
import com.couchbase.travelsample.util.DatabaseManager;

public class Login extends AppCompatActivity  {

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

    @Override
    protected void onStart() {
        super.onStart();

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


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onShowAddressEntryDialog(View view) {
        // get prompts.xml view
        Log.i("TEMP","Called Alert");
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText sgwText = promptsView
                .findViewById(R.id.editTextDialogUserInput1);

        final EditText webText = promptsView
                .findViewById(R.id.editTextDialogUserInput2);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                DatabaseManager.mSyncGatewayEndpoint = (sgwText.getText() != null) ? sgwText.getText().toString() : "ws://localhost:4984";
                                DatabaseManager.mPythonWebServerEndpoint = (webText.getText() != null) ? webText.getText().toString() + "/api" : "http://localhost:8080/api/";

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
}
