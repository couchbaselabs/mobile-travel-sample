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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JOptionPane;

import com.couchbase.travelsample.db.LocalStore;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.GuestView;
import com.couchbase.travelsample.ui.view.LoginView;
import com.couchbase.travelsample.ui.view.UserView;


@Singleton
public final class LoginController extends PageController {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());


    @Inject
    public LoginController(@Nonnull Nav nav, @Nonnull LocalStore localStore) {
        super(LoginView.PAGE_NAME, nav, localStore);
    }

    public void loginAsGuest() { localStore.openAsGuest(this::openGuest); }

    public void loginWithValidation(@Nonnull String username, @Nonnull char[] password) {
        localStore.openWithValidation(username, password, (e) -> openUser(e, username));
    }

    @Override
    protected void onClose() { }

    private void openUser(@Nullable Exception error, @Nullable String username) {
        if (error != null) {
            LOGGER.log(Level.WARNING, "login failure", error);

            JOptionPane.showMessageDialog(
                null,
                "Login failed for user " + username + ": " + error.getLocalizedMessage(),
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        toPage(UserView.PAGE_NAME);
    }

    void openGuest(Void ign) { toPage(GuestView.PAGE_NAME); }
}

