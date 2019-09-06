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
package com.couchbase.travelsample.view;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;


@Component
public class LoginView {
    public final JFrame login;
    public JPanel panel;

    private JButton guestButton;
    private JButton loginButton;
    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private JLabel image;
    private JLabel username;
    private JLabel password;
    private JLabel planeImage;

    public LoginView() {
        login = new JFrame("Login");
        login.setContentPane(panel);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.pack();
        login.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getGuestLoginButton() {
        return guestButton;
    }

    public String getUsernameInput() {
        return usernameInput.getText();
    }

    public String getPasswordInput() {
        return passwordInput.getText();
    }

    public void show() {
        login.setVisible(true);
    }

    public void hide() {
        login.setVisible(false);
    }

    public void dispose() {
        login.dispose();
    }

    private void createUIComponents() {
        image = new JLabel(new ImageIcon("cbtravel_logo.png"));
        planeImage = new JLabel(new ImageIcon("plane.png"));
    }
}