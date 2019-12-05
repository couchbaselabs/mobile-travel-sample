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
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.GuestView;
import com.couchbase.travelsample.ui.view.LoginView;


@Singleton
public final class LoginController extends BaseController {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    @Inject
    public LoginController(Nav nav, LocalStore localStore) { super(nav, localStore); }

    public void loginAsGuest() { localStore.openAsGuest(this::onLoginComplete); }

    public void loginWithValidation(String username, char[] password) {
        localStore.openWithValidation(username, password, this::onLoginComplete);
    }

    private void onLoginComplete(Boolean ok) {
        LOGGER.info("");

        if (!ok) {
            JOptionPane.showMessageDialog(
                null,
                "Both username and password fields must be filled.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        nav.toPage(GuestView.PAGE_NAME);
    }
}

