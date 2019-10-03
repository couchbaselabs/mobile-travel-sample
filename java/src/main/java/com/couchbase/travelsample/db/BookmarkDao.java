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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.couchbase.lite.ArrayFunction;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Join;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.model.Hotel;

import static com.couchbase.travelsample.db.LocalStore.GUEST_DOC_TYPE;


public class BookmarkDao {
    private final LocalStore db;
    private final DBExecutor exec;

    @Inject
    public BookmarkDao(LocalStore db, DBExecutor exec) {
        this.db = db;
        this.exec = exec;
    }

    // SELECT bookmark.*, hotel.*
    // FROM DATABASE as bookmark
    // JOIN DATABASE as hotel ON bookmark.hotels CONTAINS hotel.meta.id
    // WHERE bookmark.type = "bookmarkedhotels"
    public void getBookmarks(Consumer<List<Hotel>> listener) {
        exec.submit(
            () -> {
                Database database = db.getDatabase();

                DataSource bookmark = DataSource.database(database).as("bookmark");
                DataSource hotel = DataSource.database(database).as("hotel");

                return QueryBuilder
                    .select(SelectResult.all().from("bookmark"), SelectResult.all().from("hotel"))
                    .from(bookmark)
                    .join(Join.join(hotel)
                        .on(ArrayFunction
                            .contains(Expression.property("hotels").from("bookmark"), Meta.id.from("hotel"))))
                    .where(Expression.property("type").from("bookmark").equalTo(Expression.string(GUEST_DOC_TYPE)));
            },
            query -> query.addChangeListener(change -> onBookmarks(change, listener)));
    }

    public void addBookmark(@Nonnull Hotel hotel) {
        exec.submit(
            () -> {
                Database database = db.getDatabase();

                // Create a hotel document if it doesn't exist
                String id = hotel.getId();
                if (id == null) { return null; }

                Document hotelDoc = database.getDocument(id);

                if (hotelDoc == null) { database.save(Hotel.toDocument(hotel)); }

                // Get the guest document
                Document doc = database.getDocument(LocalStore.GUEST_DOC_ID);
                MutableDocument mDoc;
                if (doc != null) { mDoc = doc.toMutable(); }
                else {
                    mDoc = new MutableDocument(LocalStore.GUEST_DOC_ID);
                    mDoc.setString("type", LocalStore.GUEST_DOC_TYPE);
                    mDoc.setArray("hotels", new MutableArray());
                }

                // Add the bookmarked hotel id to the hotels array
                MutableArray hotels = mDoc.getArray("hotels");
                hotels.addString(id);
                database.save(mDoc);

                return null;
            });
    }

    public void removeBookmark(@Nonnull Hotel hotel) {
        final String id = hotel.getId();
        if (id == null) { return; }

        exec.submit(
            () -> {
                Database database = db.getDatabase();

                Document document = database.getDocument(id);
                database.delete(document);

                MutableDocument guestDoc = database.getDocument("user::guest").toMutable();
                MutableArray hotelIds = guestDoc.getArray("hotels").toMutable();
                for (int i = 0; i < hotelIds.count(); i++) {
                    if (hotelIds.getString(i).equals(id)) { hotelIds.remove(i); }
                }

                database.save(guestDoc);

                return null;
            });
    }

    private void onBookmarks(QueryChange change, Consumer<List<Hotel>> listener) {
        ResultSet results = change.getResults();
        List<Hotel> bookmarks = new ArrayList<>();
        for (Result result : results) { bookmarks.add(Hotel.fromDictionary(result.getDictionary(1))); }
        listener.accept(bookmarks);
    }
}
