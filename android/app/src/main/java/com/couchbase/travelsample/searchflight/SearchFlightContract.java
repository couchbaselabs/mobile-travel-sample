package com.couchbase.travelsample.searchflight;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface SearchFlightContract {

    interface View {

        void showAirports(List<String> airports, String tag);

        void showFlights(List<List<JSONObject>> flights);

    }

    interface UserActionsListener {

        void startsWith(String s, String prefix);

        void fetchFlights(String origin, String destination, String from, String to);

        void saveFlight(List<JSONObject> flight);

    }
}
