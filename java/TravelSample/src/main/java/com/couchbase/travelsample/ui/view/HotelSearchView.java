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
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class HotelSearchView extends Page<HotelSearchController> implements GuestView.HotelSelector {
    private static final Logger LOGGER = Logger.getLogger(HotelSearchView.class.getName());

    public static final String PAGE_NAME = "SEARCH_HOTELS";

    private static class SelectionListener implements ListSelectionListener {
        private final Set<Hotel> selection = new HashSet<>();

        public SelectionListener() {}

        public Set<Hotel> getSelection() { return new HashSet<>(selection); }

        public void valueChanged(ListSelectionEvent e) {
            Object src = e.getSource();
            if (!(src instanceof JList)) { return; }
            JList<Hotel> hotels = ((JList<Hotel>) src);

            ListSelectionModel selectionModel = hotels.getSelectionModel();

            selection.clear();
            if (selectionModel.isSelectionEmpty()) { return; }

            ListModel<Hotel> model = hotels.getModel();
            int n = selectionModel.getMaxSelectionIndex();
            for (int i = selectionModel.getMinSelectionIndex(); i <= n; i++) {
                if (selectionModel.isSelectedIndex(i)) { selection.add(model.getElementAt(i)); }
            }
        }
    }

    class HotelKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) {}

        public void keyTyped(KeyEvent e) { }

        public void keyReleased(KeyEvent e) { searchHotels(); }
    }


    @Nonnull
    private final SelectionListener selectionListener;

    private JPanel panel;
    private JTextField hotelLocation;
    private JTextField hotelDescription;
    private JList<Hotel> hotels;
    private JButton logoutButton;
    private JButton doneButton;

    private Set<Hotel> selection;

    @Inject
    public HotelSearchView(HotelSearchController controller) {
        super(PAGE_NAME, controller);

        selectionListener = new SelectionListener();

        logoutButton.addActionListener(e -> logout());
        doneButton.addActionListener(e -> done());

        HotelKeyListener listener = new HotelKeyListener();
        hotelLocation.addKeyListener(listener);
        hotelDescription.addKeyListener(listener);

        hotels.setModel(controller.getHotelModel());
        hotels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        hotels.addListSelectionListener(selectionListener);
        hotels.setCellRenderer(new HotelCellRenderer());
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    protected void onOpen(@Nullable Page<?> prevPage) { }

    @Override
    protected void onClose() { }

    @Override
    public Set<Hotel> getSelection() { return new HashSet<>(selection); }

    void searchHotels() {
        String location = hotelLocation.getText();
        if (location.isEmpty()) { return; }
        controller.searchHotels(location, hotelDescription.getText());
    }

    void done() {
        selection = selectionListener.getSelection();
        hotelLocation.setText("");
        hotelDescription.setText("");
        hotels.clearSelection();
        controller.done();
    }
}
