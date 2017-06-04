//
//  FlightPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
typealias Flight = [String:Any]
typealias Flights = [Flight]

typealias FlightSearchCriteria = (name:String,date:Date?)

protocol FlightPresenterProtocol:PresenterProtocol {
    var flights:Flights {get}
    func fetchFlightsForCurrentUserWithSource( _ source:FlightSearchCriteria,destination:FlightSearchCriteria, handler:@escaping(_ flights:Flights?, _ error:Error?)->Void)
    
    func addFlightBookings(_ flights:Flights, handler:@escaping(_ error:Error?)->Void)
    func replaceFlightBookings(_ flights:Flights, handler:@escaping(_ error:Error?)->Void)
    func removeFlightBookings(_ flights:Flights, handler:@escaping(_ error:Error?)->Void)
}

