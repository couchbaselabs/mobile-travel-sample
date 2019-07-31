public class mainStateMachine {

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