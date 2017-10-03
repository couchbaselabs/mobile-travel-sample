package com.couchbase.travelsample.searchflight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.util.ResultAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchFlightActivity extends AppCompatActivity implements SearchFlightContract.View {

    private SearchFlightContract.UserActionsListener mActionListener;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);

        mRecyclerView = findViewById(R.id.resultList);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);

        ResultAdapter mResultAdapter = new ResultAdapter(new ArrayList<String>());
        mRecyclerView.setAdapter(mResultAdapter);

        mActionListener = new SearchFlightPresenter(this);

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

    @Override
    public void showAirports(List<String> airports) {
        mRecyclerView.setAdapter(new ResultAdapter(airports));
        mRecyclerView.invalidate();
    }
}
