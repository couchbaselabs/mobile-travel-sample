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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.travelsample.model.GuestModel;
import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.model.HotelModel;


@Singleton
public final class GuestController {

    private final DefaultListModel<String> hotelListModel = new DefaultListModel<>();
    private final DefaultListModel<String> bookmarkListModel = new DefaultListModel<>();

    private final GuestModel guestModel;
    private final HotelModel hotelModel;

    private List<Hotel> hotels; // List of hotels fetched from python server
    private boolean isFetchingBookmarks;
    private List<Dictionary> bookmarks;

    @Inject
    public GuestController(GuestModel guestModel, HotelModel hotelModel) {
        this.guestModel = guestModel;
        this.hotelModel = hotelModel;
    }

    public DefaultListModel<String> getHotelModel() { return hotelListModel; }

    public DefaultListModel<String> getBookmarkModel() { return bookmarkListModel; }

    public void searchHotels(String location, String description) {
        hotelListModel.removeAllElements();
        final DefaultListModel<String> newHotels = new DefaultListModel<>();

        hotelModel.searchHotelsUsingRest(
            location,
            description,
            (hotels) -> SwingUtilities.invokeLater(() -> updateHotels(hotels)));
    }

    // 1. Save the selected Hotel data into the database.
    // 2. Get or create the guest document with id = user::guest.
    //    Document Structure: { type: 'bookmarkedhotels', hotels: [] }
    //    The hotels is an Array of the bookmarked hotel ids
    // 3. Add the selected hotel id to the hotels array and save the guest document.
    // 4. Display the UI indicating that the hotel has been bookmarked.
    public void bookmarkHotel(int index) {
        Hotel hotel = hotels.get(index);
        try { guestModel.bookmarkHotel(hotel); }
        catch (CouchbaseLiteException e) { e.printStackTrace(); }
    }

    public void deleteBookmark(int index) {
        Dictionary bookmark = bookmarks.get(index);
        String hotelId = bookmark.getString("id");

        try { guestModel.removeBookmark(hotelId); }
        catch (CouchbaseLiteException e) { e.printStackTrace(); }
    }

    public void fetchBookmarks() {
        if (isFetchingBookmarks) { return; }

        isFetchingBookmarks = true;

        // update ui to display bookmarks
        guestModel.getBookmarks(this::updateBookmarks);
    }

    private void updateBookmarks(QueryChange change) {
        bookmarkListModel.clear();
        ResultSet results = change.getResults();
        for (Result result : results) {
            bookmarkListModel.addElement(result.getDictionary(1).getString("name"));
        }
        isFetchingBookmarks = false;
    }

    private void updateHotels(List<Hotel> hotels) {
        hotelListModel.clear();
        for (Hotel hotel : hotels) {
            hotelListModel.addElement("- " + hotel.getName() + " on " + hotel.getAddress());
        }
    }

    private void refreshBookmarks(List<Hotel> hotels) {
        hotelListModel.clear();
        for (Hotel hotel : hotels) {
            hotelListModel.addElement("- " + hotel.getName() + " on " + hotel.getAddress());
        }
    }
}
