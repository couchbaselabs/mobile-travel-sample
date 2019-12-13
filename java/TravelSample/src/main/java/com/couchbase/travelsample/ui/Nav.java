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
package com.couchbase.travelsample.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.couchbase.travelsample.AppFactory;
import com.couchbase.travelsample.ui.view.Page;
import com.couchbase.travelsample.ui.view.RootView;


@Singleton
public class Nav {
    private static final Logger LOGGER = Logger.getLogger(Nav.class.getName());


    private final RootView rootView;

    private final Map<String, Page<?>> pages = new HashMap<>();

    private Page<?> frontPage;

    @Inject
    public Nav(RootView rootView) { this.rootView = rootView; }

    public void start(AppFactory appFactory) {
        for (Page<?> page : appFactory.pages()) {
            final String name = page.getName();
            LOGGER.info("Adding page: " + name + " @" + page);
            pages.put(name, page);
            rootView.addPage(page);
        }

        frontPage = appFactory.startPage();
        rootView.start(frontPage);
    }

    public void toPage(@Nonnull String prevPageName, @Nonnull String nextPageName) {
        LOGGER.info("Nav: " + prevPageName + " => " + nextPageName);

        final Page<?> prevPage = pages.get(prevPageName);
        final Page<?> page = pages.get(nextPageName);
        if (page == null) { return; }

        page.open(prevPage);
        rootView.toPage(page);
        frontPage.close();
        frontPage = page;
    }
}
