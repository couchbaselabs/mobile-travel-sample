package com.couchbase.travelsample.bookmarks;

import java.util.List;
import java.util.Map;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface BookmarksContract {

    interface View {

        void showBookmarks(List<Map<String, Object>> bookmarks);

    }

    interface UserActionsListener {

        void fetchBookmarks();

        void removeBookmark(Map<String, Object> bookmark);

    }

}
