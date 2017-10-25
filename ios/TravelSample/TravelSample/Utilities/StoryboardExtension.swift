//
//  StoryboardExtension.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/30/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

extension UIStoryboard {
    enum Storyboard:String {
        case Main = "Main"
        
    }
    
    enum StoryboardSegue {
        case addFlightBooking
        case searchFlight
        case searchHotelInGuestMode
        var identifier:String {
            switch self {
            case .addFlightBooking:
                return "AddBookingSegue"
            case .searchFlight:
                return "SearchFlightSegue"
            case .searchHotelInGuestMode:
                return "SearchHotelInGuestModeSegue"
                
            }
        }
    
    }
    
    class func getStoryboard(_ storyboard:Storyboard,bundle:Bundle? = nil )->UIStoryboard {
        return UIStoryboard(name:storyboard.rawValue, bundle: bundle)
    }
    
    func instantiateViewControllerWithIdentifier<T:UIViewController>()->T where T:StoryboardIdentifiable {
        let vc =  self.instantiateViewController(withIdentifier: T.storyboardIdentifier)
        if let viewController = vc as? T {
            return viewController
        }
        fatalError("Check identifier of viewcontroller in storyboard")
        
    }
    
}

protocol StoryboardIdentifiable {
    static var storyboardIdentifier:String {get}
}

extension StoryboardIdentifiable where Self:UIViewController {
    static var storyboardIdentifier:String {
        return String(describing: self)
    }
}
