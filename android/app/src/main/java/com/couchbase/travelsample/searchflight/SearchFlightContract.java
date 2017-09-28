package com.couchbase.travelsample.searchflight;

import com.couchbase.lite.ResultSet;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface SearchFlightContract {

    interface View {

        void showAirports(ResultSet result);

    }

    interface UserActionsListener {

        void startsWith(String prefix);

        void saveFlight(String title, String description);

    }
}
