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
    fileprivate var _liveQueryListener:ListenerToken?
    fileprivate var _bookingQuery:Query?
    // Uncomment below and comment above declaration if doing live queries
    //fileprivate var _bookingQuery:Query?
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
        let userQuery = QueryBuilder
            .select(_SelectColumn.DOCIDRESULT)
            .from(DataSource.database(db))
            .where(_Property.USERNAME.equalTo(Expression.string(user)))
        if _userDocId == nil {
            do {
                
                for (_, row) in try userQuery.execute().enumerated() {
                    // V1.0. There should be only one document for a user.
                    // There isnt a convenience API to get to documentId from Result. So use "id" key
                    // Tracking - https://github.com/couchbaselabs/couchbase-lite-apiv2/issues/123
                    _userDocId = row.string(forKey: "id")
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
        
       
        // Live Query . Its just one document but we will be notified of changes
 
        _bookingQuery = QueryBuilder
            .select(_SelectColumn.FLIGHTSRESULT)
            .from(DataSource.database(db))
            .where(_Property.USERNAME.equalTo(Expression.string(user))) // Just being future proof.We do not need this since there is only one doc for a user and a separate local db for each user anyways.
       // try! print(bookingQuery.explain())
           
     
        // Register for live query changes. This will run the query
        _liveQueryListener = _bookingQuery?.addChangeListener({ [weak self](change) in
            self?.associatedView?.dataFinishedLoading()
            
            switch change.error {
            case nil:
                for (_, row) in (change.results?.enumerated())! {
                    // There should be only one document for a user
       
                    print (row.array(forKey: "flights")?.toArray() ?? "No element with flights key!")
                    if let bookings = row.array(forKey: "flights")?.toArray() as? Bookings {
                        self?._bookings = bookings
                    }
                    print ("bookings is \(String(describing: self?.bookings))")
                }
                self?.associatedView?.dataFinishedLoading()
                self?.associatedView?.updateUIWithUpdatedBookings(self?.bookings, error: nil)
                
            default:
                self?.associatedView?.dataFinishedLoading()
                
                self?.associatedView?.updateUIWithUpdatedBookings(nil, error: change.error)
            }
           

        })
  
       
        
 /******/
        // Until  BUG :https://github.com/couchbase/couchbase-lite-ios/issues/1816 is resolved, use regular query
        /**** LIVE QUERY SWITCH . Uncomment the Query Request below if doing LiveQuery
        _bookingQuery = Query
            .select()
            .from(DataSource.database(db))
            .where(Expression.property("username").equalTo(user)) // Just being future proof.We do not need this since there is only one doc for a user and a separate local db for each user anyways.
        try! print(_bookingQuery?.explain())
        
        
        
        do {
            
            for (_, row) in try _bookingQuery!.run().enumerated() {
                // There should be only one document for a user
                print (row.array(forKey: "flights")?.toArray() ?? "No element with flights key!")
                if let bookings = row.array(forKey: "flights")?.toArray() as? Bookings {
                    _bookings += bookings
                }
                print ("bookings is \(bookings)")
            }
            self.associatedView?.dataFinishedLoading()
            self.associatedView?.updateUIWithUpdatedBookings(bookings, error: nil)
        }
        catch {
            self.associatedView?.dataFinishedLoading()
            
            print(error.localizedDescription)
            self.associatedView?.updateUIWithUpdatedBookings(nil, error: error)
        }
 
*****/
        
    }
    
    
    func attachPresentingView(_ view:PresentingViewProtocol) {
        if let viewToAttach = view as? BookingPresentingViewProtocol {
            self.associatedView = viewToAttach
            // LIVE QUERY SWITCH: Comment if we are doing live query
            // registerNotificationObservers()

        }
        
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
    
        self.associatedView = nil
        
        // LIVE QUERY SWITCH: Comment block below if we are not doing live query
        if let liveQueryListener = _liveQueryListener {
            print(#function)
            _bookingQuery?.removeChangeListener(withToken:liveQueryListener)
            _bookingQuery = nil
            _liveQueryListener = nil
        }
     
        // LIVE QUERY SWITCH: Comment if we are doing live query
        // deregisterNotificationObservers()
    }
    
    func registerNotificationObservers() {
        NotificationCenter.default.addObserver(forName: NSNotification.Name(rawValue: AppNotifications.replicationIdle.name.rawValue), object: nil, queue: nil) { [weak self] (notification) in
            // Comment if we are  using live query 
            self?.fetchBookingsForCurrentUser(observeChanges: true)
            
        }
    }
    
    
    func deregisterNotificationObservers() {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: AppNotifications.replicationIdle.name.rawValue), object: nil)
        
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
        
        guard let db = dbMgr.db else {
            handler(TravelSampleError.DatabaseNotInitialized)
            return
        }
        
        guard  let docId = userDocId else {
            handler(TravelSampleError.UserNotFound)
            return
        }
      
        if let flightDocument = db.document(withID:docId)?.toMutable() {
        
            _bookings = flightDocument.array(forKey: "flights")?.toArray() as? Bookings ?? []
            _bookings.append(contentsOf: flights)
            
            _bookings = _bookings.map({ (orig)  in
                var newBooking:Booking = orig
                newBooking["date"] = orig["date"]
                newBooking["destinationairport"] = orig["destinationairport"]
                newBooking["flight"] = orig["flight"]
                newBooking["price"] = orig["price"]
                  newBooking["name"] = orig["name"]
                newBooking["sourceairport"] = orig["sourceairport"]
                return newBooking
            })
            flightDocument.setValue(_bookings, forKey: "flights")
            do {
                try db.saveDocument(flightDocument)
                  handler(nil)
            }
            catch{
                handler(error)
                return
            }
           
        }
        else {
            handler(nil)
            return
        }
            
        
    }
  
    func removeFlightBookings(_ bookingsToRemove:Bookings, handler:@escaping(_ error:Error?)->Void) {
    
        guard let db = dbMgr.db else {
            handler(TravelSampleError.DatabaseNotInitialized)
            return
        }
        
        guard  let docId = userDocId else {
            handler(TravelSampleError.UserNotFound)
            return
        }
        
        
        if let flightDocument = db.document(withID:docId)?.toMutable() {
            
            _bookings = flightDocument.array(forKey: "flights")?.toArray() as? Bookings ?? []
            _bookings = _bookings.filter({ (booking) -> Bool in
                return bookingsToRemove.contains(where: { (bookingToRemove) -> Bool in
                    return bookingToRemove == booking
                }) == false
            })
            print("Updated booking after delete is \(_bookings)")
            flightDocument.setValue(_bookings, forKey: "flights")
            do {
                try db.saveDocument(flightDocument)
                handler(nil)
            }
            catch {
                handler(error)
                return
            }
        }
        else {
         
            handler(nil)
            return
        }
       
    }
}

