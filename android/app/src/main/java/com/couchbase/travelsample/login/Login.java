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
    // Minimum acceleration needed to count as a shake movement
    private static final int MIN_SHAKE_ACCELERATION = 5;

    // Minimum number of movements to register a shake
    private static final int MIN_MOVEMENTS = 2;

    // Maximum time (in milliseconds) for the whole shake to occur
    private static final int MAX_SHAKE_DURATION = 10000;

    // Arrays to store gravity and linear acceleration values
    private float[] mGravity = { 0.0f, 0.0f, 0.0f };
    private float[] mLinearAcceleration = { 0.0f, 0.0f, 0.0f };

    // Indexes for x, y, and z values
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private SensorManager mSensorManager;
    // Start time for the shake detection
    long startTime = 0;

    // Counter for shake movements
    int moveCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        /* do this in onCreate */
        mSensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Shake Gesture
         mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

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


    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {

            // This method will be called when the accelerometer detects a change.

            // Call a helper method that wraps code from the Android developer site
            setCurrentAcceleration(se);

            // Get the max linear acceleration in any direction
            float maxLinearAcceleration = getMaxCurrentLinearAcceleration();

            // Check if the acceleration is greater than our minimum threshold
            if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
                long now = System.currentTimeMillis();

                // Set the startTime if it was reset to zero
                if (startTime == 0) {
                    startTime = now;
                }

                long elapsedTime = now - startTime;

                // Check if we're still in the shake window we defined
                if (elapsedTime > MAX_SHAKE_DURATION) {
                    // Too much time has passed. Start over!
                    resetShakeDetection();
                }
                else {
                    // Keep track of all the movements
                    moveCount++;

                    // Check if enough movements have been made to qualify as a shake
                    if (moveCount > MIN_MOVEMENTS) {
                        // It's a shake! Show an alert to accept SGW and web server endpoint

                        // Reset for the next one!
                        resetShakeDetection();
                 //       showAlertForAddressEntry();


                    }
                }
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        // Shake Gesture Recognition Related
        private void setCurrentAcceleration(SensorEvent event) {
        /*
         *  BEGIN SECTION from Android developer site. This code accounts for
         *  gravity using a high-pass filter
         */

            // alpha is calculated as t / (t + dT)
            // with t, the low-pass filter's time-constant
            // and dT, the event delivery rate

            final float alpha = 0.8f;

            // Gravity components of x, y, and z acceleration
            mGravity[X] = alpha * mGravity[X] + (1 - alpha) * event.values[X];
            mGravity[Y] = alpha * mGravity[Y] + (1 - alpha) * event.values[Y];
            mGravity[Z] = alpha * mGravity[Z] + (1 - alpha) * event.values[Z];

            // Linear acceleration along the x, y, and z axes (gravity effects removed)
            mLinearAcceleration[X] = event.values[X] - mGravity[X];
            mLinearAcceleration[Y] = event.values[Y] - mGravity[Y];
            mLinearAcceleration[Z] = event.values[Z] - mGravity[Z];

        /*
         *  END SECTION from Android developer site
         */
        }

        private float getMaxCurrentLinearAcceleration() {
            // Start by setting the value to the x value
            float maxLinearAcceleration = mLinearAcceleration[X];

            // Check if the y value is greater
            if (mLinearAcceleration[Y] > maxLinearAcceleration) {
                maxLinearAcceleration = mLinearAcceleration[Y];
            }

            // Check if the z value is greater
            if (mLinearAcceleration[Z] > maxLinearAcceleration) {
                maxLinearAcceleration = mLinearAcceleration[Z];
            }

            // Return the greatest value
            return maxLinearAcceleration;
        }

        private void resetShakeDetection() {
            startTime = 0;
            moveCount = 0;
        }



    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
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
