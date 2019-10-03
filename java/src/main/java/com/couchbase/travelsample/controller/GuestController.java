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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.travelsample.model.GuestModel;
import com.couchbase.travelsample.model.HotelModel;
import com.couchbase.travelsample.view.GuestView;


@Singleton
public final class GuestController {
    private final GuestModel guestModel;
    private final HotelModel hotelModel;
    private final GuestView guestView;

    private List<Map<String, Object>> hotels; // List of hotels fetched from python server
    private boolean isFetchingBookmarks;
    private List<Dictionary> bookmarks;

    @Inject
    public GuestController(GuestModel guestModel, HotelModel hotelModel, GuestView guestView) {
        this.guestModel = guestModel;
        this.hotelModel = hotelModel;

        this.guestView = guestView;
        guestView.getGuestHotelSearchButton().addActionListener(e -> guestHotelSearchButtonPressed());
        guestView.getBookmarkHotelButton().addActionListener(e -> bookmarkHotelButtonPressed());
        guestView.getDeleteBookmarkButton().addActionListener(e -> deleteBookmarkButtonPressed());
    }

    private void fetchBookmarks() {
        if (isFetchingBookmarks) { return; }

        isFetchingBookmarks = true;

        // update ui to display bookmarks
        guestModel.getBookmarks(change -> {
            guestView.clearBookmarks();
            List<Dictionary> bookmarks = new ArrayList<>();
            ResultSet rs = change.getResults();
            for (Result result : rs) {
                Dictionary hotel = result.getDictionary(1);
                guestView.addBookmark(hotel.getString("name"));
                bookmarks.add(hotel);
            }
            GuestController.this.bookmarks = bookmarks;
            guestView.refreshBookmarkList();
        });
    }

    private void guestHotelSearchButtonPressed() {
        String location = guestView.getGuestHotelLocationInput();
        String description = guestView.getGuestHotelDescriptionInput();
        guestView.clearHotels();
        guestView.setHotelList(guestView.getHotelListModel());
        hotelModel.searchHotelsUsingRest(
            location,
            description,
            (success, hotels) -> {
                GuestController.this.hotels = hotels;
                for (Map<String, Object> hotel : hotels) {
                    guestView.addHotel(hotel.get("name").toString(), hotel.get("address").toString());
                }
                guestView.refreshHotelList();
            });
    }

    // 1. Save the selected Hotel data into the database.
    // 2. Get or create the guest document with id = user::guest.
    //    Document Structure: { type: 'bookmarkedhotels', hotels: [] }
    //    The hotels is an Array of the bookmarked hotel ids
    // 3. Add the selected hotel id to the hotels array and save the guest document.
    // 4. Display the UI indicating that the hotel has been bookmarked.
    private void bookmarkHotelButtonPressed() {
        int index = guestView.getHotelList().getSelectedIndex();
        Map<String, Object> hotel = hotels.get(index);
        try {
            guestModel.bookmarkHotel(hotel);
            displayBookmarkedNotification(3000);
        }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void displayBookmarkedNotification(long delayMs) {
        guestView.getBookmarkNotification().setVisible(true);
        new Timer().schedule(
            new TimerTask() {
                @Override
                public void run() { guestView.getBookmarkNotification().setVisible(false); }
            },
            delayMs);
    }

    private void deleteBookmarkButtonPressed() {
        int index = guestView.getBookmarkList().getSelectedIndex();
        Dictionary bookmark = bookmarks.get(index);
        String hotelId = bookmark.getString("id");

        try { guestModel.removeBookmark(hotelId); }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
