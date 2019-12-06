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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.ui.controller.HotelSearchController;
import com.couchbase.travelsample.ui.view.widgets.HotelCellRenderer;


@Singleton
public class HotelSearchView extends Page {
    private final static Logger LOGGER = Logger.getLogger(HotelSearchView.class.getName());
    public static final String PAGE_NAME = "SEARCH_HOTELS";

    static class SelectionListener implements ListSelectionListener {
        private Set<Hotel> selection;

        public Set<Hotel> getSelection() { return (selection == null) ? null : new HashSet<>(selection); }

        public void valueChanged(ListSelectionEvent e) {
            Object src = e.getSource();
            if (!(src instanceof JList)) { return; }
            JList<Hotel> hotels = ((JList<Hotel>) src);

            ListModel<Hotel> model = hotels.getModel();
            ListSelectionModel selectionModel = hotels.getSelectionModel();

            if (selectionModel.isSelectionEmpty()) {
                selection = null;
                return;
            }

            selection = new HashSet<>();
            int n = selectionModel.getMaxSelectionIndex();
            for (int i = selectionModel.getMinSelectionIndex(); i <= n; i++) {
                selection.add(model.getElementAt(i));
            }

            LOGGER.log(Level.INFO, "new selection: " + selection);
        }
    }

    class HotelKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) {}

        public void keyTyped(KeyEvent e) { }

        public void keyReleased(KeyEvent e) { searchHotels(); }
    }


    private final HotelSearchController controller;
    private final SelectionListener selectionListener;

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
        this.selectionListener = new SelectionListener();

        logoutButton.addActionListener(e -> controller.logout());

        doneButton.addActionListener(e -> controller.done(selectionListener.getSelection()));

        hotelLocation.addKeyListener(new HotelKeyListener());
        hotelDescription.addKeyListener(new HotelKeyListener());

        hotels.setModel(controller.getHotelModel());
        hotels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        hotels.addListSelectionListener(selectionListener);
        hotels.setCellRenderer(new HotelCellRenderer());
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    public void open(Object args) { }

    @Override
    public void close() { }

    void searchHotels() {
        String location = hotelLocation.getText();
        if (location.isEmpty()) { return; }
        controller.searchHotels(location, hotelDescription.getText());
    }
}
