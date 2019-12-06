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
import com.couchbase.lite.QueryBuilder;
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
        exec.submit(
            this::queryBookmarksAsync,
            (bookmarks) -> SwingUtilities.invokeLater(() -> listener.accept(bookmarks)));
    }

    public void addBookmarks(@Nonnull Set<Hotel> hotels) { exec.submit(() -> addBookmarksAsync(hotels)); }

    public void removeBookmarks(@Nonnull Set<Hotel> hotels) { exec.submit(() -> removeBookmarksAsync(hotels)); }

    @Nullable
    private List<Hotel> queryBookmarksAsync() throws CouchbaseLiteException {
        final Database database = db.getDatabase();
        if (database == null) { return null; }

        final ResultSet results = QueryBuilder
            .select(SelectResult.all().from("bookmark"), SelectResult.all().from("hotel"))
            .from(DataSource.database(database).as("bookmark"))
            .join(Join.join(DataSource.database(database).as("hotel"))
                .on(ArrayFunction.contains(Expression.property(LocalStore.FIELD_BOOKMARKS)
                    .from("bookmark"), Meta.id.from("hotel"))))
            .where(Expression.property(LocalStore.FIELD_TYPE).from("bookmark")
                .equalTo(Expression.string(GUEST_DOC_TYPE)))
            .execute();

        final List<Hotel> bookmarks = new ArrayList<>();
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

        final MutableArray bookmarks = guestDoc.getArray(LocalStore.FIELD_BOOKMARKS);
        if (bookmarks != null) {
            for (int i = 0; i < bookmarks.count(); i++) { currentBookmarks.add(bookmarks.getString(i)); }
        }

        currentBookmarks.addAll(ids);
        LOGGER.log(Level.INFO, "Bookmarking: " + currentBookmarks);

        final MutableArray newBookmarks = new MutableArray();
        for (String bookmark : currentBookmarks) { newBookmarks.addString(bookmark); }

        guestDoc.setArray(LocalStore.FIELD_BOOKMARKS, newBookmarks);

        database.save(guestDoc);
    }

    private void unbookmarkIds(Database database, Set<String> ids) throws CouchbaseLiteException {
        LOGGER.log(Level.INFO, "Unbookmarking: " + ids);

        final MutableDocument guestDoc = getGuestDoc(database);

        final MutableArray bookmarks = guestDoc.getArray(LocalStore.FIELD_BOOKMARKS);
        if (bookmarks == null) { return; }

        for (int i = bookmarks.count() - 1; i >= 0; i--) {
            if (ids.contains(bookmarks.getString(i))) { bookmarks.remove(i); }
        }

        database.save(guestDoc);
    }

    private MutableDocument getGuestDoc(Database database) {
        final Document doc = database.getDocument(LocalStore.GUEST_DOC_ID);

        if (doc != null) { return doc.toMutable(); }

        final MutableDocument mDoc = new MutableDocument(LocalStore.GUEST_DOC_ID);
        mDoc.setString(LocalStore.FIELD_TYPE, LocalStore.GUEST_DOC_TYPE);

        return mDoc;
    }
}
