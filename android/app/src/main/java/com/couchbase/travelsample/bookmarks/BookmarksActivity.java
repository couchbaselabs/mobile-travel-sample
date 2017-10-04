package com.couchbase.travelsample.bookmarks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.couchbase.travelsample.R;
import com.couchbase.travelsample.hotels.HotelsActivity;
import com.couchbase.travelsample.util.ResultAdapter;

import org.json.JSONArray;

import java.util.ArrayList;

public class BookmarksActivity extends AppCompatActivity implements BookmarksContract.View {

    private BookmarksContract.UserActionsListener mActionListener;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HotelsActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.bookmarksList);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);

        ResultAdapter mResultAdapter = new ResultAdapter(new ArrayList<String>());
        mResultAdapter.setOnItemClickListener(new ResultAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Log.d("App", String.valueOf(position));
            }
        });
        mRecyclerView.setAdapter(mResultAdapter);

        mActionListener = new BookmarksPresenter(this);
        mActionListener.fetchBookmarks();
    }

    @Override
    public void showBookmarks(JSONArray bookmarks) {

    }
}
