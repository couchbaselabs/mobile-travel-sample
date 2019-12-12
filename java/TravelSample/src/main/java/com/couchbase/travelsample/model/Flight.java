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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONObject;

import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;


public class Flight {
    @Nonnull
    public static Flight fromJSON(@Nonnull JSONObject json) {
        System.out.println("got flight: " + json);
        return new Flight(
            json.getString("flight"),
            json.getString("name"),
            json.getString("sourceairport"),
            json.getString("destinationairport"),
            json.getString("equipment"),
            json.getString("utc"),
            json.getInt("flighttime"),
            json.getFloat("price"));
    }

    @Nonnull
    private final String flight;
    @Nonnull
    private final String carrier;
    @Nonnull
    private final String source;
    @Nonnull
    private final String dest;
    @Nonnull
    private final String equipment;
    @Nonnull
    private final String depart;
    private final int flightTime;
    private final float price;

    public Flight(
        @Nonnull String flight,
        @Nonnull String carrier,
        @Nonnull String source,
        @Nonnull String dest,
        @Nonnull String equipment,
        @Nonnull String depart,
        int flightTime,
        float price) {
        this.flight = flight;
        this.carrier = carrier;
        this.source = source;
        this.dest = dest;
        this.equipment = equipment;
        this.depart = depart;
        this.flightTime = flightTime;
        this.price = price;
    }

    @Nonnull
    public String getFlight() { return flight; }

    @Nonnull
    public String getCarrier() { return carrier; }

    @Nonnull
    public String getSourceAirport() { return source; }

    @Nonnull
    public String getDestinationAirport() { return dest; }

    @Nonnull
    public String getEquipment() { return equipment; }

    @Nonnull
    public String getDepartureTime() { return depart; }

    public int getFlightTime() { return flightTime; }

    public float getPrice() { return price; }

    @Override
    public int hashCode() { return Objects.hash(flight); }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        return flight.equals(((Flight) o).flight);
    }
}
