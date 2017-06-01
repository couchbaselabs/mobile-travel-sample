//
//  BookingPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/31/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation

typealias Booking = [String:Any]
typealias Bookings = [Booking]
protocol BookingPresenterProtocol:PresenterProtocol {
    func fetchBookingsForCurrentUser( observeChanges:Bool) 
}

// Workplan view controller must implement this protocol
protocol BookingPresentingViewProtocol:PresentingViewProtocol {
    func updateUIWithBookings(_ bookings:Bookings?)
}
