package com.couchbase.travelsample.model;

import com.couchbase.lite.*;

import java.util.HashMap;
import java.util.Map;

public class GuestModel {
    private static final String GUEST_DOC_ID = "user::guest";
    private static final String GUEST_DOC_TYPE = "bookmarkedhotels";

    public static void bookmarkHotel(Map<String, Object> hotel) throws CouchbaseLiteException {
        Database database = DatabaseManager.getDatabase();

        // Create a hotel document if it doesn't exist
        String id = (String) hotel.get("id");
        Document hotelDoc = database.getDocument(id);
        if (hotelDoc == null) {
            database.save( new MutableDocument(id, hotel));
        }

        // Get the guest document
        Document doc = database.getDocument(GUEST_DOC_ID);
        MutableDocument mDoc = null;
        if (doc == null) {
            HashMap<String, Object> properties = new HashMap<>();
            mDoc = new MutableDocument(GUEST_DOC_ID);
            mDoc.setString("type", GUEST_DOC_TYPE);
            mDoc.setArray("hotels", new MutableArray());
        }
        else {
            mDoc = doc.toMutable();
        }

        // Add the bookmarked hotel id to the hotels array
        MutableArray hotels =  mDoc.getArray("hotels");
        hotels.addString(id);
        database.save(mDoc);
    }

    public static void getBookmarks(QueryChangeListener listener) throws CouchbaseLiteException {
        // SELECT bookmark.*, hotel.*
        // FROM DATABASE as bookmark
        // JOIN DATABASE as hotel ON bookmark.hotels CONTAINS hotel.meta.id
        // WHERE bookmark.type = "bookmarkedhotels"
        Database database = DatabaseManager.getDatabase();
        DataSource bookmark = DataSource.database(database).as("bookmark");
        DataSource hotel = DataSource.database(database).as("hotel");

        Expression joinCondition = ArrayFunction.contains(
                Expression.property("hotels").from("bookmark"),
                Meta.id.from("hotel"));

        Query query = QueryBuilder
                .select(SelectResult.all().from("bookmark"), SelectResult.all().from("hotel"))
                .from(bookmark)
                .join(Join.join(hotel).on(joinCondition))
                .where(Expression.property("type").from("bookmark").equalTo(Expression.string(GUEST_DOC_TYPE)));
        query.addChangeListener(listener);
    }

    public static void removeBookmark(String id) throws CouchbaseLiteException {
        Database database = DatabaseManager.getDatabase();
        Document document = database.getDocument(id);
        database.delete(document);

        MutableDocument guestDoc = database.getDocument("user::guest").toMutable();
        MutableArray hotelIds = guestDoc.getArray("hotels").toMutable();
        for (int i = 0; i < hotelIds.count(); i++) {
            if (hotelIds.getString(i).equals(id)) {
                hotelIds.remove(i);
            }
        }

        database.save(guestDoc);
    }
}
