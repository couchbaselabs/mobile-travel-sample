//
//  BookingPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/31/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import CouchbaseLiteSwift

class BookingPresenter:BookingPresenterProtocol {
    
    weak var associatedView: BookingPresentingViewProtocol?
    private var dbMgr:DatabaseManager = DatabaseManager.shared
    private var _bookings:Bookings = Bookings()
    var bookings:Bookings {
        get {
            return _bookings
        }
    }
    
    func fetchBookingsForCurrentUser( observeChanges:Bool) {
        print(#function)
        _bookings.removeAll()
        guard let db = dbMgr.db , let user = dbMgr.currentUserCredentials?.user else {
            fatalError("db is not initialized at this point!")
            return
        }
        self.associatedView?.dataStartedLoading()
        // TODO: Switch to live query depending on observe changes.
        
        let bookingQuery = Query
            .select()
            .from(DataSource.database(db))
            .where(Expression.property("username").equalTo(user)) // Just being future proof.We do not need this since there is only one doc for a user and a separate local db for each user anyways.
        do {
            for (_, row) in try bookingQuery.run().enumerated() {
                // There should be only one document for a user
                print (row.document.array(forKey: "flights")?.toArray() ?? "No element with flights key!")
                if let bookings = row.document.array(forKey: "flights")?.toArray() as? Bookings {
                     _bookings += bookings
                }
            }
            self.associatedView?.dataFinishedLoading()
            print("Thread is \(Thread.current)")
            self.associatedView?.updateUIWithUpdatedBookings(bookings, error: nil)
        }
        catch {
            self.associatedView?.dataFinishedLoading()
            
            print(error.localizedDescription)
            self.associatedView?.updateUIWithUpdatedBookings(nil, error: error)
        }
    }
    
    
    func attachPresentingView(_ view:PresentingViewProtocol) {
        if let viewToAttach = view as? BookingPresentingViewProtocol {
            self.associatedView = viewToAttach
        }
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
    
}
