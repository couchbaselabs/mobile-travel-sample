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

import java.util.ArrayList;
import java.util.List;

public class SearchFlightActivity extends AppCompatActivity implements SearchFlightContract.View {

    private SearchFlightContract.UserActionsListener mActionListener;
    private ResultAdapter mResultAdapter;
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

        mResultAdapter = new ResultAdapter(new ArrayList<String>());
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

class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private List<String> mResultSet;

    ResultAdapter(List<String> mResultSet) {
        this.mResultSet = mResultSet;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(android.R.layout.simple_selectable_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mResultSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mResultSet.size();
    }
}
