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
import com.couchbase.lite.MutableDocument;


public class Hotel {
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_ADDRESS = "address";


    @Nonnull
    public static Hotel fromJSON(@Nonnull JSONObject json) {
        return new Hotel(
            json.getString(PROP_ID),
            json.getString(PROP_NAME),
            json.getString(PROP_ADDRESS));
    }

    @Nullable
    public static Hotel fromDictionary(@Nullable Dictionary dict) {
        if (dict == null) { return null; }
        return new Hotel(
            dict.getString(PROP_ID),
            dict.getString(PROP_NAME),
            dict.getString(PROP_ADDRESS));
    }

    @Nullable
    public static MutableDocument toDocument(@Nullable Hotel hotel) {
        if (hotel == null) { return null; }

        final String id = hotel.getId();

        final MutableDocument doc = new MutableDocument(id);
        doc.setString(PROP_ID, id);
        doc.setString(PROP_NAME, hotel.getName());
        doc.setString(PROP_ADDRESS, hotel.getAddress());

        return doc;
    }

    @Nonnull
    private final String id;
    @Nullable
    private final String name;
    @Nullable
    private final String address;

    Hotel(@Nonnull String id, @Nullable String name, @Nullable String address) {
        if ((id == null) || id.isEmpty()) { throw new IllegalStateException("Hotel has no id"); }
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @Nonnull
    public String getId() { return id; }

    @Nullable
    public String getName() { return name; }

    @Nullable
    public String getAddress() { return address; }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        final Hotel hotel = (Hotel) o;
        return id.equals(hotel.id);
    }
}
