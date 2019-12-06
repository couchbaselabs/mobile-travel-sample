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
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

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
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.model.Hotel;

import static com.couchbase.travelsample.db.LocalStore.GUEST_DOC_TYPE;


/**
 * Rely *heavily* on the single-threaded executor.
 * If these things always happen in order, then it is impossible for the
 * dao to be closed, while it is in the middle of doing something.
 */
public class BookmarkDao {
    private final static Logger LOGGER = Logger.getLogger(BookmarkDao.class.getName());

    private final LocalStore db;
    private final DBExecutor exec;

    @Inject
    public BookmarkDao(LocalStore db, DBExecutor exec) {
        this.db = db;
        this.exec = exec;
    }

    public void getBookmarks(@Nonnull Consumer<List<Hotel>> listener) {
        exec.submit(() -> queryBookmarksAsync(listener));
    }

    public void addBookmarks(@Nonnull Set<Hotel> hotels) { exec.submit(() -> addBookmarksAsync(hotels)); }

    public void removeBookmark(@Nonnull Hotel hotel) {
        final String id = hotel.getId();
        if (id == null) { return; }

        exec.submit(() -> addBookmarkAsync(id));
    }

    @Nullable
    private Void addBookmarkAsync(@Nonnull String id) throws CouchbaseLiteException {
        final Database database = db.getDatabase();
        if (database == null) { return null; }

        final Document document = database.getDocument(id);
        database.delete(document);

        final MutableDocument guestDoc = database.getDocument("user::guest").toMutable();
        final MutableArray hotelIds = guestDoc.getArray("hotels").toMutable();
        for (int i = 0; i < hotelIds.count(); i++) {
            if (hotelIds.getString(i).equals(id)) { hotelIds.remove(i); }
        }

        database.save(guestDoc);

        return null;
    }

    // SELECT bookmark.*, hotel.*
    // FROM DATABASE as bookmark
    // JOIN DATABASE as hotel ON bookmark.hotels CONTAINS hotel.meta.id
    // WHERE bookmark.type = "bookmarkedhotels"
    @Nullable
    private Void queryBookmarksAsync(@Nonnull Consumer<List<Hotel>> listener) {
        final Database database = db.getDatabase();
        if (database == null) { return null; }

        final DataSource bookmark = DataSource.database(database).as("bookmark");
        final DataSource hotel = DataSource.database(database).as("hotel");

        final Query query = QueryBuilder
            .select(SelectResult.all().from("bookmark"), SelectResult.all().from("hotel"))
            .from(bookmark)
            .join(Join.join(hotel)
                .on(ArrayFunction
                    .contains(Expression.property("hotels").from("bookmark"), Meta.id.from("hotel"))))
            .where(Expression.property("type").from("bookmark").equalTo(Expression.string(GUEST_DOC_TYPE)));

        db.registerQuery(query, query.addChangeListener(change -> onBookmarks(change, listener)));

        return null;
    }

    @Nullable
    private Void addBookmarksAsync(@Nonnull Set<Hotel> hotels) throws CouchbaseLiteException {
        final Database database = db.getDatabase();
        if (database == null) { return null; }

        for (Hotel hotel : hotels) {
            addBookmarkAsync(database, hotel);
        }

        return null;
    }

    @Nullable
    private Void addBookmarkAsync(@Nonnull Database database, @Nonnull Hotel hotel) throws CouchbaseLiteException {
        final String id = hotel.getId();
        if (id == null) {
            LOGGER.log(Level.WARNING, "Hotel has null ID: " + hotel);
            return null;
        }

        final Document hotelDoc = database.getDocument(id);

        if (hotelDoc == null) { database.save(Hotel.toDocument(hotel)); }

        // Get the guest document
        final Document doc = database.getDocument(LocalStore.GUEST_DOC_ID);
        final MutableDocument mDoc;
        if (doc != null) { mDoc = doc.toMutable(); }
        else {
            mDoc = new MutableDocument(LocalStore.GUEST_DOC_ID);
            mDoc.setString("type", LocalStore.GUEST_DOC_TYPE);
            mDoc.setArray("hotels", new MutableArray());
        }

        // Add the bookmarked hotel id to the hotels array
        final MutableArray hotels = mDoc.getArray("hotels");
        hotels.addString(id);
        database.save(mDoc);

        return null;
    }

    private void onBookmarks(QueryChange change, Consumer<List<Hotel>> listener) {
        final ResultSet results = change.getResults();
        final List<Hotel> bookmarks = new ArrayList<>();
        for (Result result : results) { bookmarks.add(Hotel.fromDictionary(result.getDictionary(1))); }
        SwingUtilities.invokeLater(() -> listener.accept(bookmarks));
    }
}
