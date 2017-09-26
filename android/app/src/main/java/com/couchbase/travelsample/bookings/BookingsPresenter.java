package com.couchbase.travelsample.bookings;

import android.support.annotation.NonNull;

/**
 * Listens to user actions from the UI, retrieves the data and updates the UI as required.
 */
public class BookingsPresenter {

    private final BookingsContract.View mBookingsView;

    public BookingsPresenter(@NonNull BookingsContract.View bookingsView) {
        this.mBookingsView = bookingsView;
    }
}
