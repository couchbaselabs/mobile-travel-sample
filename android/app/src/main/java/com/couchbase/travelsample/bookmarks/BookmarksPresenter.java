package com.couchbase.travelsample.bookmarks;

import com.couchbase.lite.Array;
import com.couchbase.lite.ArrayFunction;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.Join;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.util.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookmarksPresenter implements BookmarksContract.UserActionsListener {

    private BookmarksContract.View mBookmarksView;

    public BookmarksPresenter(BookmarksContract.View mBookmarksView) {
        this.mBookmarksView = mBookmarksView;
    }

    public void fetchBookmarks() {
        Database database = DatabaseManager.getDatabase();

        DataSource bookmarkDS = DataSource.database(database).as("bookmarkDS");
        DataSource hotelsDS = DataSource.database(database).as("hotelDS");

        Expression hotelsExpr = Expression.property("hotels").from("bookmarkDS");
        Expression hotelIdExpr = Meta.id.from("hotelDS");

        ArrayFunction.contains(Expression.property("public_likes"), value: Expression.string("Yasmeen Lemke"))

        Expression joinExpr = ArrayFunction.contains(hotelsExpr, hotelIdExpr);
        Join join = Join.join(hotelsDS).on(joinExpr);

        Expression typeExpr = Expression.property("type").from("bookmarkDS");

        SelectResult bookmarkAllColumns = SelectResult.all().from("bookmarkDS");
        SelectResult hotelsAllColumns = SelectResult.all().from("hotelDS");

        Query query = QueryBuilder
            .select(bookmarkAllColumns, hotelsAllColumns)
            .from(bookmarkDS)
            .join(join)
            .where(typeExpr.equalTo(Expression.string("bookmarkedhotels")));

        query.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {
                ResultSet rows = change.getResults();

                List<Map<String, Object>> data = new ArrayList<>();
                Result row = null;
                while((row = rows.next()) != null) {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("name", row.getDictionary("hotelDS").getString("name"));
                    properties.put("address", row.getDictionary("hotelDS").getString("address"));
                    properties.put("id", row.getDictionary("hotelDS").getString("id"));
                    data.add(properties);
                }
                mBookmarksView.showBookmarks(data);
            }
        });

        try {
            query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeBookmark(Map<String, Object> bookmark) {
        Database database = DatabaseManager.getDatabase();
        Document document = database.getDocument((String) bookmark.get("id"));
        try {
            database.delete(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        MutableDocument guestDoc = database.getDocument("user::guest").toMutable();
        MutableArray hotelIds = guestDoc.getArray("hotels").toMutable();
        for (int i = 0; i < hotelIds.count(); i++) {
            if (hotelIds.getString(i).equals((String) bookmark.get("id"))) {
                hotelIds.remove(i);
            }
        }

        try {
            database.save(guestDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
