//
// Copyright (c) 2019 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.travelsample.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.model.Flight;
import com.couchbase.travelsample.model.Trip;


public class FlightsDao {
    private static final Logger LOGGER = Logger.getLogger(FlightsDao.class.getName());

    public static final String TYPE_AIRPORT = "airport";

    public static final String PROP_FLIGHTS = "flights";
    public static final String PROP_USER = "username";


    public static final String PROP_AIRPORT_NAME = "airportname";
    public static final String PROP_FAA = "faa";


    @Nonnull
    private final DbManager db;
    @Nonnull
    private final DbExecutor exec;

    @Inject
    public FlightsDao(@Nonnull DbManager db, @Nonnull DbExecutor exec) {
        this.db = db;
        this.exec = exec;
    }

    public void getFlights(@Nonnull Consumer<List<Flight>> listener) {
        exec.submit(this::queryFlightsAsync, listener);
    }

    public void searchAirports(@Nonnull String name, int maxResults, @Nonnull Consumer<List<String>> listener) {
        exec.submit(() -> searchAirportsAsync(name, maxResults), listener);
    }

    public void bookTrip(@Nonnull Trip trip, @Nonnull Consumer<Void> onSuccess, @Nonnull Consumer<Exception> onError) {
        exec.submit(() -> bookTripAsync(trip), onSuccess, onError);
    }

    public void deleteFlight(Flight flight) { exec.submit(() -> deleteFlightAsync(flight)); }

    @Nonnull
    private List<Flight> queryFlightsAsync() throws CouchbaseLiteException {
        final List<Flight> flights = new ArrayList<>();

        final ResultSet results = QueryBuilder
            .select(SelectResult.expression(Expression.property(PROP_FLIGHTS)))
            .from(DataSource.database(db.getDatabase()))
            .where(Expression.property(PROP_USER).equalTo(Expression.string(db.getCurrentUser())))
            .execute();

        final Array flightsArray = results.allResults().get(0).getArray(0);
        final int n = flightsArray.count();
        for (int i = 0; i < n; i++) { flights.add(Flight.fromDictionary(flightsArray.getDictionary(i))); }

        return flights;
    }

    @Nonnull
    private List<String> searchAirportsAsync(@Nonnull String prefix, int maxResults) throws CouchbaseLiteException {
        final String target = "%" + prefix + "%";
        final ResultSet results = QueryBuilder.select(SelectResult
            .expression(Expression.property(PROP_AIRPORT_NAME)))
            .from(DataSource.database(db.getDatabase()))
            .where(Expression.property(DbManager.PROP_DOC_TYPE).equalTo(Expression.string(TYPE_AIRPORT))
                .and(Function.lower(Expression.property(PROP_AIRPORT_NAME))
                    .like(Function.lower(Expression.string(target))))
                .or(Function.lower(Expression.property(PROP_FAA))
                    .like(Function.lower(Expression.string(target)))))
            .orderBy(Ordering.property(PROP_AIRPORT_NAME).ascending())
            .limit(Expression.intValue(maxResults))
            .execute();

        final List<String> airports = new ArrayList<>();
        Result row;
        while ((row = results.next()) != null) { airports.add(row.getString(PROP_AIRPORT_NAME)); }

        return airports;
    }

    Void bookTripAsync(@Nonnull Trip trip) throws CouchbaseLiteException {
        bookFlightAsync(trip.getOutboundFlight(), trip.getDepartureDate());
        bookFlightAsync(trip.getReturnFlight(), trip.getReturnDate());
        return null;
    }

    Void deleteFlightAsync(Flight flight) {
        LOGGER.log(Level.INFO, "Delete Flight not implemented");
        return null;
    }

    private void bookFlightAsync(@Nonnull Flight flight, @Nonnull Date date) throws CouchbaseLiteException {
        final MutableDocument userDoc = db.getUserDoc();

        MutableArray bookings = userDoc.getArray(PROP_FLIGHTS);
        if (bookings == null) { bookings = new MutableArray(); }
        bookings.addDictionary(Flight.toDictionary(flight));

        userDoc.setArray(PROP_FLIGHTS, bookings);
        db.getDatabase().save(userDoc);
    }
}
