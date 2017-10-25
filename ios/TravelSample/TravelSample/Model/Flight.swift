//
//  Flight.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 9/28/17.
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
