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
        
        // Description is looked up in the "description" and "name" content
        // Location is looked up in country, city, state and address
        // Reference :https://developer.couchbase.com/documentation/server/4.6/sdk/sample-application.html
        var descExp:Expression?
        
        if let descriptionStr = descriptionStr {
            descExp = Expression.property("description").like("%\(descriptionStr)%")
                .or(Expression.property("name").like("%\(descriptionStr)%" ))
        }
        
        let locationExp = Expression.property("country").equalTo(locationStr)
            .or(Expression.property("city").equalTo(locationStr))
            .or(Expression.property("state").equalTo(locationStr))
            .or(Expression.property("address").equalTo(locationStr))
        
        var searchExp:Expression = locationExp
        if  let descExp = descExp {
            searchExp = locationExp.and(descExp)
        }
        
      
        
        let hotelSearchQuery = Query
            .select()
            .from(DataSource.database(db))
            .where(Expression.property("type")
                .equalTo("hotel")
            .and(searchExp))
        
       try! hotelSearchQuery.explain()
    
        var matches:Hotels = []
        do {
            for (index,row) in try hotelSearchQuery.run().enumerated() {
                
                let match = row.document.toDictionary()
                matches.append(match)
                
            }
            handler(matches,nil)
        }
        catch {
            handler(nil,error)
        }
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
