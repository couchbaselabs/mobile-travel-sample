import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private LoginModel loginModel;
    private LoginView loginView;
    private final static Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    public LoginController(LoginModel model, LoginView view) {
        loginModel = model;
        loginView = view;
        this.initController();
    }

    public void initController() {
        loginView.getLoginButton().addActionListener(e -> loginButtonPressed());
        loginView.getGuestLoginButton().addActionListener(e -> guestLoginButtonPressed());

        System.out.println(loginModel.getLoginState().toString());
    }

    private void loginButtonPressed() {
        String usernameInputText = loginView.getUsernameInput();
        String passwordInputText = loginView.getPasswordInput();
        LOGGER.log(Level.INFO, "Username input: " + usernameInputText);
        LOGGER.log(Level.INFO, "Password input: " + passwordInputText);

        loginModel.setLoginState(LoginState.USER_LOGIN);
        System.out.println(loginModel.getLoginState().toString());
    }

    private void guestLoginButtonPressed() {
        LOGGER.log(Level.INFO, "Guest button pressed");

        loginModel.setLoginState(LoginState.GUEST_LOGIN);
        System.out.println(loginModel.getLoginState().toString());
    }
}
