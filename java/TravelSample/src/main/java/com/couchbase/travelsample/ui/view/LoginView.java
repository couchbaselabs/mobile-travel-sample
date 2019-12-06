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

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.couchbase.travelsample.ui.controller.LoginController;


@Singleton
public final class LoginView extends Page<LoginController> {
    public static final String PAGE_NAME = "LOGIN";

    private JPanel panel;
    private JButton guestButton;
    private JButton loginButton;
    private JTextField username;
    private JPasswordField password;
    private JLabel logo;

    @Inject
    public LoginView(LoginController controller) {
        super(PAGE_NAME, controller);

        guestButton.addActionListener(e -> controller.loginAsGuest());

        loginButton.addActionListener(e -> controller.loginWithValidation(
            username.getText(),
            password.getPassword()));
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    public void open(@Nullable Page<?> prevPage) { }

    private void createUIComponents() {
        logo = new JLabel(new ImageIcon(LoginView.class.getResource("images/cbtravel_logo.png")));
    }
}