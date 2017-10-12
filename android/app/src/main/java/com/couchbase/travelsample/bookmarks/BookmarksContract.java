package com.couchbase.travelsample.bookmarks;

import org.json.JSONArray;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface BookmarksContract {

    interface View {

        void showBookmarks(List<String> bookmarks);

    }

    interface UserActionsListener {

        void fetchBookmarks();

    }

}
