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

import javax.inject.Inject;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.FullTextExpression;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.model.Hotel;


public class HotelDao {
    private final LocalStore db;
    private final DBExecutor exec;

    @Inject
    public HotelDao(LocalStore db, DBExecutor exec) {
        this.db = db;
        this.exec = exec;
    }

    public void searchHotels(String location, String description, Consumer<List<Hotel>> completion) {
        exec.submit(
            () -> {
                Expression descExp = FullTextExpression.index("descFTSIndex").match(description);
                Expression locationExp = Expression.property("country")
                    .like(Expression.string("%" + location + "%"))
                    .or(Expression.property("city").like(Expression.string("%" + location + "%")))
                    .or(Expression.property("state").like(Expression.string("%" + location + "%")))
                    .or(Expression.property("address").like(Expression.string("%" + location + "%")));

                Expression searchExp = descExp.and(locationExp);
                Query hotelSearchQuery = QueryBuilder
                    .select(SelectResult.all())
                    .from(DataSource.database(db.getDatabase()))
                    .where(Expression.property("type").equalTo(Expression.string("hotel")).and(searchExp));

                return Hotel.fromResults(hotelSearchQuery.execute());
            },
            completion::accept);
    }
}
