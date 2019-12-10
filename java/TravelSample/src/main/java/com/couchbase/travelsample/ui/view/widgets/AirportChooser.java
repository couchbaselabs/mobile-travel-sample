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
package com.couchbase.travelsample.ui.view.widgets;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.google.gwt.dev.util.collect.Lists;

import com.couchbase.travelsample.ui.controller.FlightSearchController;
import com.couchbase.travelsample.ui.view.FlightSearchView;


public class AirportChooser extends JComboBox<String> implements Consumer<List<String>> {

    class FlightKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) {}

        public void keyTyped(KeyEvent e) { }

        public void keyReleased(KeyEvent e) { searchAirports(); }
    }

    private final FlightSearchView view;
    private final FlightSearchController controller;

    public AirportChooser(FlightSearchView view, FlightSearchController controller) {
        this.view = view;
        this.controller = controller;
        setEditable(true);
        addKeyListener(new FlightKeyListener());
    }

    @Override
    public void accept(List<String> airports) {
        Lists.sort(airports, Comparator.naturalOrder());
        setModel(new DefaultComboBoxModel<>(new Vector<>(airports)));
    }

    void searchAirports() { controller.searchAirports("SFO", AirportChooser.this); }
}
