package com.couchbase.travelsample.hotels;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.util.ResultAdapter;

import java.util.List;
import java.util.Map;

public class HotelsActivity extends AppCompatActivity implements HotelsContract.View {

    private HotelsContract.UserActionsListener mActionListener;
    private RecyclerView mRecyclerView;
    private EditText mLocationInput;
    private EditText mDescriptionInput;
    private Boolean IS_GUEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);

        mRecyclerView = findViewById(R.id.hotelsList);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);

        mActionListener = new HotelsPresenter(this);

        IS_GUEST = getIntent().getBooleanExtra(getString(R.string.guest_field), false);

        mLocationInput = (EditText) findViewById(R.id.locationInput);
        mLocationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String location = editable.toString();
                searchHotels(location, mDescriptionInput.getText().toString());
            }
        });

        mDescriptionInput = (EditText) findViewById(R.id.descriptionInput);
        mDescriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String description = editable.toString();
                searchHotels(mLocationInput.getText().toString(), description);
            }
        });
    }

    void searchHotels(String location, String description) {
        if (IS_GUEST) {
            mActionListener.fetchHotels(location, description);
        } else {
            mActionListener.queryHotels(location, description);
        }
    }

    @Override
    public void showHotels(final List<Map<String, Object>> hotels) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ResultAdapter adapter = new ResultAdapter(hotels, "name", "address");
                adapter.setOnItemClickListener(new ResultAdapter.OnItemListener() {
                    @Override
                    public void OnClick(View view, int position) {
                        if (IS_GUEST) {
                            Map<String, Object> selectedHotel = hotels.get(position);
                            mActionListener.bookmarkHotels(selectedHotel);
                            finish();
                        }
                    }

                    @Override
                    public void OnSwipe(int position) {

                    }
                });
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.invalidate();
            }
        });
    }
}
