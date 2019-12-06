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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.couchbase.travelsample.db.LocalStore;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.HotelFlightView;


@Singleton
public final class HotelFlightController extends PageController {
    private final static Logger LOGGER = Logger.getLogger(HotelFlightView.class.getName());

    @Inject
    public HotelFlightController(Nav nav, LocalStore localStore) {
        super(nav, localStore);
    }

    public void hotelSearchButtonPressed(String hotelLocation, String hotelDesc) {
        LOGGER.log(Level.INFO, "Location: " + hotelLocation);
        LOGGER.log(Level.INFO, "Description: " + hotelDesc);
    }

    public void flightSearchButtonPressed(
        String flightOrigin,
        String flightDest,
        String flightOriginDate,
        String flightDestDate) {
        LOGGER.log(Level.INFO, "Origin: " + flightOrigin);
        LOGGER.log(Level.INFO, "Origin date input: " + flightOriginDate);
        LOGGER.log(Level.INFO, "Destination: " + flightDest);
        LOGGER.log(Level.INFO, "Destination date: " + flightDestDate);
    }
}
