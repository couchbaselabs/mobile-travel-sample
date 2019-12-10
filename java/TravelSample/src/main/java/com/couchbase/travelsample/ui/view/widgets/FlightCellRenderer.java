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

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.couchbase.travelsample.model.Flight;


public class FlightCellRenderer extends JPanel implements ListCellRenderer<Flight> {
    private final JLabel flightInfo;
    private final JLabel details;

    public FlightCellRenderer() {
        super(new BorderLayout(), true);

        flightInfo = new JLabel();
        flightInfo.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));
        Font f = flightInfo.getFont();
        flightInfo.setFont(f.deriveFont(f.getStyle() | Font.BOLD));

        details = new JLabel();
        details.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));

        add(flightInfo, BorderLayout.NORTH);
        add(details, BorderLayout.SOUTH);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(204, 42, 47)));
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
            flight.getSourceAirport(), flight.getDestinationAirport(), flight.getPrice()));
        return this;
    }
}
