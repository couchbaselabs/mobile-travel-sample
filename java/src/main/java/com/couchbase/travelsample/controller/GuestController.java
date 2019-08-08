package com.couchbase.travelsample.controller;

import com.couchbase.travelsample.view.GuestView;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GuestController implements ViewController {

    private GuestView guestView;

    private final static Logger LOGGER = Logger.getLogger(GuestView.class.getName());

    public GuestController() {
        guestView = new GuestView();
        guestView.getGuestHotelSearchButton().addActionListener(event -> guestHotelSearchButtonPressed());
    }

    public void show() {
        guestView.show();
    }

    public void hide() {
        guestView.hide();
    }

    public void dispose() {
        guestView.dispose();
    }

    private void guestHotelSearchButtonPressed() {
        String guestHotelLocationInputText = guestView.getGuestHotelLocationInput();
        String guestHotelDescriptionInputText = guestView.getGuestHotelDescriptionInput();
        LOGGER.log(Level.INFO, "Guest location input: " + guestHotelLocationInputText);
        LOGGER.log(Level.INFO, "Guest description input: " + guestHotelDescriptionInputText);
    }
}
