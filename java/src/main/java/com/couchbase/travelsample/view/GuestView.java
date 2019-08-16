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
    private JButton logoutButton;
    private JButton logoutButton1;
    private JFrame guest;
    private DefaultListModel<String> hotelListModel = new DefaultListModel<>();
    private DefaultListModel<String> bookmarkListModel = new DefaultListModel<>();

    public GuestView() {
        guest = new JFrame("Guest");
        guest.setContentPane(panel);
        bookmarkHotelButton.setVisible(false);
        bookmarkNotification.setVisible(false);
        hotelList.setModel(hotelListModel);
        bookmarkList.setModel(bookmarkListModel);
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

    public void addHotel(String name, String address) {
        hotelListModel.addElement("- " + name + " on " + address);
    }

    public void refreshHotelList() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                hotelList.updateUI();
                bookmarkHotelButton.setVisible(hotelListModel.size() > 0);
            }
        });
    }

    public void clearBookmarks() {
        bookmarkListModel.removeAllElements();
    }

    public void addBookmark(String hotel) {
        bookmarkListModel.addElement(hotel);
    }

    public void deleteBookmark(String hotel) {
        bookmarkListModel.removeElement(hotel);
    }

    public void refreshBookmarkList() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                bookmarkList.updateUI();
            }
        });
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
