package com.couchbase.travelsample;

import com.couchbase.travelsample.controller.LoginController;
import com.couchbase.travelsample.model.LoginModel;
import com.couchbase.travelsample.view.LoginView;

public class TravelSample {

    public static void main(String[] args) {
        LoginModel loginModel = new LoginModel();
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginModel, loginView);
    }
}