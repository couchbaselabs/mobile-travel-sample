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

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.DefaultListModel;

import com.couchbase.travelsample.db.DbManager;
import com.couchbase.travelsample.db.FlightsDao;
import com.couchbase.travelsample.model.Flight;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.BookingsView;
import com.couchbase.travelsample.ui.view.FlightSearchView;
import com.couchbase.travelsample.ui.view.HotelSearchView;


@Singleton
public final class BookingsController extends PageController {
    private static final Logger LOGGER = Logger.getLogger(BookingsController.class.getName());


    @Nonnull
    private final DefaultListModel<Flight> flightsModel = new DefaultListModel<>();

    @Nonnull
    private final FlightsDao flightsDao;

    private String sessionId;

    @Inject
    public BookingsController(@Nonnull Nav nav, @Nonnull DbManager localStore, @Nonnull FlightsDao flightsDao) {
        super(BookingsView.PAGE_NAME, nav, localStore);
        this.flightsDao = flightsDao;
    }

    @Nonnull
    public DefaultListModel<Flight> getFlightsModel() { return flightsModel; }

    public void selectFlight() { toPage(FlightSearchView.PAGE_NAME); }

    public void selectHotel() { toPage(HotelSearchView.PAGE_NAME); }

    @Override
    protected void onClose() {
        final String id = sessionId;
        sessionId = null;
        localStore.endSession(id);
        flightsModel.clear();
    }

    public void fetchBookedFlights() {
        if (sessionId != null) { throw new IllegalStateException("Session already exists: " + sessionId); }
        sessionId = localStore.startSession();
        flightsDao.getBookedFlights(sessionId, this::updateFlights);
    }

    public void deleteBooking(@Nullable Flight flight) {
        if (flight == null) { return; }
        flightsDao.deleteBookedFlight(flight);
        flightsModel.removeElement(flight);
    }

    private void updateFlights(@Nullable List<Flight> flights) {
        if (sessionId == null) { return; }
        flightsModel.clear();
        if (flights == null) { return; }
        for (Flight flight : flights) { flightsModel.addElement(flight); }
    }
}
