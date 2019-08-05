package com.couchbase.travelsample.view;

import javax.swing.*;

public class LoginView {

    public JPanel panel;
    private JButton guestButton;
    private JButton loginButton;
    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private JLabel image;
    private JLabel username;
    private JLabel password;
    private JLabel planeImage;
    public JFrame login;

    public LoginView() {
        login = new JFrame("com.couchbase.travelsample.view.LoginView");
        login.setContentPane(panel);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.pack();
        login.setExtendedState(JFrame.MAXIMIZED_BOTH);
        login.setVisible(true);
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

    public void setVisible() {
        login.setVisible(true);
    }

    public void setInvisible() {
        login.setVisible(false);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        image = new JLabel(new ImageIcon("cbtravel_logo.png"));
        planeImage = new JLabel(new ImageIcon("plane.png"));
    }
}