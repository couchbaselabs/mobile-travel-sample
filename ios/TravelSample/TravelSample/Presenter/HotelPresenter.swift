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
     func fetchHotelsMatchingDescription( _ descriptionStr:String?,location locationStr:String, fromLocalStore:Bool, handler:@escaping(_ hotels:Hotels?, _ error:Error?)->Void) {
        
        switch fromLocalStore {
        case true:
            fetchHotelsFromLocalDatabaseMatchingDescription(descriptionStr, location: locationStr, handler: handler)
        case false:
             fetchHotelsFromWebServiceMatchingDescription(descriptionStr, location: locationStr, handler: handler)
        }
    }
    
    
    
    func bookmarkHotels(_ hotels: Hotels, handler:@escaping( _ error:Error?)->Void) {
        guard let db = dbMgr.db else {
            handler(TravelSampleError.DatabaseNotInitialized)
            return
        }
        
        do {
            var bookmarkedHotelsDoc = try fetchGuestBookmarkDocumentFromDB(db)
            
            if bookmarkedHotelsDoc == nil {
                // First time bookmark is created for guest account
                // Create document of type "bookmarkedhotels"
                bookmarkedHotelsDoc = Document.init(dictionary: ["type":"bookmarkedhotels","hotels":[String]()])
                
            }
            
            // Get the Ids of all hotels that need to be bookmarked
            let hotelIds:[String] = hotels.map({ (hotelDict)  in
                if let idVal = hotelDict["id"] as? String {
                    return idVal
                }
                return ""
            })
            
            // Fetch the current list of bookmarked hotel Ids
            var bookmarked = bookmarkedHotelsDoc?.array(forKey: "hotels")
            
            // Add the new hotel ids to the bookmarked hotels array
            for newId in hotelIds {
                bookmarked = bookmarked?.addString(newId)
            }
            // Update and save the "bookmarkedhotels" document
            if let bookmarkedHotelsDoc = bookmarkedHotelsDoc {
                // Update and save the bookmark document
                bookmarkedHotelsDoc.setArray(bookmarked, forKey: "hotels")
                try db.save(bookmarkedHotelsDoc)
                
            }
            
            // Add the hotel details documents
            for hotelDoc in hotels {
                if let idVal = hotelDoc["id"] as? String {
                    if let doc = db.getDocument(idVal) {
                        doc.setDictionary(hotelDoc)
                        try db.save(doc)
                    }
                    else {
                        try db.save(Document.init(idVal, dictionary: hotelDoc))
                    }
                }
            }
        
            handler(nil)
        }
        catch {
            handler(TravelSampleError.DocumentFetchException)
            return
        }
 
        
    }
    
    func unbookmarkHotels(_ hotels: Hotels, handler:@escaping( _ error:Error?)->Void) {
        guard let db = dbMgr.db else {
            handler(TravelSampleError.DatabaseNotInitialized)
            return
        }
        
        do {
            guard let document = try fetchGuestBookmarkDocumentFromDB(db) else {
                handler(TravelSampleError.DocumentFetchException)
                return
            }
        
            // Get the Ids of all hotels that need to be unbookmarked from the hotels array
            let idsToRemove:[String] = hotels.map({ (dict)  in
                if let idVal = dict["id"] as? String {
                    return idVal
                }
                return ""
            })
            
            
            // Get current list of bookmarked hotels.
            guard let arrOfCurrentIds:[String] = document.array(forKey: "hotels")?.toArray().flatMap({ return $0 as? String }) else {
                handler(TravelSampleError.DocumentFetchException)
                return
            }
            
            
            // Convert to Set for easy set operations
            let setOfCurrentBookmarkedIds:Set<String> = Set(arrOfCurrentIds)
            
            // Get the delta of the new list and current Id list to identify the hotels that are not yet bookmarked
            let IdToRemain = Array(setOfCurrentBookmarkedIds.subtracting(idsToRemove))
         
            // Update the bookmarked Id list
            document.setArray(ArrayObject.init(array: IdToRemain), forKey: "hotels")
            // Save updated version of bookmarkedhotels document
            try db.save(document)
                    
            // Remove unbookmarked hotel documents
            for idOfDocToRemove in idsToRemove {
                if let doc = db.getDocument(idOfDocToRemove) {
                    try db.delete(doc)
                }
            }
                
            handler(nil)

        }
        catch {
            handler(TravelSampleError.DocumentFetchException)
            return
        }

    }

    func fetchBookmarkedHotels( handler:@escaping(_ hotels:Hotels?, _ error:Error?)->Void)
    {
        print(#function)
        guard let db = dbMgr.db else {
            handler(nil,TravelSampleError.DatabaseNotInitialized)
            return
            
        }
        do {
            
            // Do a JOIN Query to fetch bookmark document and for every hotel Id listed
            // in the "hotels" property, fetch the corresponding hotel document
            var bookmarkedHotels:Hotels = Hotels()
            
            // First, we instantiate two data sources which corresponds to the two sides of the join query.
            let bookmarkDS = DataSource.database( db).as("bookmarkDS")
            let hotelsDS = DataSource.database(db).as("hotelsDS")
            
            // Next we write the query expressions. The first one gets the hotels property on the
            // bookmarks data source. The seconds get the document ID on the hotels data source.
            let hotelsExpr = Expression.property("hotels").from("bookmarkDS")
            let hotelIdExpr = Expression.meta().id.from("hotelsDS")
            
            // Next, we use a function expression to find document's whose _id property is in the hotels array. And build the join expression.
            let joinExpr = Function.arrayContains(hotelsExpr, value: hotelIdExpr)
            let join = Join.join(hotelsDS).on(joinExpr);
            
            // Finally, the query object uses that join expression to find all the hotel document referenced in the "hotels" array of the bookmark document.
            let typeExpr = Expression.property("type").from("bookmarkDS")
            
            let bookmarkAllColumns = _SelectColumn.ALLRESULT.from("bookmarkDS")
            let hotelsAllColumns = _SelectColumn.ALLRESULT.from("hotelsDS")
            
            let query = Query.select(bookmarkAllColumns, hotelsAllColumns).from(bookmarkDS).join(join).where(typeExpr.equalTo("bookmarkedhotels"));
            
            print (try? query.explain())
            for result in try query.run() {
                print ("RESULT IS \(result.toDictionary())")
                if let hotel = result.toDictionary()["hotelsDS"] as? Hotel{
                      bookmarkedHotels.append(hotel)
                }
            }
            handler(bookmarkedHotels,nil)
          
        }
        catch {
            handler(nil,TravelSampleError.DocumentFetchException)
            return
 
        }
    }
    
}


// MARL Private
extension HotelPresenter {
    
    fileprivate func fetchHotelsFromLocalDatabaseMatchingDescription( _ descriptionStr:String?,location locationStr:String, handler:@escaping(_ hotels:Hotels?, _ error:Error?)->Void) {
        guard let db = dbMgr.db else {
            fatalError("db is not initialized at this point!")
        }
        
        // Description is looked up in the "description" and "name" content
        // Location is looked up in country, city, state and address
        // Reference :https://developer.couchbase.com/documentation/server/4.6/sdk/sample-application.html
        // MATCH can only appear at top-level, or in a top-level AND

        var descExp:Expression?
        if let descriptionStr = descriptionStr , descriptionStr != ""{
            descExp = _Property.DESCRIPTION.match(descriptionStr)
        }
        
        
        let locationExp = _Property.COUNTRY.like("%\(locationStr)%")
            .or(_Property.CITY.like("%\(locationStr)%"))
            .or(_Property.STATE.like("%\(locationStr)%"))
            .or(_Property.ADDRESS.like("%\(locationStr)%"))
        
        var searchExp:Expression = locationExp
        if  let descExp = descExp {
            searchExp = descExp.and(locationExp)
        }
        
       
        let hotelSearchQuery = Query
            .select(_SelectColumn.ALLRESULT) // CHANGE THIS WHEN SELECT* IS SUPPORTED
            .from(DataSource.database(db))
            .where(
                _Property.TYPE.equalTo("hotel")
                 .and(searchExp)
                )
        print(try? hotelSearchQuery.explain())
        
        var matches:Hotels = []
        do {
            for (_,row) in try hotelSearchQuery.run().enumerated() {
                
                if let dbName = dbMgr.db?.name, let match = row.dictionary(forKey: dbName) {
                    
                    matches.append(match.toDictionary())
                }
                
            }
            handler(matches,nil)
        }
        catch {
            handler(nil,error)
        }
    }

    
    fileprivate func fetchHotelsFromWebServiceMatchingDescription( _ descriptionStr:String?,location locationStr:String, handler:@escaping(_ hotels:Hotels?, _ error:Error?)->Void)
    {
        // Description is looked up in the "description" and "name" content
        // Location is looked up in country, city, state and address
        // Reference :https://developer.couchbase.com/documentation/server/4.6/sdk/sample-application.html
        // Example query:http://localhost:8080/api/hotel/<description>/<location>
        
        
        self.associatedView?.dataStartedLoading()
        let session = URLSession.shared
        var escapedDescStr = "*"
        if let descriptionStr = descriptionStr, descriptionStr != "" {
            escapedDescStr = descriptionStr.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? "*"
        }
        
        let escapedLocationStr = locationStr.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? ""
        
        let searchPath = "\(escapedDescStr)/\(escapedLocationStr)"
        let fullPath = "hotel/\(String(describing: searchPath))"
        
        if let url = URL.init(string: fullPath, relativeTo: TravelSampleWebService.serverBackendUrl) {
            let dataTask = session.dataTask(with: url) { [weak self] (data, response, error) in
                self?.associatedView?.dataFinishedLoading()
                
                switch error {
                case nil:
                    if let httpResp = response as? HTTPURLResponse {
                        switch httpResp.statusCode {
                        case 200 :
                            if let dataVal = data {
                                do {
                                    if let flightData = try JSONSerialization.jsonObject(with: dataVal, options:.allowFragments) as? [String:Flights] {
                                        DispatchQueue.main.async {
                                            handler(flightData["data"],nil)                                            }
                                        
                                    }
                                }
                                    
                                catch {
                                    print("Failed to serialize JSON data")
                                    DispatchQueue.main.async {
                                        handler(nil,TravelSampleError.DataParseError)
                                    }
                                    
                                }
                            }
                            
                        default:
                            // TODO: Create custom error
                            print("Got status of \(httpResp)")
                        }
                    }
                default:
                    DispatchQueue.main.async {
                        handler(nil,error)
                    }
                    
                }
            }
            dataTask.resume()
        }
        
        
    }
    
    fileprivate func fetchGuestBookmarkDocumentFromDB(_ db:Database)throws ->Document?{
        let searchQuery = Query
            .select(_SelectColumn.DOCIDRESULT)
            .from(DataSource.database(db))
            .where(_Property.TYPE
                .equalTo("bookmarkedhotels"))
        
        /*
         {
         "type" : "bookmarkedhotelss"
         "hotels":["hotel1","hotel2"]
         }
 
        */
        
        for row in try searchQuery.run() {
            print("Bookmarked doc is \(row.toDictionary())")
            if let docId = row.string(forKey: "id") {
                return db.getDocument(docId)
            }
        }
        
        return nil
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
