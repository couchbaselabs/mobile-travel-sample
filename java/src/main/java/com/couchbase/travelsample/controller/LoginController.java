package com.couchbase.travelsample.controller;

import com.couchbase.travelsample.model.DatabaseManager;
import com.couchbase.travelsample.view.LoginView;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements ViewController {

    private LoginView loginView;

    private GuestController guestController;
    private HotelFlightController hotelFlightController;

    private final static Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    public LoginController() {
        loginView = new LoginView();
        loginView.getLoginButton().addActionListener(e -> loginButtonPressed());
        loginView.getGuestLoginButton().addActionListener(e -> guestLoginButtonPressed());
    }

    public void show() {
        loginView.show();
    }

    public void hide() {
        loginView.hide();
    }

    public void dispose() {
        loginView.dispose();
    }

    private void loginButtonPressed() {
        String usernameInputText = loginView.getUsernameInput();
        String passwordInputText = loginView.getPasswordInput();

        if (usernameInputText.equals("") || passwordInputText.equals("")) {
            System.out.println("Login failed");
            JOptionPane.showMessageDialog(null, "Both username and password fields must be filled.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        } else {
            LOGGER.log(Level.INFO, "Username input: " + usernameInputText);
            LOGGER.log(Level.INFO, "Password input: " + passwordInputText);

            hotelFlightController = new HotelFlightController();
            hotelFlightController.show();

            dispose();
        }
    }

    private void guestLoginButtonPressed() {
        LOGGER.log(Level.INFO, "Guest button pressed");

        DatabaseManager dbMgr = DatabaseManager.getSharedInstance();
        dbMgr.OpenGuestDatabase();

        guestController = new GuestController();
        guestController.show();

        dispose();
    }
}
