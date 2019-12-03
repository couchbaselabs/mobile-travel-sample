package com.couchbase.travelsample.bookings;

import java.util.List;
import java.util.Map;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface BookingsContract {

    interface View {

        void showBookings(List<Map<String, Object>> data);

    }

    interface UserActionsListener {

        void fetchUserBookings();

    }

}
