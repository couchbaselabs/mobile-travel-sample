//
//  HotelPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/7/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
typealias Hotel = [String:Any]
typealias Hotels = [Hotel]


protocol HotelPresenterProtocol:PresenterProtocol {
    func fetchHotelsMatchingDescription( _ descriptionStr:String?,location locationStr:String, handler:@escaping(_ hotels:Hotels?, _ error:Error?)->Void)
    
}

