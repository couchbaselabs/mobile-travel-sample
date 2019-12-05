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
package com.couchbase.travelsample.db;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.FullTextExpression;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.model.Hotel;


public class HotelDao {
    private final LocalStore db;
    private final DBExecutor exec;

    @Inject
    public HotelDao(@Nonnull LocalStore db, @Nonnull DBExecutor exec) {
        this.db = db;
        this.exec = exec;
    }

    public void searchHotels(
        @Nonnull String location,
        @Nonnull String description,
        @Nonnull Consumer<List<Hotel>> listener) {
        exec.submit(() -> searchHotelsAsync(location, description), listener);
    }

    public void fetchHotels(@Nonnull Consumer<List<Hotel>> listener) {
        exec.submit(this::fetchHotelsAsync, listener);
    }

    @Nullable
    private List<Hotel> fetchHotelsAsync() throws CouchbaseLiteException {
        if (!db.isOpen()) { return null; }

        return Hotel.fromResults(
            QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db.getDatabase()))
                .where(Expression.property("type").equalTo(Expression.string("hotel")))
                .execute());
    }

    @Nullable
    private List<Hotel> searchHotelsAsync(@Nonnull String location, @Nonnull String description)
        throws CouchbaseLiteException {
        if (!db.isOpen()) { return null; }

        return Hotel.fromResults(
            QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db.getDatabase()))
                .where(Expression.property("type").equalTo(Expression.string("hotel"))
                    .and(FullTextExpression.index("descFTSIndex").match(description)
                        .add(Expression.property("country")
                            .like(Expression.string("%" + location + "%"))
                            .or(Expression.property("city").like(Expression.string("%" + location + "%")))
                            .or(Expression.property("state").like(Expression.string("%" + location + "%")))
                            .or(Expression.property("address").like(Expression.string("%" + location + "%"))))))
                .execute());
    }
}
