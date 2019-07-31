package com.couchbase.travelsample;

import com.couchbase.travelsample.controller.HotelFlightController;
import com.couchbase.travelsample.controller.LoginController;
import com.couchbase.travelsample.model.HotelFlightModel;
import com.couchbase.travelsample.model.LoginModel;
import com.couchbase.travelsample.view.HotelFlightView;
import com.couchbase.travelsample.view.LoginView;

public class TravelSample {

    public static void main(String[] args) {

        LoginModel loginModel = new LoginModel();
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginModel, loginView);

        //need to know if guest or user
        HotelFlightModel hotelFlightModel = new HotelFlightModel();
        HotelFlightView hotelFlightView = new HotelFlightView();
        HotelFlightController hotelFlightController = new HotelFlightController(hotelFlightModel, hotelFlightView);
    }
}