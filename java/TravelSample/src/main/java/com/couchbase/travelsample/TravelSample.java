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
package com.couchbase.travelsample;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import com.couchbase.travelsample.ui.Nav;


public class TravelSample {
    public static void main(String[] args) {
        final AppFactory appFactory = DaggerAppFactory.create();
        appFactory.app().start(appFactory);
    }

    private final Nav nav;

    @Inject
    public TravelSample(Nav nav) { this.nav = nav; }

    private void start(AppFactory appFactory) { SwingUtilities.invokeLater(() -> nav.start(appFactory)); }
}
