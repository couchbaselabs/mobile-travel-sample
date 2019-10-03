package com.couchbase.travelsample.model;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONObject;

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableDocument;


public class Hotel {
    public static MutableDocument toDocument(Hotel hotel) {
        final String id = hotel.getId();

        final MutableDocument doc = new MutableDocument(id);
        if (id != null) { doc.setString("id", id); }
        doc.setString("name", hotel.getName());
        doc.setString("address", hotel.getAddress());

        return doc;
    }

    public static Hotel fromDictionary(@Nonnull Dictionary dict) {
        return new Hotel(dict.getString("id"), dict.getString("name"), dict.getString("address"));
    }

    public static Hotel fromJSON(@Nonnull JSONObject json) {
        String id = (!json.has("id")) ? null : json.getString("id");
        return new Hotel(id, json.getString("name"), json.getString("address"));
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
