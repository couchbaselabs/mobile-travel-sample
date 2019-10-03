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


import java.awt.*;

import javax.inject.Inject;
import javax.swing.*;

import com.couchbase.travelsample.app.DaggerEnv;
import com.couchbase.travelsample.app.Env;
import com.couchbase.travelsample.view.GuestView;
import com.couchbase.travelsample.view.HotelFlightView;
import com.couchbase.travelsample.view.LoginView;


public class TravelSample {
    public static final String LOG_DIR = "logs";
    public static final String DATABASE_DIR = "database";
    public static final String GUEST_DATABASE_DIR = DATABASE_DIR + "/guest";
    public static final String DATABASE_NAME = "guest";
    public static final String WEB_APP_ENDPOINT = "http://54.185.31.148:8080/api/";

    private static Env env;
    public static Env env() { return env; }

    public static void main(String[] args) {
        env = DaggerEnv.create();
        env.app().start();
    }

    private final JFrame frame;
    private final JPanel cards;

    @Inject
    public TravelSample(LoginView loginView, GuestView guestView, HotelFlightView hotelFlightView) {
        frame = new JFrame("Travel Sample");
        cards = new JPanel(new CardLayout());

        cards.add(loginView.getLoginView());
        cards.add(guestView.getGuestView());
        cards.add(hotelFlightView.getHotelFlightView());

        frame.setContentPane(cards);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void start() {
        frame.setVisible(true);
    }
}
