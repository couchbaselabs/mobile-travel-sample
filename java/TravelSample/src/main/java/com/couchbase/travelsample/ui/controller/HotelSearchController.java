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
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.DefaultListModel;

import com.couchbase.travelsample.db.DbManager;
import com.couchbase.travelsample.db.HotelsDao;
import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.net.TryCb;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.HotelSearchView;


@Singleton
public final class HotelSearchController extends PageController {
    private static final Logger LOGGER = Logger.getLogger(HotelSearchController.class.getName());


    @Nonnull
    private final TryCb remote;
    @Nonnull
    private final HotelsDao hotelsDao;
    @Nonnull
    private final DefaultListModel<Hotel> hotelsModel = new DefaultListModel<>();

    private String prevPage;

    @Inject
    public HotelSearchController(
        @Nonnull Nav nav,
        @Nonnull DbManager localStore,
        @Nonnull TryCb remoteStore,
        @Nonnull HotelsDao hotelsDao) {
        super(HotelSearchView.PAGE_NAME, nav, localStore);
        this.remote = remoteStore;
        this.hotelsDao = hotelsDao;
    }

    @Nonnull
    public DefaultListModel<Hotel> getHotelModel() { return hotelsModel; }

    public void searchHotels(@Nonnull String hotelLocation, @Nonnull String hotelDesc) {
        if (localStore.isLoggedIn()) {
            hotelsDao.searchHotels(hotelLocation, hotelDesc, this::displayHotels);
        }
        else {
            remote.searchHotels(hotelLocation, hotelDesc, this::displayHotels);
        }
    }

    public void done() { back(); }

    @Override
    protected void onClose() { hotelsModel.clear(); }

    private void displayHotels(@Nullable List<Hotel> hotels) {
        hotelsModel.clear();
        if (hotels == null) { return; }
        for (Hotel hotel : hotels) { hotelsModel.addElement(hotel); }
    }
}
