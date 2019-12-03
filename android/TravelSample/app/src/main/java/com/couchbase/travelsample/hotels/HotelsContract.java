package com.couchbase.travelsample.hotels;

import java.util.List;
import java.util.Map;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface HotelsContract {

    interface View {

        void showHotels(List<Map<String, Object>> hotels);

    }

    interface UserActionsListener {

        void fetchHotels(String location, String description);

        void bookmarkHotels(Map<String, Object> hotel);

        void queryHotels(String location, String description);

    }

}
