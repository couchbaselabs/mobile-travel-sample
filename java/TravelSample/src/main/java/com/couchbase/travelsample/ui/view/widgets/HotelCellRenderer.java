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

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.ui.view.Page;


public class HotelCellRenderer extends JPanel implements ListCellRenderer<Hotel> {
    private final JLabel name;
    private final JLabel location;

    public HotelCellRenderer() {
        super(new BorderLayout(), true);

        name = new JLabel();
        name.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));
        location = new JLabel();
        location.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));

        add(name, BorderLayout.NORTH);
        add(location, BorderLayout.SOUTH);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(204, 42, 47)));
    }

    public Component getListCellRendererComponent(
        JList list,
        Hotel hotel,
        int idx,
        boolean selected,
        boolean focused) {
        name.setText(hotel.getName());
        location.setText(hotel.getAddress());
        setForeground(focused ? Page.COLOR_TEXT : Page.COLOR_UNFOCUSED);
        setBackground(selected ? Page.COLOR_SELECTED : Page.COLOR_BACKGROUND);
        return this;
    }
}
