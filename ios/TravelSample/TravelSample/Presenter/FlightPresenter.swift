//
//  FlightPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation

/*Data Model of Flight
 {
 "data": [
 {
 "destinationairport": "LHR",
 "equipment": "777",
 "flight": "AA090",
 "flighttime": 3943.0,
 "name": "American Airlines",
 "price": 492.88,
 "sourceairport": "SAN",
 "utc": "17:44:00"
 }
 ]
 }
 */
class FlightPresenter:FlightPresenterProtocol {
    // Example query:http://localhost:8080/api/flightPaths/Heathrow/San%20Diego%20Intl?leave=05/04/2017&return=leave=05/04/2017
    
    let serverBackendUrl:URL? = URL.init(string: "http://localhost:8080/api/")
    private var dbMgr:DatabaseManager = DatabaseManager.shared
    weak var associatedView: PresentingViewProtocol?

    private var _flights:Flights = Flights()
    var flights:Flights {
        get {
            return _flights
        }
    }
    
    fileprivate  var  dateFormatter:DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "MM/dd/yyyy"
        
        return formatter
    }
    
    func fetchFlightsForCurrentUserWithSource( _ source:FlightSearchCriteria,destination:FlightSearchCriteria,handler:@escaping (_ flights:Flights?, _ error:Error?)->Void) {
         _flights.removeAll()
        self.associatedView?.dataStartedLoading()
        let session = URLSession.shared
        var leaveDate = ""
        if let date = source.date {
            leaveDate = dateFormatter.string(from: date)
        }
        let searchPath = "\(source.name)/\(destination.name)"
        let escapedSearchPath = searchPath.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)
        let fullPath = "flightPaths/\(String(describing: escapedSearchPath!))?leave=\(leaveDate)"
        
        if let url = URL.init(string: fullPath, relativeTo: serverBackendUrl) {
            let dataTask = session.dataTask(with: url) { [weak self] (data, response, error) in
                self?.associatedView?.dataFinishedLoading()
                
                switch error {
                case nil:
                    if let httpResp = response as? HTTPURLResponse {
                        switch httpResp.statusCode {
                            //todo: Do CUstom Error
                            case 200 :
                                if let dataVal = data {
                                    do {
                                        if let flightData = try JSONSerialization.jsonObject(with: dataVal, options:.allowFragments) as? [String:Flights] {
                                            DispatchQueue.main.async {
                                                 handler(flightData["data"],nil)
                                            }
                                            
                                        }
                                    }
                                
                                    catch {
                                         print("Failed to serialize JSON data")
                                        //TODO: Create custom error
                                        DispatchQueue.main.async {
                                            handler(nil,nil)
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
    
    
    func attachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = view
        
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
    
}
