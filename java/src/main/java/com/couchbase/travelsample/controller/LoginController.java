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
import javax.swing.*;

import com.couchbase.travelsample.model.DatabaseManager;
import com.couchbase.travelsample.view.LoginView;


@Singleton
public final class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    private final DatabaseManager dbMgr;

    @Inject
    public LoginController(DatabaseManager dbMgr) {
        this.dbMgr = dbMgr;
    }

    public void loginAsGuest() {
        dbMgr.openGuestDatabase();
    }

    public void loginAsUser(String username, String password) {
        if (username.equals("") || password.equals("")) {
            LOGGER.log(Level.WARNING, "login failed: " + username + ", " + password);
            JOptionPane.showMessageDialog(
                null,
                "Both username and password fields must be filled.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        LOGGER.log(Level.INFO, "Username input: " + username);
        LOGGER.log(Level.INFO, "Password input: " + password);
    }
}
