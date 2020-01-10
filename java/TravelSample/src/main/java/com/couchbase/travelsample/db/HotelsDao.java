//
// Copyright (c) 2020 Couchbase, Inc All rights reserved.
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
package com.couchbase.travelsample.db;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.FullTextExpression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.ui.controller.FlightSearchController;


public class HotelsDao {
    private static final Logger LOGGER = Logger.getLogger(FlightSearchController.class.getName());

    @Nonnull
    private final DbManager db;
    @Nonnull
    private final DbExecutor exec;

    @Inject
    public HotelsDao(@Nonnull DbManager db, @Nonnull DbExecutor exec) {
        this.db = db;
        this.exec = exec;
    }

    public void searchHotels(@Nonnull String location, @Nonnull String desc, @Nonnull Consumer<List<Hotel>> listener) {
        exec.submit(() -> searchHotelsAsync(location, desc), listener);
    }

    @Nonnull
    private List<Hotel> searchHotelsAsync(@Nonnull String location, @Nonnull String desc)
        throws CouchbaseLiteException {
        final List<Hotel> hotels = new ArrayList<>();

        final String loc = "%" + location + "%";

        final ResultSet results = QueryBuilder
            .select(SelectResult.expression(Meta.id), SelectResult.all())
            .from(DataSource.database(db.getDatabase()))
            .where(Expression.property(DbManager.PROP_DOC_TYPE).equalTo(Expression.string(Hotel.DOC_TYPE))
                .and(FullTextExpression.index(DbManager.FTS_INDEX_DESC).match(desc)
                    .and(Expression.property(Hotel.PROP_ADDRESS).like(Expression.string(loc))
                        .or(Expression.property(Hotel.PROP_CITY).like(Expression.string(loc)))
                        .or(Expression.property(Hotel.PROP_STATE).like(Expression.string(loc)))
                        .or(Expression.property(Hotel.PROP_COUNTRY).like(Expression.string(loc))))))
            .orderBy(Ordering.property(Hotel.PROP_NAME).ascending())
            .execute();


        for (Result result : results.allResults()) {
            if (result.count() < 2) { continue; }
            final Hotel hotel = Hotel.fromDictionary(result.getString(0), result.getDictionary(1));
            if (hotel != null) { hotels.add(hotel); }
        }

        return hotels;
    }
}
