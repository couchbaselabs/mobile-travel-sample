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
    func fetchHotelsMatchingDescription( _ descriptionStr:String?,location locationStr:String, fromLocalStore:Bool, handler:@escaping(_ hotels:Hotels?, _ error:Error?)->Void)
    
    func bookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void)
    func unbookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void)
    func fetchBookmarkedHotels( handler:@escaping(_ hotels:BookmarkHotels?, _ error:Error?)->Void)
}


extension HotelPresenterProtocol {
    func bookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void) {
        
    }
    func unbookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void) {
        
    }
}
