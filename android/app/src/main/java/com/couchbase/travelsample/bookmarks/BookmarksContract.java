package com.couchbase.travelsample.bookmarks;

import org.json.JSONArray;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface BookmarksContract {

    interface View {

        void showBookmarks(JSONArray bookmarks);

    }

    interface UserActionsListener {

        void fetchBookmarks();

    }

}
