//
//  UserPresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 12/15/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import UIKit

protocol UserPresenterProtocol:PresenterProtocol {
    func fetchLoggedInUserProfile( handler: @escaping (_ user: User?, _ error: Error?) -> Void)
}


extension UserPresenterProtocol {
    func updateProfileImage(_ image: UIImage, handler:@escaping( _ error:Error?)->Void) {
        
    }
    
}
