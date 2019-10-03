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

import com.toedter.calendar.JDateChooser;

import com.couchbase.travelsample.controller.HotelFlightController;


@Singleton
public final class HotelFlightView {
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

    public JPanel getHotelFlightView() { return panel; }

    private void createUIComponents() {
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }

    private String getDate(JDateChooser dateChooser) {
        return ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
    }
}
