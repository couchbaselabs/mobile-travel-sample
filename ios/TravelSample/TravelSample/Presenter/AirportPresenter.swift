//
//  AirportPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/7/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import CouchbaseLiteSwift

class AirportPresenter:AirportPresenterProtocol {

     fileprivate var dbMgr:DatabaseManager = DatabaseManager.shared
    
    
    weak var associatedView: PresentingViewProtocol?
    
    fileprivate var _flights:Flights = Flights()
    
    
}


// MARK: Local DB Queries via CBM 2.0
extension AirportPresenter {
    func fetchAirportsMatching( _ searchStr:String, handler:@escaping(_ airports:Airports?, _ error:Error?)->Void) {
        guard let db = dbMgr.db else {
            fatalError("db is not initialized at this point!")
            
        }
        
        // Search for all airports starting with specific searchStr
        let startsWithQuery = Query
            .select()
            .from(DataSource.database(db))
            .where(Expression.property("airportname").like("\(searchStr)%"))
        var matches:Airports = []
        do {
            for row in try startsWithQuery.run() {
                if let match = row.document.string(forKey: "airportname") {
                    matches.append( match)
                }
            }
            handler(matches,nil)
        }
        catch {
             handler(nil,error)
        }

    }
}


// MARK: PresenterProtocol
extension AirportPresenter:PresenterProtocol {
    func attachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = view
        
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
}
