//
//  BookmarkHotelPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 9/1/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import CouchbaseLiteSwift

class BookmarkHotelPresenter:BookmarkHotelPresenterProtocol {
    
    weak var associatedView: BookingPresentingViewProtocol?
    fileprivate var dbMgr:DatabaseManager = DatabaseManager.shared

    fileprivate var _liveQueryListener:NSObjectProtocol?
    fileprivate var _bookingQuery:LiveQuery?

}


extension BookmarkHotelPresenter {
    func fetchBookmarkedHotels( handler:@escaping(_ hotels:BookmarkHotels?, _ error:Error?)->Void)
    {
        print(#function)
        var bookmarkedHotels:BookmarkHotels = BookmarkHotels()
        guard let db = dbMgr.db , let user = dbMgr.currentUserCredentials?.user else {
            fatalError("db is not initialized at this point!")
            
        }
        self.associatedView?.dataStartedLoading()
        
        
        
         /*
         do {
            // Query to fetch bookmarked hotels
            let bookmarkHotelExpr = Expression.property("type").equalTo("bookmarkedhotel")
            let hotelExpr =
            let _hotelQuery:Query? = Query
                .select()
                .from(DataSource.database(db))
                .where(Expression.property("type").equalTo("bookmarkedhotel"))
            
            try! print(_hotelQuery?.explain())
            
            
            try _hotelQuery?.run()
         
            for (_, row) in try _hotelQuery!.run().enumerated() {
                // There should be only one document for a user
                print (row.array(forKey: "flights")?.toArray() ?? "No element with flights key!")
                if let hotels = row.array(forKey: "flights")?.toArray() as? bookmarkedHotels {
                    bookmarkedHotels += bookings
                }
                print ("bookings is \(bookmarkedHotels)")
            }
            self.associatedView?.dataFinishedLoading()
            self.associatedView?.updateUIWithUpdatedBookings(bookmarkedHotels, error: nil)
         }
         catch {
            self.associatedView?.dataFinishedLoading()
         
            print(error.localizedDescription)
            self.associatedView?.updateUIWithUpdatedBookings(nil, error: error)
         }
    */
        
        

    }
    
    func bookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void) {
        
        
    }
    func unbookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void) {
        
    }
}



// MARK: PresenterProtocol
extension BookmarkHotelPresenter:PresenterProtocol {
    func attachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = view as! BookingPresentingViewProtocol
        
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
}
