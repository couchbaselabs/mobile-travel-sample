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

import javax.annotation.Nonnull;

import com.couchbase.travelsample.db.DbManager;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.LoginView;


public abstract class PageController {
    @Nonnull
    protected final DbManager localStore;

    @Nonnull
    private final String pageName;
    @Nonnull
    private final Nav nav;

    @Nonnull
    private String prevPageName;

    protected PageController(@Nonnull String pageName, @Nonnull Nav nav, @Nonnull DbManager localStore) {
        this.pageName = pageName;
        this.nav = nav;
        this.localStore = localStore;
    }

    protected abstract void onClose();

    public final void close() { onClose(); }

    public final void setPrevPage(@Nonnull String prevPageName) { this.prevPageName = prevPageName; }

    public final boolean isLoggedIn() { return localStore.isLoggedIn(); }

    public final void logout() {
        close();
        localStore.close();
        toPage(LoginView.PAGE_NAME);
    }

    protected final void toPage(@Nonnull String nextPageName) { nav.toPage(pageName, nextPageName); }

    protected final void back() { toPage(prevPageName); }
}
