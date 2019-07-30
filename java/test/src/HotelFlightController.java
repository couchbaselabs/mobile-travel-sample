import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelFlightController {
    private HotelFlightModel hotelFlightModel;
    private HotelFlightView hotelFlightView;
    private final static Logger LOGGER = Logger.getLogger(HotelFlightView.class.getName());

    public HotelFlightController(HotelFlightModel m, HotelFlightView v) {
        hotelFlightModel = m;
        hotelFlightView = v;
    }

    public void initController() {
        hotelFlightView.getSearchButton().addActionListener(e -> searchButtonPressed());
    }

    private void searchButtonPressed() {
        String originInputText = hotelFlightView.getOriginInput();
        String destInputText = hotelFlightView.getDestInput();
        LOGGER.log(Level.INFO, "Origin input: " + originInputText);
        LOGGER.log(Level.INFO, "Destination input: " + destInputText);
    }
}
