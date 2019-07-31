package com.couchbase.travelsample.model;

import com.couchbase.travelsample.LoginState;

public class LoginModel {

    private LoginState loginState;

    public LoginModel() {
        loginState = LoginState.NO_LOGIN;
    }

    public void setLoginState(LoginState loginState) {
        this.loginState = loginState;
    }

    public LoginState getLoginState() {
        return loginState;
    }
}
