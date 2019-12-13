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

import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.couchbase.travelsample.db.DbManager;
import com.couchbase.travelsample.db.LoginDao;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.BookingsView;
import com.couchbase.travelsample.ui.view.GuestView;
import com.couchbase.travelsample.ui.view.LoginView;


@Singleton
public final class LoginController extends PageController {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());
    private final LoginDao loginDao;


    @Inject
    public LoginController(@Nonnull Nav nav, @Nonnull DbManager localStore, @Nonnull LoginDao loginDao) {
        super(LoginView.PAGE_NAME, nav, localStore);
        this.loginDao = loginDao;
    }

    public void loginAsGuest() { loginDao.openAsGuest((ign) -> toPage(GuestView.PAGE_NAME)); }

    public void loginWithValidation(
        @Nonnull String username,
        @Nonnull char[] password,
        @Nonnull Consumer<Exception> onFail) {
        loginDao.openWithValidation(
            username,
            password,
            () -> toPage(BookingsView.PAGE_NAME),
            onFail);
    }

    @Override
    protected void onClose() { }
}

