//
//  FlightPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation


class FlightPresenter:FlightPresenterProtocol {
    // Example query:http://localhost:8080/api/flightPaths/Heathrow/San%20Diego%20Intl?leave=05/04/2017&return=leave=05/04/2017
    
    let serverBackendUrl:URL? = URL.init(string: "http://localhost:8080/api/flightPaths")
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
        formatter.dateStyle = .short
        formatter.timeStyle = .none
        
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
        var queryPath = "flightPaths/\(source.name)/\(destination.name)?leave=\(leaveDate)"
        print("search queryPath is \(queryPath)")
        if let url = URL.init(string: queryPath, relativeTo: serverBackendUrl) {
            session.dataTask(with: url) { [weak self] (data, response, error) in
                self?.associatedView?.dataFinishedLoading()
                
                switch error {
                case nil:
                    if let httpResp = response as? HTTPURLResponse {
                        switch httpResp.statusCode {
                            //todo: Do CUstom Error
                            case 200 :
                                if let dataVal = data {
                                    do {
                                        if let flights = try JSONSerialization.jsonObject(with: dataVal, options:.allowFragments) as? Flights {
                                             handler(flights,nil)
                                        }
                                    }
                                
                                    catch {
                                         print("Failed to serialize JSON data")
                                        //TODO: Create custom error
                                         handler(nil,nil)
                                    }
                                }
                        
                        
                            default:
                                // TODO: Create custom error
                                print("Got status of \(httpResp)")
                                
                            }
                        
                    }
                default:
                    handler(nil,error)
                }
            }
        }
    }
    
    
    func attachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = view
        
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
    
}
