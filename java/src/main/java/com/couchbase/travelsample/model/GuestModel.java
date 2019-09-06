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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.couchbase.lite.ArrayFunction;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Join;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.SelectResult;


@Component
public class GuestModel {
    private static final String GUEST_DOC_ID = "user::guest";
    private static final String GUEST_DOC_TYPE = "bookmarkedhotels";


    private final DatabaseManager dbMgr;

    @Autowired
    public GuestModel(DatabaseManager dbMgr) { this.dbMgr = dbMgr; }

    public void bookmarkHotel(Map<String, Object> hotel) throws CouchbaseLiteException {
        Database database = dbMgr.getDatabase();

        // Create a hotel document if it doesn't exist
        String id = (String) hotel.get("id");
        Document hotelDoc = database.getDocument(id);
        if (hotelDoc == null) {
            database.save(new MutableDocument(id, hotel));
        }

        // Get the guest document
        Document doc = database.getDocument(GUEST_DOC_ID);
        MutableDocument mDoc;
        if (doc != null) {
            mDoc = doc.toMutable();
        }
        else {
            mDoc = new MutableDocument(GUEST_DOC_ID);
            mDoc.setString("type", GUEST_DOC_TYPE);
            mDoc.setArray("hotels", new MutableArray());
        }

        // Add the bookmarked hotel id to the hotels array
        MutableArray hotels = mDoc.getArray("hotels");
        hotels.addString(id);
        database.save(mDoc);
    }

    // SELECT bookmark.*, hotel.*
    // FROM DATABASE as bookmark
    // JOIN DATABASE as hotel ON bookmark.hotels CONTAINS hotel.meta.id
    // WHERE bookmark.type = "bookmarkedhotels"
    public void getBookmarks(QueryChangeListener listener) {
        Database database = dbMgr.getDatabase();
        DataSource bookmark = DataSource.database(database).as("bookmark");
        DataSource hotel = DataSource.database(database).as("hotel");

        Expression joinCondition = ArrayFunction.contains(
            Expression.property("hotels").from("bookmark"),
            Meta.id.from("hotel"));

        Query query = QueryBuilder
            .select(SelectResult.all().from("bookmark"), SelectResult.all().from("hotel"))
            .from(bookmark)
            .join(Join.join(hotel).on(joinCondition))
            .where(Expression.property("type").from("bookmark").equalTo(Expression.string(GUEST_DOC_TYPE)));
        query.addChangeListener(listener);
    }

    public void removeBookmark(String id) throws CouchbaseLiteException {
        Database database = dbMgr.getDatabase();
        Document document = database.getDocument(id);
        database.delete(document);

        MutableDocument guestDoc = database.getDocument("user::guest").toMutable();
        MutableArray hotelIds = guestDoc.getArray("hotels").toMutable();
        for (int i = 0; i < hotelIds.count(); i++) {
            if (hotelIds.getString(i).equals(id)) { hotelIds.remove(i); }
        }

        database.save(guestDoc);
    }
}
