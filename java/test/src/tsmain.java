public class tsmain {

    public static void main(String[] args) {

        //eventually this will work like this
        LoginModel loginModel = new LoginModel();
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginModel, loginView);
        loginController.initController();

        //need to know if guest or user
        HotelFlightModel hotelFlightModel = new HotelFlightModel();
        HotelFlightView hotelFlightView = new HotelFlightView();
        HotelFlightController hotelFlightController = new HotelFlightController(hotelFlightModel, hotelFlightView);
        hotelFlightController.initController();
    }
}