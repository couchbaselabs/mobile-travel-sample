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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

import com.couchbase.travelsample.ui.controller.HotelFlightController;


@Singleton
public final class HotelFlightView extends Page {
    public static final String PAGE_NAME = "HOTEL-FLIGHT";


    private final HotelFlightController controller;

    private JPanel panel;
    private JTabbedPane flightPane;
    private JTextField hotelLocationInput;
    private JTextField hotelDescriptionInput;
    private JLabel locationLabel;
    private JLabel descriptionLabel;
    private JButton hotelSearchButton;
    private JPanel originFlightDate;
    private JPanel returnFlightDate;
    private JLabel descImage;
    private JLabel locationImage;
    private JLabel originFlight;
    private JLabel destFlight;
    private JTextField flightOriginInput;
    private JTextField flightDestinationInput;
    private JButton flightSearchButton;
    private JLabel originDate;
    private JLabel destinationDate;
    private JDateChooser originFlightDateChooser;
    private JDateChooser returnFlightDateChooser;

    @Inject
    public HotelFlightView(HotelFlightController controller) {
        super(PAGE_NAME);

        this.controller = controller;

        hotelSearchButton.addActionListener(
            e -> controller.hotelSearchButtonPressed(hotelLocationInput.getText(), hotelDescriptionInput.getText()));

        flightSearchButton.addActionListener(
            e -> controller.flightSearchButtonPressed(
                flightOriginInput.getText(),
                flightDestinationInput.getText(),
                getDate(originFlightDateChooser),
                getDate(returnFlightDateChooser)));

        originFlightDateChooser = new JDateChooser();
        originFlightDate.add(originFlightDateChooser);

        returnFlightDateChooser = new JDateChooser();
        returnFlightDate.add(returnFlightDateChooser);
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    public void open(Object args) { }

    @Override
    public void close() { }

    private String getDate(JDateChooser dateChooser) {
        return ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
    }

    private void createUIComponents() {
        locationImage = new JLabel(new ImageIcon(HotelFlightView.class.getResource("images/globe.png")));
        descImage = new JLabel(new ImageIcon(HotelFlightView.class.getResource("images/magglass.png")));
    }

}
