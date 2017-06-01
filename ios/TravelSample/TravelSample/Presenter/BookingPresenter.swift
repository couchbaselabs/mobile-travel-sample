//
//  BookingPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/31/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation

class BookingPresenter:BookingPresenterProtocol {
    
    weak var associatedView: BookingPresentingViewProtocol?
    
    func fetchBookingsForCurrentUser( observeChanges:Bool) {
        print(#function)
    }
    
    
    func attachPresentingView(_ view:PresentingViewProtocol) {
        if let viewToAttach = view as? BookingPresentingViewProtocol {
            self.associatedView = viewToAttach
        }
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
    
}
