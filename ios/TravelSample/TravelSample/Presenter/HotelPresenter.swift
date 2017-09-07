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
    
    
    
    func bookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void) {
        
        
    }
    func unbookmarkHotels(_ hotels: BookmarkHotels, handler:@escaping( _ error:Error?)->Void) {
        
    }

    
}


// MARL Private
extension HotelPresenter {
    
    func fetchHotelsFromLocalDatabaseMatchingDescription( _ descriptionStr:String?,location locationStr:String, handler:@escaping(_ hotels:Hotels?, _ error:Error?)->Void) {
        guard let db = dbMgr.db else {
            fatalError("db is not initialized at this point!")
        }
        
        // Description is looked up in the "description" and "name" content
        // Location is looked up in country, city, state and address
        // Reference :https://developer.couchbase.com/documentation/server/4.6/sdk/sample-application.html
        var descExp:Expression?
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
        
        
        let hotelSearchQuery = Query
            .select(_SelectColumn.ALLRESULT) // CHANGE THIS WHEN SELECT* IS SUPPORTED
            .from(DataSource.database(db))
            .where(_Property.TYPE
                .equalTo("hotel")
                .and(searchExp))
        
        print(try! hotelSearchQuery.explain())
        
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
        let descStr = descriptionStr ?? "*"
        let searchPath = "\(descStr)/\(locationStr)"
        let escapedSearchPath = searchPath.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)
        let fullPath = "hotel/\(String(describing: escapedSearchPath!))"
        
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
