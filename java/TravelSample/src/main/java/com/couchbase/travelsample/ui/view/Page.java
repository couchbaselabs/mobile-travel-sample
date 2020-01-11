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

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.couchbase.travelsample.ui.controller.PageController;


public abstract class Page<T extends PageController> {
    public static final Color COLOR_ACCENT = new Color(204, 42, 47);
    public static final Color COLOR_TEXT = Color.BLACK;
    public static final Color COLOR_UNFOCUSED = Color.GRAY;
    public static final Color COLOR_SELECTED = new Color(204, 42, 47, 100);
    public static final Color COLOR_BACKGROUND = Color.WHITE;

    public static final String LABEL_LOGIN = "LOGIN";
    public static final String LABEL_LOGOUT = "LOGOUT";


    @Nonnull
    protected final T controller;
    @Nonnull
    private final String name;

    @Nullable
    private JButton logoutButton;

    public Page(@Nonnull String name, @Nonnull T controller) {
        this.name = name;
        this.controller = controller;
    }

    @Nonnull
    public abstract JPanel getView();

    protected abstract void onOpen(@Nonnull Page<?> prev);

    protected abstract void onClose();

    @Nonnull
    public final String getName() { return name; }

    public final void open(@Nonnull Page<?> prev) {
        onOpen(prev);
        controller.setPrevPage(prev.getName());

        if (logoutButton != null) {
            logoutButton.setText(controller.isLoggedIn() ? LABEL_LOGOUT : LABEL_LOGIN);
            logoutButton.addActionListener(e -> controller.logout());
        }
    }

    public final void close() {
        onClose();
        controller.close();
    }

    protected void registerLogoutButton(@Nonnull JButton logoutButton) { this.logoutButton = logoutButton; }

    void setButtonEnabled(@Nonnull JButton button, boolean enabled) {
        button.setEnabled(enabled);
        button.setBackground(enabled ? COLOR_ACCENT : COLOR_SELECTED);
    }
}
