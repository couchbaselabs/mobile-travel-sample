package com.couchbase.travelsample.searchflight;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.couchbase.travelsample.R;

import static com.couchbase.travelsample.R.id.fromInput;

public class SearchFlightActivity extends AppCompatActivity {

    private SearchFlightContract.UserActionsListener mActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);

        mActionListener = new SearchFlightPresenter();

        EditText fromInput = (EditText) findViewById(R.id.fromInput);
        fromInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String prefix = editable.toString();
                mActionListener.startsWith(prefix);
            }
        });
    }
}
