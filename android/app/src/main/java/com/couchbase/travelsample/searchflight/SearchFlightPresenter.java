package com.couchbase.travelsample.searchflight;

import android.support.annotation.NonNull;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.util.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class SearchFlightPresenter implements SearchFlightContract.UserActionsListener{

    private final SearchFlightContract.View mSearchView;

    public SearchFlightPresenter(@NonNull SearchFlightContract.View mSearchView) {
        this.mSearchView = mSearchView;
    }

    @Override
    public void startsWith(String prefix) {
        Database database = DatabaseManager.getDatabase();
        Query searchQuery = Query
            .select(SelectResult.expression(Expression.property("airportname")))
            .from(DataSource.database(database))
            .where(
                Expression.property("type").equalTo("airport")
                .and(Expression.property("faa").equalTo(prefix.toUpperCase()))
            );

        ResultSet rows = null;
        try {
            rows = searchQuery.run();
        } catch (CouchbaseLiteException e) {
            Log.e("app", "Failed to run query", e);
            return;
        }

        Result row;
        List<String> data = new ArrayList<>();
        while ((row = rows.next()) != null) {
            data.add(row.getString("airportname"));
        }
        mSearchView.showAirports(data);
    }

    @Override
    public void saveFlight(String title, String description) {

    }

}