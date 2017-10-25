//
//  Booking.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 9/28/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation

typealias Booking = [String:Any]
typealias Bookings = [Booking]

/*
 Data Model of Booking
 {
 "destinationairport": "SAN",
 "equipment": "777",
 "flight": "AA698",
 "flighttime": 4137.0,
 "name": "American Airlines",
 "price": 517.13,
 "sourceairport": "LHR",
 "utc": "08:16:00"
 }
 */

func == (lhs:Booking, rhs:Booking)->Bool {
    
    print("lhs  = \(lhs) , rhs = \(rhs)")
    
    return lhs["destinationairport"] as? String == rhs["destinationairport"] as? String &&
        lhs["equipment"]as? String  == rhs["equipment"]as? String  &&
        lhs["flight"]as? String  == rhs["flight"]as? String  &&
        lhs["flighttime"]as? Int  == rhs["flighttime"]as? Int &&
        lhs["name"] as? String == rhs["name"] as? String &&
        lhs["price"] as? Float == rhs["price"] as? Float  &&
        lhs["sourceairport"] as? String  == rhs["sourceairport"] as? String &&
        lhs["utc"]as? String  == rhs["utc"]as? String  &&
        lhs["date"] as? String == rhs["date"] as? String
    
}
