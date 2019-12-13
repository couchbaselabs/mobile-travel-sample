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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.couchbase.travelsample.ui.controller.LoginController;


@Singleton
public final class LoginView extends Page<LoginController> {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    public static final String PAGE_NAME = "LOGIN";

    class LoginKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) {}

        public void keyTyped(KeyEvent e) { }

        public void keyReleased(KeyEvent e) {
            setLoginButtonEnabled((!username.getText().isEmpty()) && (password.getPassword().length > 0));
        }
    }


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

        loginButton.addActionListener(e -> {
            final String uname = username.getText();
            controller.loginWithValidation(uname, password.getPassword(), (err) -> loginFail(err, uname));
        });

        final LoginKeyListener keyListener = new LoginKeyListener();
        username.addKeyListener(keyListener);
        password.addKeyListener(keyListener);
        setLoginButtonEnabled(false);
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    protected void onOpen(@Nullable Page<?> prevPage) { }

    @Override
    protected void onClose() { }

    private void createUIComponents() {
        logo = new JLabel(new ImageIcon(LoginView.class.getResource("images/cbtravel_logo.png")));
    }

    void setLoginButtonEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
        loginButton.setBackground(enabled ? COLOR_ACCENT : COLOR_SELECTED);
    }

    void loginFail(@Nonnull Exception error, @Nonnull String username) {
        LOGGER.log(Level.WARNING, "login failure", error);

        JOptionPane.showMessageDialog(
            null,
            "Login failed for user " + username + ": " + error.getLocalizedMessage(),
            "Login Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
