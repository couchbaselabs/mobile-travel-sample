//
//  FlightPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation

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

typealias Flight = [String:Any]
typealias Flights = [Flight]

typealias FlightSearchCriteria = (name:String,date:Date?)

protocol FlightPresenterProtocol:PresenterProtocol {
    func fetchFlightsForCurrentUserWithSource( _ source:FlightSearchCriteria,destination:FlightSearchCriteria, handler:@escaping(_ flights:Flights?, _ error:Error?)->Void)

   
}

func == (tuple1:FlightSearchCriteria,tuple2:FlightSearchCriteria) -> Bool
{
    return (tuple1.0 == tuple2.0) && (tuple1.1 == tuple2.1)
}

