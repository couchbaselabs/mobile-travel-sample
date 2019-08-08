package com.couchbase.travelsample.view;

import javax.swing.*;

public class GuestView {

    private JPanel panel;
    private JTabbedPane hotelPane;
    private JTextField guestHotelLocationInput;
    private JTextField guestHotelDescriptionInput;
    private JLabel locationLabel;
    private JLabel descriptionLabel;
    private JButton guestHotelSearchButton;
    private JLabel locationImage;
    private JLabel descImage;
    private JLabel infobox;
    public JPanel panel1;
    private JList bookmarkList;
    public JFrame guest;

    public GuestView() {
        guest = new JFrame("Guest");
        guest.setContentPane(panel);
        guest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guest.pack();
        guest.setExtendedState(JFrame.MAXIMIZED_BOTH);
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

    public void show() {
        guest.setVisible(true);
    }

    public void hide() {
        guest.setVisible(false);
    }

    public void dispose() {
        guest.dispose();
    }

    private void createUIComponents () {
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }
}
