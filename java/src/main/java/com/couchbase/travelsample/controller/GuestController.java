package com.couchbase.travelsample.controller;

import com.couchbase.travelsample.view.GuestView;
import com.couchbase.travelsample.model.HotelModel;

import java.util.Map;
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
        String location = guestView.getGuestHotelLocationInput();
        String description = guestView.getGuestHotelDescriptionInput();
        LOGGER.log(Level.INFO, "Guest location input: " + location);
        LOGGER.log(Level.INFO, "Guest description input: " + description);
        HotelModel.searchHotelsUsingRest(location, description, (success, hotels) -> {
            System.out.println("Hotels: " + hotels.size());
            for (Map<String, Object> hotel : hotels) {
                guestView.addHotel(hotel.get("name").toString(), hotel.get("address").toString());
            }
            guestView.setHotelList(guestView.getHotelListModel());
        });
    }
}
