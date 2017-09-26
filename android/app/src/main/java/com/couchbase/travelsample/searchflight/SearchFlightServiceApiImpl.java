package com.couchbase.travelsample.searchflight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Implementation of the SearchFlight Service API that looks up for flights.
 */
public class SearchFlightServiceApiImpl implements SearchFlightApi {

    @Override
    public void searchFlights(SearchFlightApiCallback<List<HashMap>> callback) {
        /* Request flights from the back-end API */
        callback.onLoaded(new ArrayList<HashMap>());
    }
}
