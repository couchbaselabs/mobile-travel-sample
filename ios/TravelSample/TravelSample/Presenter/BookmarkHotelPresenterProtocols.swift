//
//  BookmarkedPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 8/31/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation

typealias BookmarkHotel = [String:Any]
typealias BookmarkHotels = [BookmarkHotel]



protocol BookmarkHotelPresenterProtocol:PresenterProtocol {
    func fetchBookmarkedHotels( handler:@escaping(_ hotels:BookmarkHotels?, _ error:Error?)->Void)
    func bookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void)
    func unbookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void)
}

