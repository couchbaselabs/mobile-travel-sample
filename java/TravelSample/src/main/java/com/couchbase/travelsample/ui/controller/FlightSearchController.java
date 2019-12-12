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
package com.couchbase.travelsample.ui.controller;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.DefaultListModel;

import com.couchbase.travelsample.db.DbManager;
import com.couchbase.travelsample.db.FlightsDao;
import com.couchbase.travelsample.model.Flight;
import com.couchbase.travelsample.net.TryCb;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.BookingsView;
import com.couchbase.travelsample.ui.view.FlightSearchView;


@Singleton
public final class FlightSearchController extends PageController {
    private static final Logger LOGGER = Logger.getLogger(FlightSearchController.class.getName());

    @Nonnull
    private final DefaultListModel<Flight> outboundFlightsModel = new DefaultListModel<>();
    @Nonnull
    private final DefaultListModel<Flight> returningFlightsModel = new DefaultListModel<>();

    private final FlightsDao flightDao;
    private final TryCb tryCb;

    @Inject
    public FlightSearchController(
        @Nonnull Nav nav,
        @Nonnull DbManager localStore,
        @Nonnull FlightsDao flightDao,
        @Nonnull TryCb tryCb) {
        super(FlightSearchView.PAGE_NAME, nav, localStore);
        this.flightDao = flightDao;
        this.tryCb = tryCb;
    }

    @Nonnull
    public DefaultListModel<Flight> getOutboundFlightsModel() { return outboundFlightsModel; }

    @Nonnull
    public DefaultListModel<Flight> getReturningFlightsModel() { return returningFlightsModel; }

    public void searchAirports(@Nonnull String prefix, int maxResults, @Nonnull Consumer<List<String>> listener) {
        flightDao.searchAirports(prefix, maxResults, listener);
    }

    public void searchFlights(
        @Nonnull String origin,
        @Nonnull String destination,
        @Nonnull Date departureDate,
        @Nonnull Date returnDate) {
        tryCb.searchFlights(origin, destination, departureDate, this::displayOutboundFlights);
        tryCb.searchFlights(destination, origin, returnDate, this::displayReturningFlights);
    }

    public void bookFlights(@Nonnull Flight outboundFlight, @Nonnull Flight returningFlight) {
        flightDao.bookFlight(outboundFlight);
        flightDao.bookFlight(returningFlight);
        back();
    }

    @Override
    protected void onClose() { }

    void displayOutboundFlights(List<Flight> flights) {
        outboundFlightsModel.clear();
        for (Flight flight : flights) { outboundFlightsModel.addElement(flight); }
    }

    void displayReturningFlights(List<Flight> flights) {
        returningFlightsModel.clear();
        for (Flight flight : flights) { returningFlightsModel.addElement(flight); }
    }

    public void done() { back(); }
}
