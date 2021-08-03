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

import java.math.BigDecimal;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;


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
    public static Flight fromJSON(@Nullable JSONObject json) throws JSONException {
        return (json == null)
            ? null
            : new Flight(
                json.getString(PROP_FLIGHT),
                (!json.has(PROP_NAME)) ? null : json.getString(PROP_NAME),
                (!json.has(PROP_ORIGIN)) ? null : json.getString(PROP_ORIGIN),
                (!json.has(PROP_DESTINATION)) ? null : json.getString(PROP_DESTINATION),
                (!json.has(PROP_EQUIPMENT)) ? null : json.getString(PROP_EQUIPMENT),
                (!json.has(PROP_DEPARTURE_TIME)) ? null : json.getString(PROP_DEPARTURE_TIME),
                (!json.has(PROP_FLIGHT_TIME)) ? 0 : json.getInt(PROP_FLIGHT_TIME),
                (!json.has(PROP_PRICE)) ? 0F : json.getLong(PROP_PRICE));//there's no getFloat
    }


    @Nonnull
    protected final String flight;
    @Nullable
    protected final String carrier;
    @Nullable
    protected final String origin;
    @Nullable
    protected final String destination;
    @Nullable
    protected final String equipment;
    @Nullable
    protected final String departTime;
    protected final int flightTime;
    protected final float price;

    Flight(
        @Nonnull String flight,
        @Nullable String carrier,
        @Nullable String origin,
        @Nullable String destination,
        @Nullable String equipment,
        @Nullable String departTime,
        int flightTime,
        float price) {
        if (flight == null) { throw new IllegalArgumentException("Flight may not have null flight id"); }
        this.flight = flight;
        this.carrier = carrier;
        this.origin = origin;
        this.destination = destination;
        this.equipment = equipment;
        this.departTime = departTime;
        this.flightTime = flightTime;
        this.price = price;
    }

    @Nonnull
    public final String getFlight() { return flight; }

    @Nullable
    public final String getCarrier() { return carrier; }

    @Nullable
    public final String getOriginAirport() { return origin; }

    @Nullable
    public final String getDestinationAirport() { return destination; }

    @Nullable
    public final String getEquipment() { return equipment; }

    @Nullable
    public final String getDepartureTime() { return departTime; }

    public final int getFlightTime() { return flightTime; }

    public final float getPrice() { return price; }

    @Override
    public int hashCode() { return Objects.hash(flight); }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if ((o == null) || (this.getClass() == o.getClass())) { return false; }
        return flight.equals(((Flight) o).flight);
    }

    @Nonnull
    @Override
    public String toString() {
        return "Flight[" + flight + ", " + carrier + ", " + equipment + ", $" + price
            + ", " + origin + "@" + departTime + " => " + destination + "," + flightTime + "]";
    }
}
