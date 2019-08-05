package com.couchbase.travelsample.controller;

import com.couchbase.travelsample.model.GuestModel;
import com.couchbase.travelsample.model.HotelFlightModel;
import com.couchbase.travelsample.model.LoginModel;
import com.couchbase.travelsample.view.GuestView;
import com.couchbase.travelsample.view.HotelFlightView;
import com.couchbase.travelsample.view.LoginView;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private LoginModel loginModel;
    private LoginView loginView;

    private GuestController guestController;
    private HotelFlightController hotelFlightController;

    private final static Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    public LoginController(LoginModel model, LoginView view) {
        loginModel = model;
        loginView = view;
        this.initController();
    }

    public void initController() {
        loginView.getLoginButton().addActionListener(e -> loginButtonPressed());
        loginView.getGuestLoginButton().addActionListener(e -> guestLoginButtonPressed());
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
            loginView.setInvisible();

            HotelFlightModel hotelFlightModel = new HotelFlightModel();
            HotelFlightView hotelFlightView = new HotelFlightView();
            hotelFlightController = new HotelFlightController(hotelFlightModel, hotelFlightView);
        }
    }

    //register callback on a change
    private void guestLoginButtonPressed() {
        LOGGER.log(Level.INFO, "Guest button pressed");
        loginView.setInvisible();

        GuestModel guestModel = new GuestModel();
        GuestView guestView = new GuestView();
        guestController = new GuestController(guestModel, guestView);
    }
}
