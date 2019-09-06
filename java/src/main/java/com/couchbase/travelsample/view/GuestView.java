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


import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.stereotype.Component;


@Component
public class GuestView {
    public JPanel panel1;
    public JButton bookmarkHotelButton;

    private final JFrame guest;
    private final DefaultListModel<String> hotelListModel = new DefaultListModel<>();
    private final DefaultListModel<String> bookmarkListModel = new DefaultListModel<>();
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

    public GuestView() {
        guest = new JFrame("Guest");
        guest.setContentPane(panel);
        bookmarkHotelButton.setVisible(false);
        bookmarkNotification.setVisible(false);
        hotelList.setModel(hotelListModel);
        bookmarkList.setModel(bookmarkListModel);
        guest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guest.pack();
        guest.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public JLabel getBookmarkNotification() {
        return bookmarkNotification;
    }

    public JButton getDeleteBookmarkButton() {
        return deleteBookmarkButton;
    }

    public JButton getGuestHotelSearchButton() {
        return guestHotelSearchButton;
    }

    public JButton getBookmarkHotelButton() {
        return bookmarkHotelButton;
    }

    public JList<String> getHotelList() {
        return hotelList;
    }

    public void setHotelList(DefaultListModel<String> hotels) {
        hotelList.setModel(hotels);
    }

    public JList<String> getBookmarkList() {
        return bookmarkList;
    }

    public DefaultListModel<String> getHotelListModel() {
        return hotelListModel;
    }

    public DefaultListModel<String> getBookmarkListModel() { return bookmarkListModel; }

    public void addHotel(String name, String address) {
        hotelListModel.addElement("- " + name + " on " + address);
    }

    public void refreshHotelList() {
        SwingUtilities.invokeLater(
            () -> bookmarkHotelButton.setVisible(hotelListModel.size() > 0)
//            new Runnable() {
//                @Override
//                public void run() {
//                    hotelList.updateUI();
//                    bookmarkHotelButton.setVisible(hotelListModel.size() > 0);
//                }
//            }
        );
    }

    public void clearHotels() {
        hotelListModel.removeAllElements();
    }

    public void clearBookmarks() {
        bookmarkListModel.removeAllElements();
    }

    public void addBookmark(String hotel) {
        bookmarkListModel.addElement(hotel);
    }

    public void deleteBookmark(String hotel) { bookmarkListModel.removeElement(hotel); }

    public void refreshBookmarkList() { SwingUtilities.invokeLater(() -> bookmarkList.updateUI()); }

    public String getGuestHotelLocationInput() {
        return guestHotelLocationInput.getText();
    }

    public String getGuestHotelDescriptionInput() {
        return guestHotelDescriptionInput.getText();
    }

    public void show() {
        guest.setVisible(true);
    }

    public void hide() {
        guest.setVisible(false);
    }

    public void dispose() {
        guest.dispose();
    }

    private void createUIComponents() {
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }
}
