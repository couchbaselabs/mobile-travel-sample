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
package com.couchbase.travelsample.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.couchbase.travelsample.model.DatabaseManager;
import com.couchbase.travelsample.view.LoginView;

@Component
public class LoginController implements ViewController {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());


    private final DatabaseManager dbMgr;
    private final LoginView loginView;
    private final GuestController guestController;
    private final HotelFlightController hotelFlightController;

    @Autowired
    public LoginController(
        DatabaseManager dbMgr,
        GuestController guestController,
        HotelFlightController hotelFlightController,
        LoginView loginView)
    {
        this.dbMgr = dbMgr;
        this.loginView = loginView;
        this.guestController = guestController;
        this.hotelFlightController = hotelFlightController;


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
            JOptionPane.showMessageDialog(
                null,
                "Both username and password fields must be filled.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
        else {
            LOGGER.log(Level.INFO, "Username input: " + usernameInputText);
            LOGGER.log(Level.INFO, "Password input: " + passwordInputText);

            hotelFlightController.show();

            dispose();
        }
    }

    private void guestLoginButtonPressed() {
        dbMgr.openGuestDatabase();

        guestController.show();

        dispose();
    }
}
