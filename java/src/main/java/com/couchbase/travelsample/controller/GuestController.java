package com.couchbase.travelsample.controller;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.travelsample.view.GuestView;
import com.couchbase.travelsample.model.HotelModel;

import java.util.List;
import java.util.Map;

public class GuestController implements ViewController {

    private GuestView guestView;
    private String selectedHotel;
    private String selectedBookmark;
    private int selectedHotelIndex;


    public GuestController() {
        guestView = new GuestView();
        guestView.getGuestHotelSearchButton().addActionListener(event -> guestHotelSearchButtonPressed());
        guestView.getBookmarkHotelButton().addActionListener(event -> bookmarkHotelButtonPressed());
        guestView.getDeleteBookmarkButton().addActionListener(event -> deleteBookmarkButtonPressed());
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
        guestView.deleteAllHotel();
        HotelModel.searchHotelsUsingRest(location, description, (success, hotels) -> {
            System.out.println("Hotels: " + hotels.size());
            for (Map<String, Object> hotel : hotels) {
                guestView.initialAddHotel(hotel.get("name").toString(), hotel.get("address").toString());
            }
            guestView.setHotelList(guestView.getHotelListModel());
        });
    }

    private void bookmarkHotelButtonPressed() {
        selectedHotel = guestView.getHotelList().getSelectedValue();
        selectedHotelIndex = guestView.getHotelList().getSelectedIndex();
        guestView.addBookmark(selectedHotel);
        guestView.setBookmarkList(guestView.getBookmarkListModel());
        guestView.deleteHotel(selectedHotel);
        guestView.setHotelList(guestView.getHotelListModel());
        guestView.getBookmarkNotification().setVisible(true);

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        guestView.getBookmarkNotification().setVisible(false);
                    }
                },
                3000
        );
    }

    private void deleteBookmarkButtonPressed() {
        selectedBookmark = guestView.getBookmarkList().getSelectedValue();
        guestView.deleteBookmark(selectedBookmark);
        guestView.setBookmarkList(guestView.getBookmarkListModel());
        if (guestView.getHotelListModel() != null) {
            guestView.addHotel(selectedHotelIndex, selectedBookmark);
            guestView.setHotelList(guestView.getHotelListModel());
        }
    }
}
