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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

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
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.model.Hotel;


/**
 * Rely *heavily* on the single-threaded executor.
 * If these things always happen in order, then it is impossible for the
 * dao to be closed, while it is in the middle of doing something.
 */
public class BookmarkDao {
    private static final Logger LOGGER = Logger.getLogger(BookmarkDao.class.getName());

    public static final String ID_GUEST_DOC = "user::guest";
    public static final String TYPE_GUEST_DOC = "bookmarkedhotels";
    public static final String PROP_TYPE = "type";
    public static final String PROP_BOOKMARKS = "hotels";

    private final DbManager db;
    private final DbExecutor exec;

    @Inject
    public BookmarkDao(DbManager db, DbExecutor exec) {
        this.db = db;
        this.exec = exec;
    }

    public void getBookmarks(@Nonnull Consumer<List<Hotel>> listener) {
        exec.submit(this::queryBookmarksAsync, listener);
    }

    public void addBookmarks(@Nonnull Set<Hotel> hotels) { exec.submit(() -> addBookmarksAsync(hotels)); }

    public void removeBookmarks(@Nonnull Set<Hotel> hotels) { exec.submit(() -> removeBookmarksAsync(hotels)); }

    @Nullable
    private List<Hotel> queryBookmarksAsync() throws CouchbaseLiteException {
        final List<Hotel> bookmarks = new ArrayList<>();

        final Database database = db.getDatabase();
        if (database == null) { return bookmarks; }

        final ResultSet results = QueryBuilder
            .select(SelectResult.all().from("bookmark"), SelectResult.all().from("hotel"))
            .from(DataSource.database(database).as("bookmark"))
            .join(Join.join(DataSource.database(database).as("hotel"))
                .on(ArrayFunction.contains(Expression.property(PROP_BOOKMARKS)
                    .from("bookmark"), Meta.id.from("hotel"))))
            .where(Expression.property(PROP_TYPE).from("bookmark")
                .equalTo(Expression.string(TYPE_GUEST_DOC)))
            .execute();

        for (Result result : results) { bookmarks.add(Hotel.fromDictionary(result.getDictionary(1))); }

        LOGGER.log(Level.INFO, "Found bookmarks: " + bookmarks);
        return bookmarks;
    }

    @Nullable
    private Void addBookmarksAsync(@Nonnull Set<Hotel> hotels) throws CouchbaseLiteException {
        final Database database = db.getDatabase();
        if (database == null) { return null; }

        final Set<String> ids = new HashSet<>();
        for (Hotel hotel : hotels) {
            final String id = hotel.getId();
            if (id == null) {
                LOGGER.log(Level.WARNING, "Hotel has null ID in remove bookmark: " + hotel);
                continue;
            }

            final Document hotelDoc = database.getDocument(id);
            if (hotelDoc == null) { database.save(Hotel.toDocument(hotel)); }

            ids.add(id);
        }

        bookmarkIds(database, ids);

        return null;
    }

    @Nullable
    private Void removeBookmarksAsync(@Nonnull Set<Hotel> hotels) throws CouchbaseLiteException {
        final Database database = db.getDatabase();
        if (database == null) { return null; }

        final Set<String> ids = new HashSet<>();
        for (Hotel hotel : hotels) {
            final String id = hotel.getId();
            if (id == null) {
                LOGGER.log(Level.WARNING, "Hotel has null ID in remove bookmark: " + hotel);
                continue;
            }
            ids.add(id);
        }

        unbookmarkIds(database, ids);

        for (String id : ids) {
            final Document hotelDoc = database.getDocument(id);
            if (hotelDoc == null) {
                LOGGER.log(Level.WARNING, "Hotel not found in remove bookmark: " + id);
                continue;
            }
            database.delete(hotelDoc);
        }

        return null;
    }

    private void bookmarkIds(Database database, Set<String> ids) throws CouchbaseLiteException {
        final MutableDocument guestDoc = getGuestDoc(database);

        final Set<String> currentBookmarks = new HashSet<>();

        final MutableArray bookmarks = guestDoc.getArray(PROP_BOOKMARKS);
        if (bookmarks != null) {
            for (int i = 0; i < bookmarks.count(); i++) { currentBookmarks.add(bookmarks.getString(i)); }
        }

        currentBookmarks.addAll(ids);
        LOGGER.log(Level.INFO, "Bookmarking: " + currentBookmarks);

        final MutableArray newBookmarks = new MutableArray();
        for (String bookmark : currentBookmarks) { newBookmarks.addString(bookmark); }

        guestDoc.setArray(PROP_BOOKMARKS, newBookmarks);

        database.save(guestDoc);
    }

    private void unbookmarkIds(Database database, Set<String> ids) throws CouchbaseLiteException {
        LOGGER.log(Level.INFO, "Unbookmarking: " + ids);

        final MutableDocument guestDoc = getGuestDoc(database);

        final MutableArray bookmarks = guestDoc.getArray(PROP_BOOKMARKS);
        if (bookmarks == null) { return; }

        for (int i = bookmarks.count() - 1; i >= 0; i--) {
            if (ids.contains(bookmarks.getString(i))) { bookmarks.remove(i); }
        }

        database.save(guestDoc);
    }

    private MutableDocument getGuestDoc(Database database) {
        final Document doc = database.getDocument(ID_GUEST_DOC);

        if (doc != null) { return doc.toMutable(); }

        final MutableDocument mDoc = new MutableDocument(ID_GUEST_DOC);
        mDoc.setString(PROP_TYPE, TYPE_GUEST_DOC);

        return mDoc;
    }
}
