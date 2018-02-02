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
            searchQuery = QueryBuilder
                .select(_SelectColumn.AIRPORTNAMERESULT)
                .from(DataSource.database(db))
                .where(_Property.TYPE
                    .equalTo(Expression.string("airport"))
                    .and(_Property.FAA
                    .equalTo(Expression.string(searchStr.uppercased())))).orderBy(Ordering.property("datfield").ascending())
            
        case AirportCodeLength.ICAO.rawValue:
            searchQuery = QueryBuilder
                .select(_SelectColumn.AIRPORTNAMERESULT)
                .from(DataSource.database(db))
                .where(_Property.TYPE
                    .equalTo(Expression.string("airport"))
                    .and(_Property.ICAO
                    .equalTo(Expression.string(searchStr.uppercased()))))
        default:
            // Search for all airports starting with specific searchStr
            searchQuery = QueryBuilder
                .select(_SelectColumn.AIRPORTNAMERESULT)
                .from(DataSource.database(db))
                .where(_Property.TYPE
                    .equalTo(Expression.string("airport"))
                    .and (Expression.property("airportname")
                    .like(Expression.string("\(searchStr)%"))))
        }
        if let searchQuery = searchQuery {
            var matches:Airports = []
            do {
                for row in try searchQuery.execute() {
                    if let match = row.string(forKey: "airportname") {
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
