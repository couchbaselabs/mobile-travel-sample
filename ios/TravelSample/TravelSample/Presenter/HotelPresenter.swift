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
        
//        if let descriptionStr = descriptionStr {
//            descExp = Expression.property("description").like("%\(descriptionStr)%")
//                .or(Expression.property("name").like("%\(descriptionStr)%" ))
//        }
//        if let descriptionStr = descriptionStr {
//            descExp = Expression.property("description").match("'\(descriptionStr)'")
//            .or(Expression.property("name").match("'\(descriptionStr)'"))
//        }
        if let descriptionStr = descriptionStr {
            descExp = _Property.DESCRIPTION.like("%\(descriptionStr)%")
                            .or(_Property.NAME.like("%\(descriptionStr)%" ))
        }
      
        
        let locationExp = _Property.COUNTRY.equalTo(locationStr)
            .or(_Property.CITY.equalTo(locationStr))
            .or(_Property.STATE.equalTo(locationStr))
            .or(_Property.ADDRESS.equalTo(locationStr))
        
        var searchExp:Expression = locationExp
        if  let descExp = descExp {
            searchExp = locationExp.and(descExp)
        }

//        // TODO: Try out predicate Query
//        let searchPredicate = NSPredicate.init(format: "type == %@ AND (description CONTAINS %@ AND (country == %@ OR city == %@ OR state == %@ OR address == %@))", "hotel",descriptionStr ?? "", locationStr,locationStr,locationStr,locationStr)
//        let hotelSearchQuery1 = db.createQuery(
//            where: searchPredicate ,returning:["name","address"])
//        
//        
//        try! print(hotelSearchQuery1.explain())
//        
//        do {
//            
//            let rows = try hotelSearchQuery1.run()
//            
//                for row in rows {
//                    print("query row is \(row )")
//
//                }
//            
//        
//            for (_, row) in try hotelSearchQuery1.run().enumerated() {
//                               print(row.document.toDictionary().count)
//            }
//        }
//        catch {
//            
//            print(error.localizedDescription)
//        }
        

    
        let hotelSearchQuery = Query
            .select(_SelectColumn.DOCIDRESULT) // CHANGE THIS WHEN SELECT* IS SUPPORTED
            .from(DataSource.database(db))
            .where(_Property.TYPE
                .equalTo("hotel")
            .and(searchExp))
        
           print(try! hotelSearchQuery.explain())
    
        var matches:Hotels = []
        do {
            for (_,row) in try hotelSearchQuery.run().enumerated() {
                if let docId = row.string(forKey: "_id"), let doc = db.getDocument(docId) {
                   
                    let match = doc.toDictionary()
                    matches.append(match)
                }
                
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
