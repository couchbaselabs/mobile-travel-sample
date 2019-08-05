package com.couchbase.travelsample.view;

import javax.swing.*;

public class GuestView {

    private JPanel panel;
    private JTabbedPane flightPane;
    private JTextField guestHotelLocationInput;
    private JTextField guestHotelDescriptionInput;
    private JLabel locationLabel;
    private JLabel descriptionLabel;
    private JButton guestHotelSearchButton;
    private JLabel locationImage;
    private JLabel descImage;
    private JLabel infobox;
    public JPanel panel1;
    public JFrame guest;

    public GuestView() {
        guest = new JFrame("com.couchbase.travelsample.view.GuestView");
        guest.setContentPane(panel);
        guest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guest.pack();
        guest.setExtendedState(JFrame.MAXIMIZED_BOTH);
        guest.setVisible(true);
    }

    public JButton getGuestHotelSearchButton() {
        return guestHotelSearchButton;
    }

    public String getGuestHotelLocationInput() {
        return guestHotelLocationInput.getText();
    }

    public String getGuestHotelDescriptionInput() {
        return guestHotelDescriptionInput.getText();
    }

    public void setVisible() {
        guest.setVisible(true);
    }

    public void setInvisible() {
        guest.setVisible(false);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }
}
