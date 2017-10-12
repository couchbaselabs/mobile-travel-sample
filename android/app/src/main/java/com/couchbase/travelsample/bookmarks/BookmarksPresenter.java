package com.couchbase.travelsample.bookmarks;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.Join;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.util.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

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

        Query query = Query
            .select(bookmarkAllColumns, hotelsAllColumns)
            .from(bookmarkDS)
            .join(join)
            .where(typeExpr.equalTo("bookmarkedhotels"));

        ResultSet rows = null;
        try {
            rows = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        List<String> data = new ArrayList<String>();
        Result row = null;
        while((row = rows.next()) != null) {
            data.add(row.getDictionary("hotelDS").getString("name"));
        }
        mBookmarksView.showBookmarks(data);
    }

}
