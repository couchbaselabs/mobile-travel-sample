package com.couchbase.travelsample.app;

import javax.inject.Singleton;

import dagger.Component;

import com.couchbase.travelsample.TravelSample;


@Component
@Singleton
public interface Env {
    TravelSample app();
}
