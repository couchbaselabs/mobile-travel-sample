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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import com.couchbase.travelsample.db.BookmarkDao;
import com.couchbase.travelsample.db.HotelDao;
import com.couchbase.travelsample.db.LocalStore;
import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.net.RemoteStore;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.GuestView;


@Singleton
public final class GuestController extends BaseController {
    private final BookmarkDao bookmarkDao;
    private final HotelDao hotelDao;

    private final RemoteStore remote;

    private final DefaultListModel<GuestView.HotelElement> hotelListModel = new DefaultListModel<>();
    private final DefaultListModel<GuestView.HotelElement> bookmarkListModel = new DefaultListModel<>();

    private boolean isFetchingHotels;
    private boolean isFetchingBookmarks;

    @Inject
    public GuestController(
        Nav nav,
        LocalStore localStore,
        BookmarkDao bookmarkDao,
        HotelDao hotelDao,
        RemoteStore remote) {
        super(nav, localStore);
        this.bookmarkDao = bookmarkDao;
        this.hotelDao = hotelDao;
        this.remote = remote;
    }

    public DefaultListModel<GuestView.HotelElement> getHotelModel() { return hotelListModel; }

    public DefaultListModel<GuestView.HotelElement> getBookmarkModel() { return bookmarkListModel; }

    public void fetchHotels() {
        if (isFetchingHotels) { return; }
        isFetchingHotels = true;

        // update ui to display hotels
        hotelDao.fetchHotels(this::updateHotels);
    }

    public void fetchBookmarks() {
        if (isFetchingBookmarks) { return; }
        isFetchingBookmarks = true;

        // update ui to display bookmarks
        bookmarkDao.getBookmarks(this::updateBookmarks);
    }

    public void searchHotels(String location, String description) {
        hotelListModel.removeAllElements();
        final DefaultListModel<String> newHotels = new DefaultListModel<>();

        remote.searchHotels(
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
    public void bookmarkHotel(Hotel hotel) {
        bookmarkListModel.addElement(new GuestView.HotelElement(hotel));
        bookmarkDao.addBookmark(hotel);
    }

    public void deleteBookmark(GuestView.HotelElement hotel) {
        bookmarkListModel.removeElement(hotel);
        bookmarkDao.removeBookmark(hotel.hotel);
    }

    private void updateBookmarks(List<Hotel> bookmarks) {
        bookmarkListModel.clear();
        for (Hotel bookmark: bookmarks) { bookmarkListModel.addElement(new GuestView.HotelElement(bookmark)); }
        isFetchingBookmarks = false;
    }

    private void updateHotels(List<Hotel> hotels) {
        hotelListModel.clear();
        for (Hotel hotel: hotels) { hotelListModel.addElement(new GuestView.HotelElement(hotel)); }
    }
}
