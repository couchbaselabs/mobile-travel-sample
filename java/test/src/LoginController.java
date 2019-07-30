import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private LoginModel loginModel;
    private LoginView loginView;
    private final static Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    public LoginController(LoginModel m, LoginView v) {
        loginModel = m;
        loginView = v;
    }

    public void initController() {
        loginView.getLoginButton().addActionListener(e -> loginButtonPressed());
        loginView.getGuestLoginButton().addActionListener(e -> guestLoginButtonPressed());
    }

    private void loginButtonPressed() {
        String usernameInputText = loginView.getUsernameInput();
        String passwordInputText = loginView.getPasswordInput();
        LOGGER.log(Level.INFO, "Username input: " + usernameInputText);
        LOGGER.log(Level.INFO, "Password input: " + passwordInputText);
    }

    private void guestLoginButtonPressed() {
        LOGGER.log(Level.INFO, "Guest button pressed");
    }
}
