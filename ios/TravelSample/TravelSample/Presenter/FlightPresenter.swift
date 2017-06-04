//
//  FlightPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import CouchbaseLiteSwift

/*Data Model of Flight
 {
 "data": [
 {
 "destinationairport": "LHR",
 "equipment": "777",
 "flight": "AA090",
 "flighttime": 3943.0,
 "name": "American Airlines",
 "price": 492.88,
 "sourceairport": "SAN",
 "utc": "17:44:00"
 }
 ]
 }
 */
class FlightPresenter:FlightPresenterProtocol {
    // Example query:http://localhost:8080/api/flightPaths/Heathrow/San%20Diego%20Intl?leave=05/04/2017&return=leave=05/04/2017
    
    let serverBackendUrl:URL? = URL.init(string: "http://localhost:8080/api/")
    fileprivate var dbMgr:DatabaseManager = DatabaseManager.shared
    
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
    
    
    weak var associatedView: PresentingViewProtocol?

    fileprivate var _flights:Flights = Flights()
    var flights:Flights {
        get {
            return _flights
        }
    }
    
}


// N1QL Query Directly Against Server (via the Couchbase Server)
extension FlightPresenter {
    fileprivate  var  dateFormatter:DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "MM/dd/yyyy"
        
        return formatter
    }
    
    func fetchFlightsForCurrentUserWithSource( _ source:FlightSearchCriteria,destination:FlightSearchCriteria,handler:@escaping (_ flights:Flights?, _ error:Error?)->Void) {
        _flights.removeAll()
        self.associatedView?.dataStartedLoading()
        let session = URLSession.shared
        var leaveDate = ""
        if let date = source.date {
            leaveDate = dateFormatter.string(from: date)
        }
        let searchPath = "\(source.name)/\(destination.name)"
        let escapedSearchPath = searchPath.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)
        let fullPath = "flightPaths/\(String(describing: escapedSearchPath!))?leave=\(leaveDate)"
        
        if let url = URL.init(string: fullPath, relativeTo: serverBackendUrl) {
            let dataTask = session.dataTask(with: url) { [weak self] (data, response, error) in
                self?.associatedView?.dataFinishedLoading()
                
                switch error {
                case nil:
                    if let httpResp = response as? HTTPURLResponse {
                        switch httpResp.statusCode {
                        //todo: Do CUstom Error
                        case 200 :
                            if let dataVal = data {
                                do {
                                    if let flightData = try JSONSerialization.jsonObject(with: dataVal, options:.allowFragments) as? [String:Flights] {
                                        DispatchQueue.main.async {
                                            handler(flightData["data"],nil)                                            }
                                        
                                    }
                                }
                                    
                                catch {
                                    print("Failed to serialize JSON data")
                                    //TODO: Create custom error
                                    DispatchQueue.main.async {
                                        handler(nil,nil)
                                    }
                                    
                                }
                            }
                            
                        default:
                            // TODO: Create custom error
                            print("Got status of \(httpResp)")
                        }
                    }
                default:
                    DispatchQueue.main.async {
                        handler(nil,error)
                    }
                    
                }
            }
            dataTask.resume()
        }
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
extension FlightPresenter {
    func addFlightBookings(_ flights:Flights, handler:@escaping(_ error:Error?)->Void) {
        
        guard let db = dbMgr.db , let user = dbMgr.currentUserCredentials?.user , let docId = userDocId else {
            // TODO: Add custom error
            handler(nil)
            return
        }
        var currFlightBookings:Flights?
        
        if let docId = userDocId {
            if let flightDocument = db.getDocument(docId) {
                currFlightBookings = flightDocument.array(forKey: "flights")?.toArray() as? Flights ?? []
                currFlightBookings?.append(contentsOf: flights)
                flightDocument.set(currFlightBookings, forKey: "flights")
                do {
                    try db.save(flightDocument)
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
        else {
            // TODO: Add custom error
            handler(nil)
            return
        }
    }
    func replaceFlightBookings(_ flights:Flights, handler:@escaping(_ error:Error?)->Void) {
        fatalError("TODO")
        // TODO
    }
    func removeFlightBookings(_ flights:Flights, handler:@escaping(_ error:Error?)->Void) {
        //TODO
        fatalError("TODO")
    }
}


// MARK: PresenterProtocol
extension FlightPresenter:PresenterProtocol {
    func attachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = view
        
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
}
