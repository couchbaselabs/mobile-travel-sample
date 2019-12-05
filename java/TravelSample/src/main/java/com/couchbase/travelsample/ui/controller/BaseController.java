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

import com.couchbase.travelsample.db.LocalStore;
import com.couchbase.travelsample.ui.Nav;
import com.couchbase.travelsample.ui.view.LoginView;


public abstract class BaseController {
    protected final Nav nav;
    protected final LocalStore localStore;

    protected BaseController(Nav nav, LocalStore localStore) {
        this.nav = nav;
        this.localStore = localStore;
    }

    public void close() { localStore.cancelQueries(); }

    public void logout() {
        localStore.close();
        nav.toPage(LoginView.PAGE_NAME);
    }
}
