//
//  HotelPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/7/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import CouchbaseLiteSwift

class HotelPresenter:HotelPresenterProtocol {
    
    fileprivate var dbMgr:DatabaseManager = DatabaseManager.shared
    
    weak var associatedView: PresentingViewProtocol?
  
}


// MARK: Local DB Queries via CBM 2.0. Both Regular queries and FTS are used
extension HotelPresenter {
    func fetchHotelsMatchingDescription( _ descriptionStr:String?,location locationStr:String, handler:@escaping(_ hotels:Hotels?, _ error:Error?)->Void) {
        guard let db = dbMgr.db else {
            fatalError("db is not initialized at this point!")
        }
      //  let query = Query.explain(<#T##Query#>)
        
        
    }
    
}


// MARK: PresenterProtocol
extension HotelPresenter:PresenterProtocol {
    func attachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = view
        
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
}
