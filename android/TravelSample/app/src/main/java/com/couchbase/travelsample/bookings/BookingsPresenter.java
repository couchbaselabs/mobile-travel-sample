package com.couchbase.travelsample.bookings;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.util.DatabaseManager;


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
        String userName = DatabaseManager.getSharedInstance().getCurrentUser();
        Query query = QueryBuilder
            .select(SelectResult.expression(Expression.property("flights")))
            .from(DataSource.database(database)).where(Expression.property("username")
                .equalTo(Expression.string(userName)));
        query.addChangeListener(new QueryChangeListener() {

            @Override
            public void changed(QueryChange change) {
                List<Map<String, Object>> data = new ArrayList<>();
                for (Result row : change.getResults()) {
                    Array flights = row.getArray("flights");
                    if (flights != null) {
                        for (Object flight : flights) {
                            MutableDictionary flightInfo = ((com.couchbase.lite.Dictionary) flight).toMutable();
                            Map<String, Object> properties = new HashMap<String, Object>();
                            properties.put("name", flightInfo.getString("name"));
                            properties.put(
                                "journey",
                                String.format(
                                    "%s - %s, Fare: $%.2f",
                                    flightInfo.getString("sourceairport"),
                                    flightInfo.getString("destinationairport"),
                                    flightInfo.getDouble("price")));

                            data.add(properties);
                        }
                    }
                }
                mBookingsView.showBookings(data);
            }
        });

        try {
            query.execute();
        }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

}
