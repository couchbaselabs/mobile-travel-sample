import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class travelSampleMain {
    public JPanel panel1;
    private JButton PROCEEDASGUESTButton;
    private JButton SIGNINButton;
    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private JLabel image;
    private JLabel username;
    private JLabel password;
    private JLabel planeImage;
    private final static Logger LOGGER = Logger.getLogger(travelSampleMain.class.getName());
    public static int loginCheck = -1;

    public travelSampleMain() {
        SIGNINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usernameInputText = usernameInput.getText();
                String passwordInputText = passwordInput.getText();
                LOGGER.log(Level.INFO, "Username input: " + usernameInputText);
                LOGGER.log(Level.INFO, "Password input: " + passwordInputText);

                loginCheck = (usernameInputText.equals("Admin") && passwordInputText.equals("password")) ? 1 : 0;

                System.out.println(loginCheck);
            }
        });

        PROCEEDASGUESTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                LOGGER.log(Level.INFO, "Guest button pressed");
                loginCheck = 2;

                System.out.println(loginCheck);
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        image = new JLabel(new ImageIcon("cbtravel_logo.png"));
        planeImage = new JLabel(new ImageIcon("plane.png"));
    }
}
