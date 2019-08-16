package com.couchbase.travelsample.controller;

import com.couchbase.lite.*;
import com.couchbase.travelsample.model.GuestModel;
import com.couchbase.travelsample.view.GuestView;
import com.couchbase.travelsample.model.HotelModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuestController implements ViewController {

    private GuestView guestView;
    private List<Map<String, Object>> hotels; // List of hotels fetched from python server
    private boolean isFetchingBookmarks;
    private List<Dictionary> bookmarks;

    public GuestController() {
        guestView = new GuestView();
        guestView.getGuestHotelSearchButton().addActionListener(event -> guestHotelSearchButtonPressed());
        guestView.getBookmarkHotelButton().addActionListener(event -> bookmarkHotelButtonPressed());
        guestView.getDeleteBookmarkButton().addActionListener(event -> deleteBookmarkButtonPressed());
    }

    public void show() {
        guestView.show();
        fetchBookmarks();
    }

    public void hide() {
        guestView.hide();
    }

    public void dispose() {
        guestView.dispose();
    }

    private void fetchBookmarks() {
        if (isFetchingBookmarks) { return; }
        isFetchingBookmarks = true;

        //update ui to display bookmarks
        try {
            GuestModel.getBookmarks(new QueryChangeListener() {
                @Override
                public void changed(QueryChange queryChange) {
                    guestView.clearBookmarks();
                    List<Dictionary> bookmarks = new ArrayList<>();
                    ResultSet rs = queryChange.getResults();
                    for (Result result : rs) {
                        Dictionary hotel = result.getDictionary(1);
                        guestView.addBookmark(hotel.getString("name"));
                        bookmarks.add(hotel);
                    }
                    GuestController.this.bookmarks = bookmarks;
                    guestView.refreshBookmarkList();
                }
            });
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void guestHotelSearchButtonPressed() {
        String location = guestView.getGuestHotelLocationInput();
        String description = guestView.getGuestHotelDescriptionInput();

        HotelModel.searchHotelsUsingRest(location, description, (success, hotels) -> {
            this.hotels = hotels;
            for (Map<String, Object> hotel : hotels) {
                guestView.addHotel(hotel.get("name").toString(), hotel.get("address").toString());
            }
            guestView.refreshHotelList();
        });
    }

    private void bookmarkHotelButtonPressed() {
        // 1. Save the selected Hotel data into the database.
        // 2. Get or create the guest document with id = user::guest.
        //    Document Structure: { type: 'bookmarkedhotels', hotels: [] }
        //    The hotels is an Array of the bookmarked hotel ids
        // 3. Add the selected hotel id to the hotels array and save the guest document.
        // 4. Display the UI indicating that the hotel has been bookmarked.
        int index = guestView.getHotelList().getSelectedIndex();
        Map<String, Object> hotel = hotels.get(index);
        try {
            GuestModel.bookmarkHotel(hotel);
            displayBookmarkedNotification(3000);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void displayBookmarkedNotification(long delayMs) {
        guestView.getBookmarkNotification().setVisible(true);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        guestView.getBookmarkNotification().setVisible(false);
                    }
                },
                delayMs
        );
    }

    private void deleteBookmarkButtonPressed() {
        int index = guestView.getBookmarkList().getSelectedIndex();
        Dictionary bookmark = bookmarks.get(index);
        String hotelId = bookmark.getString("id");

        try {
            GuestModel.removeBookmark(hotelId);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
