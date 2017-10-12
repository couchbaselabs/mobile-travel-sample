package com.couchbase.travelsample.bookings;

import android.support.annotation.NonNull;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Query;
import com.couchbase.lite.ReadOnlyArray;
import com.couchbase.lite.ReadOnlyDictionary;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.util.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Listens to user actions from the UI, retrieves the data and updates the UI as required.
 */
public class BookingsPresenter implements BookingsContract.UserActionsListener {

    private final BookingsContract.View mBookingsView;

    public BookingsPresenter(@NonNull BookingsContract.View bookingsView) {
        this.mBookingsView = bookingsView;
    }

    @Override
    public void fetchUserBookings() {
        Database database = DatabaseManager.getDatabase();

        Query query = Query
            .select(SelectResult.expression(Expression.property("flights")))
            .from(DataSource.database(database))
            .where(
                Expression.property("username").equalTo("demo")
            );

        ResultSet rows = null;
        try {
            rows = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        List<String> data = new ArrayList<>();
        Result row = null;
        while ((row = rows.next()) != null) {
            ReadOnlyArray flights = row.getArray("flights");
            for (Object flight : flights) {
                ReadOnlyDictionary flightInfo = (ReadOnlyDictionary) flight;
                String name = flightInfo.getString("name");
                String sourceairport = flightInfo.getString("sourceairport");
                String destinationairport = flightInfo.getString("destinationairport");

                data.add(String.format("%s -> %s", sourceairport, destinationairport));
            }
        }
        mBookingsView.showBookings(data);
    }

}
