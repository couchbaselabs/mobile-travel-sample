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
     
    enum AirportCodeLength:Int {
        case FAA = 3
        case ICAO = 4
        
    }
}


// MARK: Local DB Queries via CBM 2.0. Both Regular queries and FTS are used
extension AirportPresenter {
    func fetchAirportsMatching( _ searchStr:String, handler:@escaping(_ airports:Airports?, _ error:Error?)->Void) {
        guard let db = dbMgr.db else {
            fatalError("db is not initialized at this point!")
            
        }
        var searchQuery:Query?
        switch searchStr.characters.count {
        case AirportCodeLength.FAA.rawValue :
            searchQuery = Query
                .select()
                .from(DataSource.database(db))
                .where(Expression.property("type")
                    .equalTo("airport")
                    .and(Expression.property("faa"))
                    .equalTo(searchStr.uppercased()))
            
        case AirportCodeLength.ICAO.rawValue:
            searchQuery = Query
                .select()
                .from(DataSource.database(db))
                .where(Expression.property("type")
                    .equalTo("airport")
                    .and(Expression.property("icao"))
                    .equalTo(searchStr.uppercased()))
        default:
            // Search for all airports starting with specific searchStr
            searchQuery = Query
                .select()
                .from(DataSource.database(db))
                .where(Expression.property("type")
                    .equalTo("airport")
                    .and (Expression.property("airportname"))
                    .like("\(searchStr)%"))
        }
        if let searchQuery = searchQuery {
            var matches:Airports = []
            do {
                for row in try searchQuery.run() {
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
