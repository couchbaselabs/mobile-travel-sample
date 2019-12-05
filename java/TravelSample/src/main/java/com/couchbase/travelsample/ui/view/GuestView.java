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
package com.couchbase.travelsample.ui.view;

import java.awt.*;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.ui.controller.GuestController;


@Singleton
public final class GuestView extends Page {
    public static final String PAGE_NAME = "GUEST";

    public static class HotelElement {
        public final Hotel hotel;

        public HotelElement(@Nonnull Hotel hotel) { this.hotel = hotel; }

        @Override
        public String toString() { return "- " + hotel.getName() + " on " + hotel.getAddress(); }
    }


    private final GuestController controller;

    private JPanel panel;

    private JList<HotelElement> bookmarkList;

    private JButton logoutButton;

    private JButton deleteBookmarkButton;
    private JButton addBookmarkButton;

    @Inject
    public GuestView(GuestController controller) {
        super(PAGE_NAME);

        this.controller = controller;

        bookmarkList.setModel(controller.getBookmarkModel());

        logoutButton.addActionListener(e -> controller.logout());

        addBookmarkButton.addActionListener(e -> controller.selectHotel());

        //deleteBookmarkButton.setVisible(false);
        deleteBookmarkButton.addActionListener(e -> controller.deleteBookmark(bookmarkList.getSelectedValue()));
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    public void open() {
        controller.fetchBookmarks();
    }

    @Override
    public void close() {
        controller.close();
    }
//
//    public void openHotels() {
//        controller.fetchHotels();
//    }
//
//
//    private void bookmarkHotel() {
//        controller.bookmarkHotel(hotelList.getSelectedValue().hotel);
//        displayBookmarkedNotification(3000);
//    }
//
//    private void displayBookmarkedNotification(int delayMs) {
//        bookmarkNotification.setVisible(true);
//        new Timer(delayMs, e -> bookmarkNotification.setVisible(false));
//    }
//
//    private void createUIComponents() {
//        locationImage = new JLabel(new ImageIcon(GuestView.class.getResource("images/globe.png")));
//        descriptionImage = new JLabel(new ImageIcon(GuestView.class.getResource("images/magglass.png")));
//    }
//
//
//    private void setupHotels() {
//        hotelList.setModel(controller.getHotelModel());
//        bookmarkList.setModel(controller.getBookmarkModel());
//
//        bookmarkNotification.setVisible(false);
//
//        logoutHotelsButton.setVisible(false);
//        logoutHotelsButton.addActionListener(e -> controller.logout());
//
//        bookmarkHotelButton.setVisible(false);
//        bookmarkHotelButton.addActionListener(e -> bookmarkHotel());
//
//        guestHotelSearchButton.addActionListener(
//            e -> controller.searchHotels(
//                hotelLocation.getText(),
//                hotelDescription.getText()));
//    }
}
