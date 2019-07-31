package com.couchbase.travelsample.view;//prerequisite: download JCalendar

import com.couchbase.travelsample.view.LoginView;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.util.logging.Logger;

public class HotelFlightView {

    private JTabbedPane flightPane;
    public JPanel panel;
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
    private JLabel origDate;
    private JLabel destinDate;
    private JDateChooser originFlightDateChooser = new JDateChooser();
    private JDateChooser returnFlightDateChooser = new JDateChooser();
    private final static Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    public HotelFlightView() {
        JFrame search = new JFrame("com.couchbase.travelsample.view.HotelFlightView");
        search.setContentPane(panel);
        search.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        search.pack();
        originFlightDate.add(originFlightDateChooser);
        returnFlightDate.add(returnFlightDateChooser);
        search.setExtendedState(JFrame.MAXIMIZED_BOTH);
        search.setVisible(true);
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }
}
