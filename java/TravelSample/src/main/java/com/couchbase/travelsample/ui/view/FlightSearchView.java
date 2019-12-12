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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.toedter.calendar.JDateChooser;

import com.couchbase.travelsample.model.Flight;
import com.couchbase.travelsample.model.Trip;
import com.couchbase.travelsample.ui.controller.FlightSearchController;
import com.couchbase.travelsample.ui.view.widgets.FlightCellRenderer;
import com.couchbase.travelsample.ui.view.widgets.SuggestedTextField;


@Singleton
public class FlightSearchView extends Page<FlightSearchController> {
    private static final Logger LOGGER = Logger.getLogger(HotelSearchView.class.getName());

    public static final String PAGE_NAME = "SEARCH_FLIGHTS";


    class SearchKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) {}

        public void keyTyped(KeyEvent e) { }

        public void keyReleased(KeyEvent e) { setSearchButtonEnabled(); }
    }

    private class SelectionListener implements ListSelectionListener {
        private Flight selection;

        public SelectionListener() {}

        public Flight getSelection() { return selection; }

        public void valueChanged(ListSelectionEvent e) {
            Object src = e.getSource();
            if (!(src instanceof JList)) { return; }
            JList<Flight> flights = ((JList<Flight>) src);

            ListSelectionModel selectionModel = flights.getSelectionModel();
            ListModel<Flight> model = flights.getModel();

            selection = (selectionModel.isSelectionEmpty())
                ? null
                : model.getElementAt(selectionModel.getMinSelectionIndex());

            setBookButtonEnabled();
        }
    }

    private final SelectionListener outboundSelectionListener = new SelectionListener();
    private final SelectionListener returningSelectionListener = new SelectionListener();
    private final JFormattedTextField departureDateText;
    private final JFormattedTextField returnDateText;

    private JPanel panel;
    private SuggestedTextField<String> originAirport;
    private SuggestedTextField<String> destinationAirport;
    private JDateChooser departureDate;
    private JDateChooser returnDate;
    private JList<Flight> outboundFlights;
    private JList<Flight> returningFlights;
    private JButton logoutButton;
    private JButton searchButton;
    private JButton doneButton;
    private JButton bookButton;

    @Inject
    public FlightSearchView(FlightSearchController controller) {
        super(PAGE_NAME, controller);

        logoutButton.addActionListener(e -> logout());
        doneButton.addActionListener(e -> done());

        searchButton.addActionListener(e -> searchFlights());
        bookButton.addActionListener(e -> bookFlights());

        outboundFlights.setModel(controller.getOutboundFlightsModel());
        outboundFlights.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outboundFlights.addListSelectionListener(outboundSelectionListener);
        outboundFlights.setCellRenderer(new FlightCellRenderer());

        returningFlights.setModel(controller.getReturningFlightsModel());
        returningFlights.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        returningFlights.addListSelectionListener(returningSelectionListener);
        returningFlights.setCellRenderer(new FlightCellRenderer());
        setBookButtonEnabled();

        final SearchKeyListener keyListener = new SearchKeyListener();
        originAirport.addKeyListener(keyListener);
        destinationAirport.addKeyListener(keyListener);
        departureDateText = (JFormattedTextField) departureDate.getDateEditor().getUiComponent();
        departureDateText.addKeyListener(keyListener);
        returnDateText = (JFormattedTextField) returnDate.getDateEditor().getUiComponent();
        returnDateText.addKeyListener(keyListener);

        setSearchButtonEnabled();
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    protected void onOpen(@Nullable Page<?> prevPage) {
        final ZoneId defaultZone = ZoneId.systemDefault();
        final LocalDateTime defaultDate = LocalDateTime.now().plusWeeks(2);
        departureDate.setDate(Date.from(defaultDate.atZone(defaultZone).toInstant()));
        departureDate.setDate(Date.from(defaultDate.plusWeeks(1).atZone(defaultZone).toInstant()));
    }

    @Override
    protected void onClose() { }

    void searchAirports(@Nonnull String prefix, Consumer<List<String>> consumer) {
        controller.searchAirports(prefix, 12, consumer);
    }

    void searchFlights() {
        controller.searchFlights(
            originAirport.getText(),
            destinationAirport.getText(),
            departureDate.getDate(),
            returnDate.getDate());
    }

    void bookFlights() {
        controller.bookTrip(
            new Trip(
                outboundSelectionListener.getSelection(),
                returningSelectionListener.getSelection(),
                departureDate.getDate(),
                returnDate.getDate()),
            this::onError);
    }

    void onError(@Nonnull Exception failure) {
        JOptionPane.showMessageDialog(null, failure.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
    }

    void setSearchButtonEnabled() {
        final boolean enabled = !(
            originAirport.getText().isEmpty()
                || destinationAirport.getText().isEmpty()
                || (departureDate.getDate() == null)
                || (returnDate).getDate() == null);
        searchButton.setEnabled(enabled);
        searchButton.setBackground(enabled ? COLOR_ACCENT : COLOR_SELECTED);
    }

    void setBookButtonEnabled() {
        final boolean enabled
            = (outboundSelectionListener.getSelection() != null) && (returningSelectionListener.getSelection() != null);
        bookButton.setEnabled(enabled);
        bookButton.setBackground(enabled ? COLOR_ACCENT : COLOR_SELECTED);
    }

    private void createUIComponents() {
        originAirport = new SuggestedTextField<>(this::searchAirports);
        destinationAirport = new SuggestedTextField<>(this::searchAirports);

        departureDate = new JDateChooser();
        returnDate = new JDateChooser();
    }

    void done() {
        originAirport.setText("");
        destinationAirport.setText("");
        departureDateText.setText("");
        returnDateText.setText("");
        controller.done();
    }
}

