package com.couchbase.travelsample.bookmarks;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.Join;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Query;
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
        Expression hotelIdExpr = Expression.meta().getId().from("hotelDS");

        Expression joinExpr = Function.arrayContains(hotelsExpr, hotelIdExpr);
        Join join = Join.join(hotelsDS).on(joinExpr);

        Expression typeExpr = Expression.property("type").from("bookmarkDS");

        SelectResult bookmarkAllColumns = SelectResult.all().from("bookmarkDS");
        SelectResult hotelsAllColumns = SelectResult.all().from("hotelDS");

        LiveQuery query = Query
            .select(bookmarkAllColumns, hotelsAllColumns)
            .from(bookmarkDS)
            .join(join)
            .where(typeExpr.equalTo("bookmarkedhotels")).toLive();

        query.addChangeListener(new LiveQueryChangeListener() {
            @Override
            public void changed(LiveQueryChange change) {
                ResultSet rows = change.getRows();

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

        query.run();
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
    }
}
