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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.ui.controller.HotelSearchController;


@Singleton
public class HotelSearchView extends Page {
    public static final String PAGE_NAME = "SEARCH_HOTELS";

    class HotelKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) {}

        public void keyTyped(KeyEvent e) { }

        public void keyReleased(KeyEvent e) { searchHotels(); }
    }

    class HotelCellRenderer extends JPanel implements ListCellRenderer<Hotel> {
        private final JLabel name;
        private final JLabel location;

        HotelCellRenderer() {
            super(new BorderLayout(), true);

            name = new JLabel();
            name.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));
            location = new JLabel();
            location.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));

            add(name, BorderLayout.NORTH);
            add(location, BorderLayout.SOUTH);

            setBackground(Color.WHITE);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(204, 42, 47)));

        }

        public Component getListCellRendererComponent(
            JList list,
            Hotel hotel,
            int index,
            boolean selected,
            boolean focused) {
            name.setText(hotel.getName());
            location.setText(hotel.getAddress());
            return this;
        }
    }


    private final HotelSearchController controller;

    private JPanel panel;
    private JTextField hotelLocation;
    private JTextField hotelDescription;
    private JList<Hotel> hotels;
    private JButton logoutButton;
    private JButton doneButton;

    @Inject
    public HotelSearchView(HotelSearchController controller) {
        super(PAGE_NAME);

        this.controller = controller;

        logoutButton.addActionListener(e -> controller.logout());

        hotelLocation.addKeyListener(new HotelKeyListener());
        hotelDescription.addKeyListener(new HotelKeyListener());

        hotels.setCellRenderer(new HotelCellRenderer());
        hotels.setModel(controller.getHotelModel());
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    public void open() { }

    @Override
    public void close() { }

    private void searchHotels() {
        String location = hotelLocation.getText();
        if (location.isEmpty()) { return; }

        controller.searchHotels(location, hotelDescription.getText());
    }
}
