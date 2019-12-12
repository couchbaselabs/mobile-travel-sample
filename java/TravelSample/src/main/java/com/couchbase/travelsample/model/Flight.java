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

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONObject;

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableDictionary;


public class Flight {
    public static final String PROP_FLIGHT = "flight";
    public static final String PROP_NAME = "name";
    public static final String PROP_ORIGIN = "sourceairport";
    public static final String PROP_DESTINATION = "destinationairport";
    public static final String PROP_EQUIPMENT = "equipment";
    public static final String PROP_DEPARTURE_DATE = "date";
    public static final String PROP_DEPARTURE_TIME = "utc";
    public static final String PROP_FLIGHT_TIME = "flighttime";
    public static final String PROP_PRICE = "price";

    @Nullable
    public static Flight fromJSON(@Nullable JSONObject json) {
        return (json == null)
            ? null
            : new Flight(
                json.getString(PROP_FLIGHT),
                (!json.has(PROP_NAME)) ? null : json.getString(PROP_NAME),
                (!json.has(PROP_ORIGIN)) ? null : json.getString(PROP_ORIGIN),
                (!json.has(PROP_DESTINATION)) ? null : json.getString(PROP_DESTINATION),
                (!json.has(PROP_EQUIPMENT)) ? null : json.getString(PROP_EQUIPMENT),
                (!json.has(PROP_DEPARTURE_TIME)) ? null : json.getString(PROP_DEPARTURE_TIME),
                (!json.has(PROP_DEPARTURE_DATE)) ? null : json.getString(PROP_DEPARTURE_DATE),
                (!json.has(PROP_FLIGHT_TIME)) ? 0 : json.getInt(PROP_FLIGHT_TIME),
                (!json.has(PROP_PRICE)) ? 0F : json.getFloat(PROP_PRICE));
    }

    @Nullable
    public static Flight fromDictionary(@Nullable Dictionary dict) {
        return (dict == null)
            ? null
            : new Flight(
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
    public static MutableDictionary toDictionary(@Nullable Flight flight) {
        if (flight == null) { return null; }
        final MutableDictionary dict = new MutableDictionary();
        dict.setString(PROP_FLIGHT, flight.flight);
        if (flight.carrier != null) { dict.setString(PROP_NAME, flight.carrier); }
        if (flight.origin != null) { dict.setString(PROP_ORIGIN, flight.origin); }
        if (flight.destination != null) { dict.setString(PROP_DESTINATION, flight.destination); }
        if (flight.equipment != null) { dict.setString(PROP_EQUIPMENT, flight.equipment); }
        if (flight.departDate != null) { dict.setString(PROP_DEPARTURE_DATE, flight.departDate); }
        if (flight.departTime != null) { dict.setString(PROP_DEPARTURE_TIME, flight.departTime); }
        if (flight.flightTime != 0) { dict.setInt(PROP_FLIGHT_TIME, flight.flightTime); }
        if (flight.price != 0F) { dict.setFloat(PROP_PRICE, flight.price); }
        return dict;
    }

    @Nonnull
    private final String flight;
    @Nullable
    private final String carrier;
    @Nullable
    private final String origin;
    @Nullable
    private final String destination;
    @Nullable
    private final String equipment;
    @Nullable
    private final String departDate;
    @Nullable
    private final String departTime;
    private final int flightTime;
    private final float price;

    Flight(
        @Nonnull String flight,
        @Nullable String carrier,
        @Nullable String origin,
        @Nullable String destination,
        @Nullable String equipment,
        @Nullable String departDate,
        @Nullable String departTime,
        int flightTime,
        float price) {
        if (flight == null) { throw new IllegalArgumentException("Flight may not be null"); }
        this.flight = flight;
        this.carrier = carrier;
        this.origin = origin;
        this.destination = destination;
        this.equipment = equipment;
        this.departDate = departTime;
        this.departTime = departTime;
        this.flightTime = flightTime;
        this.price = price;
    }

    @Nonnull
    public String getFlight() { return flight; }

    @Nullable
    public String getCarrier() { return carrier; }

    @Nullable
    public String getOriginAirport() { return origin; }

    @Nullable
    public String getDestinationAirport() { return destination; }

    @Nullable
    public String getEquipment() { return equipment; }

    @Nullable
    public String getDepartureDate() { return departDate; }

    @Nullable
    public String getDepartureTime() { return departTime; }

    public int getFlightTime() { return flightTime; }

    public float getPrice() { return price; }

    @Override
    public int hashCode() { return Objects.hash(flight); }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Flight other = (Flight) o;
        boolean eq = flight.equals(other.flight);
        if ((departDate != null) && (other.departDate != null)) { eq = eq || departDate.equals(other.departDate); }
        if ((departTime != null) && (other.departTime != null)) { eq = eq || departTime.equals(other.departTime); }

        return eq;
    }
}
