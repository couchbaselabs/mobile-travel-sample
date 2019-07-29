//prerequisite: download JCalendar

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class hotelFlightScreen {
    private JTabbedPane flightPane;
    public JPanel panel1;
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
    private final static Logger LOGGER = Logger.getLogger(travelSampleMain.class.getName());

    public hotelFlightScreen() {
        //hotel logger
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String locationInputText = locationInput.getText();
                String descInputText = descInput.getText();
                LOGGER.log(Level.INFO, "Location input: " + locationInputText);
                LOGGER.log(Level.INFO, "Description input: " + descInputText);
            }
        });

        //flight logger
        lookUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String originInputText = originInput.getText();
                String destInputText = destInput.getText();
                LOGGER.log(Level.INFO, "Origin input: " + originInputText);
                LOGGER.log(Level.INFO, "Destination input " + destInputText);

                //date
                String origDate = ((JTextField)originChooser.getDateEditor().getUiComponent()).getText();
                String destDate = ((JTextField)destChooser.getDateEditor().getUiComponent()).getText();
                LOGGER.log(Level.INFO, "Origin date input: " + origDate);
                LOGGER.log(Level.INFO, "Arrival date input: " + destDate);
            }
        });

        originDate.add(originChooser);
        destDate.add(destChooser);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        locationImage = new JLabel(new ImageIcon("globe.png"));
        descImage = new JLabel(new ImageIcon("magglass.png"));
    }
}
