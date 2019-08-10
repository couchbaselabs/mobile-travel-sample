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
    private JList<String> bookmarkList;
    private JList<String> hotelList;
    private JScrollPane scrollPane;
    public JButton bookmarkHotelButton;
    private JLabel bookmarkNotification;
    private JButton deleteBookmarkButton;
    private JFrame guest;
    private DefaultListModel<String> hotelListModel = new DefaultListModel<>();
    private DefaultListModel<String> bookmarkListModel = new DefaultListModel<>();

    public GuestView() {
        guest = new JFrame("Guest");
        guest.setContentPane(panel);
        bookmarkHotelButton.setVisible(false);
        bookmarkNotification.setVisible(false);
        guest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guest.pack();
        guest.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public JLabel getBookmarkNotification() {
        return bookmarkNotification;
    }

    public JButton getDeleteBookmarkButton() { return deleteBookmarkButton; }

    public JButton getGuestHotelSearchButton() {
        return guestHotelSearchButton;
    }

    public JButton getBookmarkHotelButton() {
        return bookmarkHotelButton;
    }

    public JList<String> getHotelList() {
        return hotelList;
    }

    public JList<String> getBookmarkList() { return bookmarkList; }

    public DefaultListModel<String> getHotelListModel() {
        return hotelListModel;
    }

    public DefaultListModel<String> getBookmarkListModel() {
        return bookmarkListModel;
    }

    public void initialAddHotel(String name, String address) {
        hotelListModel.addElement("- " + name + " on " + address);
    }

    public void addHotel(int index, String hotel) { hotelListModel.add(index, hotel); }

    public void deleteHotel(String hotel) { hotelListModel.removeElement(hotel); }

    public void deleteAllHotel() { hotelListModel.removeAllElements(); }

    public void setHotelList(DefaultListModel<String> list) {
        hotelList.setModel(list);
        bookmarkHotelButton.setVisible(true);
    }

    public void addBookmark(String hotel) {
        bookmarkListModel.addElement(hotel);
    }

    public void deleteBookmark(String hotel) { bookmarkListModel.removeElement(hotel); }

    public void setBookmarkList(DefaultListModel<String> list) {
        bookmarkList.setModel(list);
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
