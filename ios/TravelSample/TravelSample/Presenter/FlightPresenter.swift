//
//  FlightPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation


class FlightPresenter:FlightPresenterProtocol {
    
    weak var associatedView: PresentingViewProtocol?    
}


// MARK: N1QL
// Query Directly Against Cpuchbase Server (via the Couchbase Backend TryPy application)
extension FlightPresenter {
    private  var  dateFormatter:DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "MM/dd/yyyy"
        
        return formatter
    }
    
    
    func fetchFlightsForCurrentUserWithSource( _ source:FlightSearchCriteria,destination:FlightSearchCriteria,handler:@escaping (_ flights:Flights?, _ error:Error?)->Void) {
        // Example query:http://localhost:8080/api/flightPaths/Heathrow/San%20Diego%20Intl?leave=05/04/2017&return=leave=05/04/2017
        
        self.associatedView?.dataStartedLoading()
        let session = URLSession.shared
        var leaveDate = ""
        if let date = source.date {
            leaveDate = dateFormatter.string(from: date)
        }
        let searchPath = "\(source.name)/\(destination.name)"
        let escapedSearchPath = searchPath.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)
        let fullPath = "flightPaths/\(String(describing: escapedSearchPath!))?leave=\(leaveDate)"
        
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
extension FlightPresenter:PresenterProtocol {
    func attachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = view
        
    }
    func detachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = nil
    }
}
