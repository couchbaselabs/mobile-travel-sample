package com.couchbase.travelsample.searchflight;

import com.couchbase.lite.ResultSet;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface SearchFlightContract {

    interface View {

        void showAirports(List<String> airports);

    }

    interface UserActionsListener {

        void startsWith(String prefix);

        void saveFlight(String title, String description);

    }
}
