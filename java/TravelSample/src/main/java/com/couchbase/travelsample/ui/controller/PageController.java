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
import javax.annotation.Nullable;

import com.couchbase.travelsample.db.LocalStore;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.LoginView;


public abstract class PageController {
    @Nonnull
    protected final LocalStore localStore;
    @Nonnull
    private final Nav nav;

    protected PageController(@Nonnull Nav nav, @Nonnull LocalStore localStore) {
        this.nav = nav;
        this.localStore = localStore;
    }

    public void close() { localStore.cancelQueries(); }

    public void logout(@Nonnull String pageName) {
        close();
        localStore.close();
        toPage(pageName, LoginView.PAGE_NAME);
    }

    public void toPage(@Nullable String prevPageName, @Nonnull String nextPageName) {
        nav.toPage(prevPageName, nextPageName);
    }
}
