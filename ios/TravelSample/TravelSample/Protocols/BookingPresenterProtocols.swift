//
//  BookingPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/31/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation


protocol BookingPresenterProtocol:PresenterProtocol {
    var bookings:Bookings {get}
    func fetchBookingsForCurrentUser( observeChanges:Bool)
    func addFlightBookings(_ flights:Bookings, handler:@escaping(_ error:Error?)->Void)
    func removeFlightBookings(_ flights:Bookings, handler:@escaping(_ error:Error?)->Void)
}

// Workplan view controller must implement this protocol
protocol BookingPresentingViewProtocol:PresentingViewProtocol {
    func updateUIWithUpdatedBookings(_ bookings:Bookings?,error:Error?)
}

extension BookingPresentingViewProtocol {
    func updateUIWithUpdatedBookings(_ bookings:Bookings?,error:Error?) {
        // NOOP
    }
 
}

