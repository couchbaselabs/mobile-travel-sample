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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JOptionPane;

import com.couchbase.travelsample.model.DatabaseManager;
import com.couchbase.travelsample.view.LoginView;

@Singleton
public final class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    private final DatabaseManager dbMgr;
    private final LoginView loginView;
    private final GuestController guestController;
    private final HotelFlightController hotelFlightController;

    @Inject
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
        }
    }

    private void guestLoginButtonPressed() {
        dbMgr.openGuestDatabase();
    }
}
