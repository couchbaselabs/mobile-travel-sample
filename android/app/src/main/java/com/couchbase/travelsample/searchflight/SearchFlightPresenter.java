package com.couchbase.travelsample.searchflight;

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

public class SearchFlightPresenter implements SearchFlightContract.UserActionsListener{

    @Override
    public void startsWith(String prefix) {
        Database database = DatabaseManager.getDatabase();
        Query searchQuery = Query
            .select(SelectResult.expression(Expression.property("airportname")))
            .from(DataSource.database(database))
            .where(
                Expression.property("type").equalTo(Expression.property("airport"))
                .and(Expression.property("faa").equalTo(prefix.toUpperCase()))
            );

        ResultSet rows = null;
        try {
            rows = searchQuery.run();
        } catch (CouchbaseLiteException e) {
            Log.e("app", "Failed to run query", e);
        }
        Result row;
        while ((row = rows.next()) != null) {
            Log.d("app", String.format("airport name :: %s", row.getString("airportname")));
        }
    }

    @Override
    public void saveFlight(String title, String description) {

    }
}
