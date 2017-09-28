//
//  FlightPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation


typealias FlightSearchCriteria = (name:String,date:Date?)

protocol FlightPresenterProtocol:PresenterProtocol {
    func fetchFlightsForCurrentUserWithSource( _ source:FlightSearchCriteria,destination:FlightSearchCriteria, handler:@escaping(_ flights:Flights?, _ error:Error?)->Void)

   
}

func == (tuple1:FlightSearchCriteria,tuple2:FlightSearchCriteria) -> Bool
{
    return (tuple1.0 == tuple2.0) && (tuple1.1 == tuple2.1)
}

