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
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.DefaultListModel;

import com.couchbase.travelsample.db.LocalStore;
import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.net.RemoteStore;
import com.couchbase.travelsample.ui.Nav;


@Singleton
public final class HotelSearchController extends BaseController {
    private final static Logger LOGGER = Logger.getLogger(HotelSearchController.class.getName());

    private final DefaultListModel<Hotel> hotelListModel = new DefaultListModel<>();

    private final RemoteStore remoteStore;

    @Inject
    public HotelSearchController(Nav nav, LocalStore localStore, RemoteStore remoteStore) {
        super(nav, localStore);
        this.remoteStore = remoteStore;
    }

    public DefaultListModel<Hotel> getHotelModel() { return hotelListModel; }

    public void searchHotels(String hotelLocation, String hotelDesc) {
        remoteStore.searchHotels(hotelLocation, hotelDesc, this::displayHotels);
    }

    private void displayHotels(List<Hotel> hotels) {
        hotelListModel.clear();
        for (Hotel hotel : hotels) { hotelListModel.addElement(hotel); }
    }
}
