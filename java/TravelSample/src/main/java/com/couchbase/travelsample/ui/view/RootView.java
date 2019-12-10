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
package com.couchbase.travelsample.ui.view;

import java.awt.CardLayout;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class RootView extends JFrame {
    private final JPanel cards;
    private final CardLayout cardLayout;

    @Inject
    public RootView() {
        super("Travel Sample");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        setContentPane(cards);
    }

    public void addPage(@Nonnull Page<?> page) { cards.add(page.getView(), page.getName()); }

    public void toPage(@Nonnull Page<?> page) { cardLayout.show(cards, page.getName()); }

    public void start(@Nonnull Page<?> page) {
        pack();
        toPage(page);
        setVisible(true);
    }
}
