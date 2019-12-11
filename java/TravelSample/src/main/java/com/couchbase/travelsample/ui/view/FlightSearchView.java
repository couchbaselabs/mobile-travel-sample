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

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import com.couchbase.travelsample.model.Flight;
import com.couchbase.travelsample.ui.controller.FlightSearchController;
import com.couchbase.travelsample.ui.view.widgets.FlightCellRenderer;
import com.couchbase.travelsample.ui.view.widgets.SuggestedTextField;


@Singleton
public class FlightSearchView extends Page<FlightSearchController> {
    private final static Logger LOGGER = Logger.getLogger(HotelSearchView.class.getName());

    public static final String PAGE_NAME = "SEARCH_FLIGHTS";


    private JPanel panel;
    private SuggestedTextField<String> departureAirport;
    private SuggestedTextField<String> destinationAirport;
    private JDateChooser departureDate;
    private JDateChooser returnDate;
    private JList<Flight> flights;
    private JButton logoutButton;
    private JButton doneButton;

    @Inject
    public FlightSearchView(FlightSearchController controller) {
        super(PAGE_NAME, controller);

        logoutButton.addActionListener(e -> logout());
        doneButton.addActionListener(e -> done());

        flights.setModel(controller.getFlightsModel());
        flights.setCellRenderer(new FlightCellRenderer());
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    protected void onOpen(@Nullable Page<?> prevPage) { }

    @Override
    protected void onClose() { }

    void done() {
        LOGGER.log(
            Level.INFO,
            "departure: " + departureAirport.getText() + " destination: " + destinationAirport.getText());
        controller.done();
    }

    void searchAirports(@Nonnull String prefix, Consumer<List<String>> consumer) {
        controller.searchAirports(prefix, 12, consumer);
    }

    private void createUIComponents() {
        departureAirport = new SuggestedTextField<String>(this::searchAirports);
        destinationAirport = new SuggestedTextField<String>(this::searchAirports);
        departureDate = new JDateChooser();
        returnDate = new JDateChooser();
    }
}
