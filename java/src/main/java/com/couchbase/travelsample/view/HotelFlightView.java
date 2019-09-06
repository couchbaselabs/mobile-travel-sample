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

import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;
import org.springframework.stereotype.Component;


@Component
public class HotelFlightView {
    private final static Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    public final JFrame search;
    public JPanel panel;

    private final JDateChooser originFlightDateChooser = new JDateChooser();
    private final JDateChooser returnFlightDateChooser = new JDateChooser();
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

    public HotelFlightView() {
        search = new JFrame("com.couchbase.travelsample.view.HotelFlightView");
        search.setContentPane(panel);
        search.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        search.pack();
        originFlightDate.add(originFlightDateChooser);
        returnFlightDate.add(returnFlightDateChooser);
        search.setExtendedState(JFrame.MAXIMIZED_BOTH);
        show();
    }

    public JButton getHotelSearchButton() {
        return hotelSearchButton;
    }

    public JButton getFlightSearchButton() {
        return flightSearchButton;
    }

    public String getHotelLocationInput() {
        return hotelLocationInput.getText();
    }

    public String getHotelDescriptionInput() {
        return hotelDescriptionInput.getText();
    }

    public String getFlightOriginInput() {
        return flightOriginInput.getText();
    }

    public String getFlightDestinationInput() {
        return flightDestinationInput.getText();
    }

    public String getFlightOriginDateInput() {
        return ((JTextField) originFlightDateChooser.getDateEditor().getUiComponent()).getText();
    }

    public String getFlightDestinationDateInput() {
        return ((JTextField) returnFlightDateChooser.getDateEditor().getUiComponent()).getText();
    }

    public void show() {
        search.setVisible(true);
    }

    public void hide() {
        search.setVisible(false);
    }

    public void dispose() {
        search.dispose();
    }

    // TODO: place custom component creation code here
    private void createUIComponents() {
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }
}
