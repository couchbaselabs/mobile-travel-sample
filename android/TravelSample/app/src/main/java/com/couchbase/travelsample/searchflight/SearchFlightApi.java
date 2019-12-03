package com.couchbase.travelsample.searchflight;

import java.util.HashMap;
import java.util.List;

/**
 * Defines an interface to the service API that is used by this application. All data request should
 * be piped through this interface.
 */
public interface SearchFlightApi {

    interface SearchFlightApiCallback<T> {
        void onLoaded(T flights);
    }

    void searchFlights(SearchFlightApiCallback<List<HashMap>> callback);

}
