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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.DefaultListModel;

import com.couchbase.travelsample.db.BookmarkDao;
import com.couchbase.travelsample.db.DbManager;
import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.GuestView;
import com.couchbase.travelsample.ui.view.HotelSearchView;


@Singleton
public final class GuestController extends PageController {
    private static final Logger LOGGER = Logger.getLogger(GuestController.class.getName());

    @Nonnull
    private final DefaultListModel<Hotel> bookmarks = new DefaultListModel<>();

    @Nonnull
    private final BookmarkDao bookmarkDao;

    @Inject
    public GuestController(@Nonnull Nav nav, @Nonnull DbManager localStore, @Nonnull BookmarkDao bookmarkDao) {
        super(GuestView.PAGE_NAME, nav, localStore);
        this.bookmarkDao = bookmarkDao;
    }

    @Nonnull
    public DefaultListModel<Hotel> getBookmarksModel() { return bookmarks; }

    public void fetchBookmarks() { bookmarkDao.getBookmarks(this::updateBookmarks); }

    public void addBookmarks(@Nonnull Set<Hotel> hotels) {
        bookmarkDao.addBookmarks(hotels);
        fetchBookmarks();
    }

    public void deleteBookmark(@Nonnull Set<Hotel> hotels) {
        bookmarkDao.removeBookmarks(hotels);
        for (Hotel hotel : hotels) { bookmarks.removeElement(hotel); }
    }

    public void selectHotel() { toPage(HotelSearchView.PAGE_NAME); }

    @Override
    protected void onClose() { }

    private void updateBookmarks(List<Hotel> hotels) {
        bookmarks.clear();
        for (Hotel hotel : hotels) { bookmarks.addElement(hotel); }
    }
}
