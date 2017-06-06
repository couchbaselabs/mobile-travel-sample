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
    fileprivate var dbMgr:DatabaseManager = DatabaseManager.shared
    fileprivate var _bookings:Bookings = Bookings()
    var bookings:Bookings {
        get {
            return _bookings
        }
    }
    
    private var _userDocId:String?
    fileprivate var userDocId:String? {
        
        guard let db = dbMgr.db , let user = dbMgr.currentUserCredentials?.user else {
            return _userDocId
        }
        // Every user MUST be associated with a single user document that is created when the
        // user signs up. If a user does not have this user document, then we assume that
        // the user is not a valid user
        let userQuery = Query
            .select()
            .from(DataSource.database(db))
            .where(Expression.property("username").equalTo(user))
        if _userDocId == nil {
            do {
                for (_, row) in try userQuery.run().enumerated() {
                    // V1.0. There should be only one document for a user.
                    _userDocId = row.documentID
                }
            }catch {
                return nil
            }
        }
        return _userDocId
    }
    

    
    func fetchBookingsForCurrentUser( observeChanges:Bool) {
        print(#function)
        _bookings.removeAll()
        guard let db = dbMgr.db , let user = dbMgr.currentUserCredentials?.user else {
            fatalError("db is not initialized at this point!")

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

/*
 Data Model For Flight Booking:
 {
 "username": "demo",
 "password": "5f4dcc3b5aa765d61d8327deb882cf99",
 "flights": [
 {
 "flight": "AA090",
 "name": "American Airlines",
 "destinationairport": "LHR",
 "price": 237.88,
 "sourceairport": "SAN",
 "date": "05/04/2017 17:44:00"
 }
 ]
 }
 */
// MARK: Couchbase Lite Queries
// V1 has a single document for user so most manipulation of flight data is JSON
extension BookingPresenter {
    func addFlightBookings(_ flights:Bookings, handler:@escaping(_ error:Error?)->Void) {
        
        guard let db = dbMgr.db, let docId = userDocId else {
            // TODO: Add custom error
            handler(nil)
            return
        }
        
      
        if let flightDocument = db.getDocument(docId) {
            _bookings = flightDocument.array(forKey: "flights")?.toArray() as? Bookings ?? []
            _bookings.append(contentsOf: flights)
            
            flightDocument.set(_bookings, forKey: "flights")
            do {
                try db.save(flightDocument)
                  handler(nil)
            }
            catch {
                handler(error)
                return
            }
        }
        else {
            // TODO: Add custom error
            handler(nil)
            return
        }
            
        
    }
    func replaceFlightBookings(_ flights:Bookings, handler:@escaping(_ error:Error?)->Void) {
        fatalError("TODO")
        // TODO
    }
    func removeFlightBookings(_ flights:Bookings, handler:@escaping(_ error:Error?)->Void) {
        //TODO
        fatalError("TODO")
    }
}

