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

import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.couchbase.travelsample.model.Flight;
import com.couchbase.travelsample.ui.controller.BookingsController;
import com.couchbase.travelsample.ui.view.widgets.FlightCellRenderer;


@Singleton
public final class BookingsView extends Page<BookingsController> {
    private static final Logger LOGGER = Logger.getLogger(GuestView.class.getName());

    public static final String PAGE_NAME = "BOOKINGS";

    private class SelectionListener implements ListSelectionListener {
        private Flight selection;

        SelectionListener() {}

        public Flight getSelection() { return selection; }

        public void valueChanged(ListSelectionEvent e) {
            final Object src = e.getSource();
            if (!(src instanceof JList)) { return; }
            final JList<Flight> flights = ((JList<Flight>) src);

            final ListSelectionModel selectionModel = flights.getSelectionModel();

            final boolean selectionEmpty = selectionModel.isSelectionEmpty();
            setDeleteButtonEnabled(!selectionEmpty);

            selection = (selectionEmpty)
                ? null
                : flights.getModel().getElementAt(selectionModel.getMaxSelectionIndex());
        }
    }

    private JPanel panel;
    private JList<Flight> flights;
    private JButton logoutButton;
    private JButton deleteBookingButton;
    private JButton findHotelsButton;
    private JButton findFlightsButton;

    @Inject
    public BookingsView(@Nonnull BookingsController controller) {
        super(PAGE_NAME, controller);

        final SelectionListener selectionListener = new SelectionListener();

        logoutButton.addActionListener(e -> logout());
        findFlightsButton.addActionListener(e -> controller.selectFlight());
        findHotelsButton.addActionListener(e -> controller.selectHotel());

        deleteBookingButton.addActionListener(e -> controller.deleteBooking(selectionListener.getSelection()));
        setDeleteButtonEnabled(false);

        flights.setModel(controller.getFlightsModel());
        flights.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flights.addListSelectionListener(selectionListener);
        flights.setCellRenderer(new FlightCellRenderer());
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    protected void onOpen(@Nullable Page<?> prevPage) { controller.fetchBookedFlights(); }

    @Override
    protected void onClose() { }

    void setDeleteButtonEnabled(boolean enabled) {
        deleteBookingButton.setEnabled(enabled);
        deleteBookingButton.setBackground(enabled ? COLOR_ACCENT : COLOR_SELECTED);
    }
}
