//
//  AirportPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/7/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
typealias Airport = String
typealias Airports = [Airport]


protocol AirportPresenterProtocol:PresenterProtocol {
    func fetchAirportsMatching( _ searchStr:String, handler:@escaping(_ airports:Airports?, _ error:Error?)->Void) 
  
}

