package com.couchbase.travelsample.searchflight;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.util.DatePickerFragment;
import com.couchbase.travelsample.util.FlightSearchAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFlightActivity extends AppCompatActivity implements SearchFlightContract.View, DatePickerFragment.DatePickerListener {

    private SearchFlightContract.UserActionsListener mActionListener;
    private RecyclerView mRecyclerView;
    private TextView mDepartTextView;
    private TextView mArriveTextView;
    private AutoCompleteTextView fromInput;
    private AutoCompleteTextView toInput;
    private ArrayAdapter mFromAdapter;
    private ArrayAdapter mToAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);

        mDepartTextView = findViewById(R.id.departDate);
        mArriveTextView = findViewById(R.id.arriveDate);

        mRecyclerView = findViewById(R.id.resultList);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);

        FlightSearchAdapter mResultAdapter = new FlightSearchAdapter(new ArrayList<List<JSONObject>>());
        mRecyclerView.setAdapter(mResultAdapter);

        mActionListener = new SearchFlightPresenter(this);

        fromInput = (AutoCompleteTextView) findViewById(R.id.auto_fromInput);
        mFromAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, new ArrayList<String>());
        fromInput.setAdapter(mFromAdapter);
        fromInput.setThreshold(1);
        fromInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String prefix = editable.toString();
                mActionListener.startsWith(prefix, "from");
            }
        });

        toInput = (AutoCompleteTextView) findViewById(R.id.auto_toInput);
        mToAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, new ArrayList<String>());
        toInput.setAdapter(mToAdapter);
        toInput.setThreshold(1);
        toInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String prefix = editable.toString();
                mActionListener.startsWith(prefix, "to");
            }
        });

    }

    @Override
    public void showAirports(final List<String> airports, final String tag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tag.equals("from")) {
                    mFromAdapter.clear();
                    for (int i = 0; i < airports.size(); i++) {
                        mFromAdapter.add(airports.get(i));
                    }
                    mFromAdapter.notifyDataSetChanged();
                } else {
                    mToAdapter.clear();
                    for (int i = 0; i < airports.size(); i++) {
                        mToAdapter.add(airports.get(i));
                    }
                    mToAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void displayError(String e) {
        Toast toast = Toast.makeText(getApplicationContext(), e,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showDatePicker(View view) {
        Bundle bundle = new Bundle();
        if (R.id.departDate == view.getId()) {
            bundle.putString("tag", "depart");
        } else {
            bundle.putString("tag", "arrive");
        }
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setListener(this);
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSelected(String date, String tag) {
        if (tag.equals("depart")) {
            mDepartTextView.setText(date);
        } else {
            mArriveTextView.setText(date);
        }
    }

    public void onFlightLookupTap(View view) {
        mActionListener.fetchFlights(fromInput.getText().toString(), toInput.getText().toString(), "01/01/2016", "10/01/2016");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(toInput.getWindowToken(), 0);
    }

    @Override
    public void showFlights(final List<List<JSONObject>> flights) {
        FlightSearchAdapter mResultAdapter = new FlightSearchAdapter(flights);
        mRecyclerView.setAdapter(mResultAdapter);
        mResultAdapter.setOnItemClickListener(new FlightSearchAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                mActionListener.saveFlight(flights.get(position));
                finish();
            }
        });
    }

}