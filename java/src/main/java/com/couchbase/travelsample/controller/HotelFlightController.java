package com.couchbase.travelsample.controller;

import com.couchbase.travelsample.view.HotelFlightView;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelFlightController {

    private HotelFlightView hotelFlightView;
    private final static Logger LOGGER = Logger.getLogger(HotelFlightView.class.getName());

    public HotelFlightController() {
        hotelFlightView = new HotelFlightView();
        hotelFlightView.getHotelSearchButton().addActionListener(event -> hotelSearchButtonPressed());
        hotelFlightView.getFlightSearchButton().addActionListener(event -> flightSearchButtonPressed());
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
