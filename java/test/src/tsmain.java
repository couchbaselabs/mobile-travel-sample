import javax.swing.*;

public class tsmain {

    public static void main(String[] args) {
        //login screen
        JFrame login = new JFrame("TravelSampleLogin");
        login.setContentPane(new TravelSampleLogin().panel1);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.pack();
        login.setExtendedState(JFrame.MAXIMIZED_BOTH);
        login.setVisible(true);

        //login
        int i = 0;
        while(i < 1) {
            if (TravelSampleLogin.loginCheck == 1) {
                login.setVisible(false);
                System.out.println("Successful login");
                JFrame search = new JFrame("HotelFlightScreen");
                search.setContentPane(new HotelFlightScreen().panel1);
                search.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                search.pack();
                search.setExtendedState(JFrame.MAXIMIZED_BOTH);
                search.setVisible(true);
                i++;
            } else if (TravelSampleLogin.loginCheck == 2) {
                login.setVisible(false);
                System.out.println("Guest login");
                JFrame search = new JFrame("HotelFlightScreen");
                search.setContentPane(new HotelFlightScreen().panel1);
                search.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                search.pack();
                search.setExtendedState(JFrame.MAXIMIZED_BOTH);
                search.setVisible(true);
                i++;
            } else if (TravelSampleLogin.loginCheck == 0) {
                System.out.println("Error logging in: Incorrect username or password");
                TravelSampleLogin.loginCheck = -1;
            } else System.out.print("");
        }
    }
}