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

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;

import org.json.JSONObject;


public class Hotel {
    @Nonnull
    public static Hotel fromJSON(@Nonnull JSONObject json) {
        String id = (!json.has("id")) ? null : json.getString("id");
        return new Hotel(id, json.getString("name"), json.getString("address"));
    }

    @Nullable
    public static Hotel fromDictionary(@Nullable Dictionary dict) {
        if (dict == null) { return null; }
        return new Hotel(dict.getString("id"), dict.getString("name"), dict.getString("address"));
    }

    @Nullable
    public static List<Hotel> fromResults(@Nullable ResultSet results) {
        if (results == null) { return null; }

        List<Hotel> hotels = new ArrayList<>();
        Result row;
        while ((row = results.next()) != null) {
            hotels.add(Hotel.fromDictionary(row.getDictionary("travel-sample")));
        }

        return hotels;
    }

    @Nullable
    public static MutableDocument toDocument(@Nullable Hotel hotel) {
        if (hotel == null) { return null; }

        final String id = hotel.getId();

        final MutableDocument doc = new MutableDocument(id);
        if (id != null) { doc.setString("id", id); }
        doc.setString("name", hotel.getName());
        doc.setString("address", hotel.getAddress());

        return doc;
    }

    @Nullable
    private final String id;
    @Nonnull
    private final String name;
    @Nonnull
    private final String address;

    public Hotel(@Nullable String id, @Nonnull String name, @Nonnull String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @Nullable
    public String getId() { return id; }

    @Nonnull
    public String getName() { return name; }

    @Nonnull
    public String getAddress() { return address; }

    @Override
    public int hashCode() { return Objects.hash(name, address); }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Hotel hotel = (Hotel) o;
        return name.equals(hotel.name) && address.equals(hotel.address);
    }
}
