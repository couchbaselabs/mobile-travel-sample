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
package com.couchbase.travelsample.ui.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JOptionPane;

import com.couchbase.travelsample.db.LocalStore;
import com.couchbase.travelsample.ui.view.LoginView;
import com.couchbase.travelsample.ui.Nav;


@Singleton
public final class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    private final LocalStore db;
    private final Nav nav;

    @Inject
    public LoginController(LocalStore db, Nav nav) {
        this.db = db;
        this.nav = nav;
    }

    public void loginAsGuest() { db.openAsGuest(); }

    public void loginAsUser(String username, String password) {
        db.openWithValidation(
            username,
            password,
            (ok) -> {
                if (ok) {
                    nav.nextPage();
                    return;
                }
                JOptionPane.showMessageDialog(
                    null,
                    "Both username and password fields must be filled.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            });
    }
}

