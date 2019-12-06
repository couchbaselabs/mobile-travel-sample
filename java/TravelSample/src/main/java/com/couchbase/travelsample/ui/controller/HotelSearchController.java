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
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.DefaultListModel;

import com.couchbase.travelsample.db.LocalStore;
import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.net.Remote;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.GuestView;
import com.couchbase.travelsample.ui.view.HotelSearchView;


@Singleton
public final class HotelSearchController extends PageController {
    private final static Logger LOGGER = Logger.getLogger(HotelSearchController.class.getName());

    @Nonnull
    private final DefaultListModel<Hotel> hotelsModel = new DefaultListModel<>();

    private final Remote remoteStore;

    @Inject
    public HotelSearchController(@Nonnull Nav nav, @Nonnull LocalStore localStore, @Nonnull Remote remoteStore) {
        super(nav, localStore);
        this.remoteStore = remoteStore;
    }

    @Nonnull
    public DefaultListModel<Hotel> getHotelModel() { return hotelsModel; }

    public void searchHotels(@Nonnull String hotelLocation, @Nonnull String hotelDesc) {
        remoteStore.searchHotels(hotelLocation, hotelDesc, this::displayHotels);
    }

    public void done() { toPage(HotelSearchView.PAGE_NAME, GuestView.PAGE_NAME); }

    private void displayHotels(@Nonnull List<Hotel> hotels) {
        hotelsModel.clear();
        for (Hotel hotel : hotels) { hotelsModel.addElement(hotel); }
    }
}
