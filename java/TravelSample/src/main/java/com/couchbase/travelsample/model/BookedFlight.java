//
// Copyright (c) 2019 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.travelsample.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableDictionary;


public class BookedFlight extends Flight {
    public static final ThreadLocal<DateFormat> DATE_FORMAT
        = ThreadLocal.withInitial(() -> new SimpleDateFormat("MM/dd/yyyy"));

    public static BookedFlight bookFlight(@Nonnull Flight flight, @Nonnull Date departDate) {
        return new BookedFlight(
            flight.flight,
            flight.carrier,
            flight.origin,
            flight.destination,
            flight.equipment,
            DATE_FORMAT.get().format(departDate),
            flight.departTime,
            flight.flightTime,
            flight.price);
    }

    @Nullable
    public static BookedFlight fromDictionary(@Nullable Dictionary dict) {
        return (dict == null)
            ? null
            : new BookedFlight(
                dict.getString(PROP_FLIGHT),
                dict.getString(PROP_NAME),
                dict.getString(PROP_ORIGIN),
                dict.getString(PROP_DESTINATION),
                dict.getString(PROP_EQUIPMENT),
                dict.getString(PROP_DEPARTURE_DATE),
                dict.getString(PROP_DEPARTURE_TIME),
                dict.getInt(PROP_FLIGHT_TIME),
                dict.getFloat(PROP_PRICE));
    }

    @Nullable
    public static MutableDictionary toDictionary(@Nullable BookedFlight flight) {
        if (flight == null) { return null; }
        final MutableDictionary dict = new MutableDictionary();
        dict.setString(PROP_FLIGHT, flight.flight);
        dict.setString(PROP_DEPARTURE_DATE, flight.departDate);
        if (flight.carrier != null) { dict.setString(PROP_NAME, flight.carrier); }
        if (flight.origin != null) { dict.setString(PROP_ORIGIN, flight.origin); }
        if (flight.destination != null) { dict.setString(PROP_DESTINATION, flight.destination); }
        if (flight.equipment != null) { dict.setString(PROP_EQUIPMENT, flight.equipment); }
        if (flight.departTime != null) { dict.setString(PROP_DEPARTURE_TIME, flight.departTime); }
        if (flight.flightTime != 0) { dict.setInt(PROP_FLIGHT_TIME, flight.flightTime); }
        if (flight.price != 0F) { dict.setFloat(PROP_PRICE, flight.price); }
        return dict;
    }

    public static boolean equalsDict(Flight flight, MutableDictionary dict) {
        if (!(flight instanceof BookedFlight)) { return false; }
        final BookedFlight bookedFlight = (BookedFlight) flight;
        return bookedFlight.flight.equals(dict.getString(PROP_FLIGHT))
            && bookedFlight.departDate.equals(dict.getString(PROP_DEPARTURE_DATE));
    }

    @Nonnull
    private final String departDate;

    BookedFlight(
        @Nonnull String flight,
        @Nullable String carrier,
        @Nullable String origin,
        @Nullable String destination,
        @Nullable String equipment,
        @Nonnull String departDate,
        @Nullable String departTime,
        int flightTime,
        float price) {
        super(flight, carrier, origin, destination, equipment, departTime, flightTime, price);
        if (departDate == null) { throw new IllegalArgumentException("BookedFlight may not have null departure time"); }
        this.departDate = departDate;
    }

    @Override
    public int hashCode() { return Objects.hash(flight); }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if ((o == null) || (this.getClass() == o.getClass())) { return false; }
        final BookedFlight other = (BookedFlight) o;
        return flight.equals(other.flight) && departDate.equals(other.departDate);
    }

    @Override
    public String toString() { return "Booking[@" + departDate + ": " + super.toString() + "]"; }
}
