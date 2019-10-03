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
package com.couchbase.travelsample.view;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;

import com.couchbase.travelsample.controller.GuestController;


@Singleton
public final class GuestView {
    private final GuestController controller;

    private JPanel panel;
    private JTabbedPane hotelPane;
    private JTextField guestHotelLocationInput;
    private JTextField guestHotelDescriptionInput;
    private JLabel locationLabel;
    private JLabel descriptionLabel;
    private JButton guestHotelSearchButton;
    private JLabel locationImage;
    private JLabel descImage;
    private JLabel infoBox;
    private JList<String> bookmarkList;
    private JList<String> hotelList;
    private JScrollPane scrollPane;
    private JLabel bookmarkNotification;
    private JButton deleteBookmarkButton;
    private JButton logoutButton;
    private JButton logoutButton1;
    private JButton bookmarkHotelButton;

    @Inject
    public GuestView(GuestController controller) {
        this.controller = controller;

        hotelList.setModel(controller.getHotelModel());
        bookmarkHotelButton.setVisible(false);
        bookmarkHotelButton.addActionListener(e -> bookmarkHotel());

        bookmarkHotelButton.setVisible(false);
        deleteBookmarkButton.addActionListener(
            e -> controller.deleteBookmark(bookmarkList.getSelectedIndex()));

        bookmarkList.setModel(controller.getBookmarkModel());

        bookmarkNotification.setVisible(false);

        guestHotelSearchButton.addActionListener(
            e -> controller.searchHotels(
                guestHotelLocationInput.getText(),
                guestHotelDescriptionInput.getText()));

        controller.fetchBookmarks();
    }

    public JPanel getGuestView() { return panel; }

    private void bookmarkHotel() {
        controller.bookmarkHotel(hotelList.getSelectedIndex());
        displayBookmarkedNotification(3000);
    }

    private void displayBookmarkedNotification(int delayMs) {
        bookmarkNotification.setVisible(true);
        new Timer(delayMs, e -> bookmarkNotification.setVisible(false));
    }

    private void createUIComponents() {
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }
}
