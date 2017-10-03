package com.couchbase.travelsample.hotels;

import org.json.JSONArray;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface HotelsContract {

    interface View {

        void showHotels(JSONArray hotels);

    }

    interface UserActionsListener {

        void fetchHotels();

    }

}
