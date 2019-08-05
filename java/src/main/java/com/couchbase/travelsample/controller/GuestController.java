package com.couchbase.travelsample.controller;

import com.couchbase.travelsample.model.GuestModel;
import com.couchbase.travelsample.view.GuestView;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GuestController {

    private GuestModel guestModel;
    private GuestView guestView;
    private final static Logger LOGGER = Logger.getLogger(GuestView.class.getName());

    public GuestController(GuestModel model, GuestView view) {
        guestModel = model;
        guestView = view;
        this.initController();
    }

    public void initController() {
        guestView.getGuestHotelSearchButton().addActionListener(event -> guestHotelSearchButtonPressed());
    }

    private void guestHotelSearchButtonPressed() {
        String guestHotelLocationInputText = guestView.getGuestHotelLocationInput();
        String guestHotelDescriptionInputText = guestView.getGuestHotelDescriptionInput();
        LOGGER.log(Level.INFO, "Guest location input: " + guestHotelLocationInputText);
        LOGGER.log(Level.INFO, "Guest description input: " + guestHotelDescriptionInputText);
    }
}
