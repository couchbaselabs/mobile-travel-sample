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
package com.couchbase.travelsample.ui.view.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.couchbase.travelsample.model.Flight;
import com.couchbase.travelsample.ui.view.Page;


public class FlightCellRenderer extends JPanel implements ListCellRenderer<Flight> {
    private final JLabel flightInfo;
    private final JLabel details;

    public FlightCellRenderer() {
        super(new BorderLayout(), true);

        flightInfo = new JLabel();
        flightInfo.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));
        final Font f = flightInfo.getFont();
        flightInfo.setFont(f.deriveFont(f.getStyle() | Font.BOLD));

        details = new JLabel();
        details.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));

        add(flightInfo, BorderLayout.NORTH);
        add(details, BorderLayout.SOUTH);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Page.COLOR_ACCENT));
    }

    public Component getListCellRendererComponent(
        JList list,
        Flight flight,
        int idx,
        boolean selected,
        boolean focused) {
        flightInfo.setText(flight.getCarrier() + " " + flight.getFlight());
        details.setText(String.format(
            "%s - %s, Fare: $%.2f",
            flight.getOriginAirport(), flight.getDestinationAirport(), flight.getPrice()));
        setForeground(focused ? Page.COLOR_TEXT : Page.COLOR_UNFOCUSED);
        setBackground(selected ? Page.COLOR_SELECTED : Page.COLOR_BACKGROUND);
        return this;
    }
}
