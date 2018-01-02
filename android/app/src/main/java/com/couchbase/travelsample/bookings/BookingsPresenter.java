package com.couchbase.travelsample.bookings;

import android.support.annotation.NonNull;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.lite.query.QueryChange;
import com.couchbase.lite.query.QueryChangeListener;
import com.couchbase.travelsample.util.DatabaseManager;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
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

        query.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {
                ResultSet rows = change.getRows();

                List<Map<String, Object>> data = new ArrayList<>();
                Result row = null;
                while ((row = rows.next()) != null) {
                    Array flights = row.getArray("flights");
                    if (flights != null) {
                        for (Object flight : flights) {
                            MutableDictionary flightInfo = (MutableDictionary) flight;
                            Map<String, Object> properties = new HashMap<String, Object>();
                            properties.put("name", flightInfo.getString("name"));
                            properties.put("journey", String.format("%s - %s", flightInfo.getString("sourceairport"), flightInfo.getString("destinationairport")));

                            data.add(properties);
                        }
                    }
                }
                mBookingsView.showBookings(data);
            }
        });

        try {
            query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

}
