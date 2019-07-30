//prerequisite: download JCalendar

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelFlightView {

    private JTabbedPane flightPane;
    public JPanel panel;
    private JTextField locationInput;
    private JTextField descInput;
    private JLabel locationLabel;
    private JLabel descLabel;
    private JButton searchButton;
    private JPanel originDate;
    private JPanel destDate;
    private JLabel descImage;
    private JLabel locationImage;
    private JLabel originFlight;
    private JLabel destFlight;
    private JTextField originInput;
    private JTextField destInput;
    private JButton lookUp;
    private JLabel origDate;
    private JLabel destinDate;
    private JDateChooser originChooser = new JDateChooser();
    private JDateChooser destChooser = new JDateChooser();
    private final static Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    public HotelFlightView() {

        JFrame search = new JFrame("HotelFlightView");
        search.setContentPane(panel);
        search.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        search.pack();
        search.setExtendedState(JFrame.MAXIMIZED_BOTH);
        search.setVisible(true);


               // String origDate = ((JTextField)originChooser.getDateEditor().getUiComponent()).getText();
               // String destDate = ((JTextField)destChooser.getDateEditor().getUiComponent()).getText();


       // originDate.add(originChooser);
      //  destDate.add(destChooser);
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public String getOriginInput() {
        return originInput.getText();
    }

    public String getDestInput() {
        return destInput.getText();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }
}
