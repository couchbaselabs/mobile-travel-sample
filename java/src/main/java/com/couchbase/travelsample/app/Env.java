package com.couchbase.travelsample.app;

import dagger.BindsInstance;
import dagger.Component;

import com.couchbase.travelsample.TravelSample;
import com.couchbase.travelsample.view.GuestView;
import com.couchbase.travelsample.view.VMFactory;


@Component
public interface Env {
    TravelSample app();

    VMFactory vmFactory();
}
