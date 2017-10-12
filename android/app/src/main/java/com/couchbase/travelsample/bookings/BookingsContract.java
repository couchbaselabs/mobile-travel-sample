package com.couchbase.travelsample.bookings;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface BookingsContract {

    interface View {

        void showBookings(List<String> data);

    }

    interface UserActionsListener {

        void fetchUserBookings();

    }

}
