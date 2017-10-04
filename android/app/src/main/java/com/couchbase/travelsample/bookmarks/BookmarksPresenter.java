package com.couchbase.travelsample.bookmarks;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.Join;
import com.couchbase.lite.Query;
import com.couchbase.lite.SelectResult;
import com.couchbase.travelsample.util.DatabaseManager;

public class BookmarksPresenter implements BookmarksContract.UserActionsListener {

    private BookmarksContract.View mBookmarksView;

    public BookmarksPresenter(BookmarksContract.View mBookmarksView) {
        this.mBookmarksView = mBookmarksView;
    }

    public void fetchBookmarks() {
        Database database = DatabaseManager.getDatabase();

        DataSource bookmarkDS = DataSource.database(database).as("bookmarkDS");
        DataSource hotelsDS = DataSource.database(database).as("hotelsDS");

        Expression hotelsExpr = Expression.property("hotels").from("bookmarkDS");
        Expression hotelIdExpr = Expression.meta().getId().from("hotelDS");

        Expression joinExpr = Function.arrayContains(hotelIdExpr, hotelIdExpr);
        Join join = Join.join(hotelsDS).on(joinExpr);

        Expression typeExpr = Expression.property("type").from("bookmarkDS");

        SelectResult bookmarkAllColumns = SelectResult.all().from("bookmarkDS");
        SelectResult hotelsAllColumns = SelectResult.all().from("hotelsDS");

        Query query = Query
            .select(bookmarkAllColumns, hotelsAllColumns)
            .from(bookmarkDS)
            .join(join)
            .where(typeExpr.equalTo("bookmarkedhotels"));


    }

}
