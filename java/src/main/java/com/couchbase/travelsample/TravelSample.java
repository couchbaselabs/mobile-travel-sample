package com.couchbase.travelsample;

import com.couchbase.travelsample.controller.LoginController;

public class TravelSample {

    public static void main(String[] args) {
        LoginController loginController = new LoginController();
        loginController.show();
    }
}