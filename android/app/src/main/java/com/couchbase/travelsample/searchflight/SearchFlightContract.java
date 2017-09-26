package com.couchbase.travelsample.searchflight;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface SearchFlightContract {

    interface UserActionsListener {

        void saveFlight(String title, String description);

    }
}
