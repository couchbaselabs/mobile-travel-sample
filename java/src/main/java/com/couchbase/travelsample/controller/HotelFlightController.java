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
package com.couchbase.travelsample.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.couchbase.travelsample.view.HotelFlightView;

@Component
public class HotelFlightController {
    private final static Logger LOGGER = Logger.getLogger(HotelFlightView.class.getName());

    private final HotelFlightView hotelFlightView;

    @Autowired
    public HotelFlightController(HotelFlightView hotelFlightView) {
        this.hotelFlightView = hotelFlightView;

        hotelFlightView.getHotelSearchButton().addActionListener(e -> hotelSearchButtonPressed());
        hotelFlightView.getFlightSearchButton().addActionListener(e -> flightSearchButtonPressed());
    }

    public void show() {
        hotelFlightView.show();
    }

    public void hide() {
        hotelFlightView.hide();
    }

    public void dispose() {
        hotelFlightView.dispose();
    }

    private void hotelSearchButtonPressed() {
        String hotelLocationInputText = hotelFlightView.getHotelLocationInput();
        String hotelDescriptionInputText = hotelFlightView.getHotelDescriptionInput();
        LOGGER.log(Level.INFO, "Location input: " + hotelLocationInputText);
        LOGGER.log(Level.INFO, "Description input: " + hotelDescriptionInputText);
    }

    private void flightSearchButtonPressed() {
        String flightOriginInputText = hotelFlightView.getFlightOriginInput();
        String flightDestinationInputText = hotelFlightView.getFlightDestinationInput();
        LOGGER.log(Level.INFO, "Origin input: " + flightOriginInputText);
        LOGGER.log(Level.INFO, "Destination input: " + flightDestinationInputText);

        String flightOriginInputDate = hotelFlightView.getFlightOriginDateInput();
        String flightDestinationInputDate = hotelFlightView.getFlightDestinationDateInput();
        LOGGER.log(Level.INFO, "Origin date input: " + flightOriginInputDate);
        LOGGER.log(Level.INFO, "Destination date input: " + flightDestinationInputDate);
    }
}
