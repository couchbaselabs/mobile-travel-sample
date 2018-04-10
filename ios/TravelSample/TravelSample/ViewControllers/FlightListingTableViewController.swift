//
//  FlightListingTableViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

protocol FlightListingProtocol:class {
    func onSelectedFlight(_ details:Booking?)
    
}

class FlightListingTableViewController: UITableViewController {

    lazy var flightPresenter:FlightPresenter = FlightPresenter()
    weak var delegate:FlightListingProtocol?
    
    fileprivate var flights:Flights?
    fileprivate var indexPathOfCurrentSelectedFlight:IndexPath?
    
    
    var searchCriteria:(source:FlightSearchCriteria,destination:FlightSearchCriteria)? {
        didSet {
            // Do a N1QL Query directly on server to fetch the flight details
            if let source = searchCriteria?.source, let destination = searchCriteria?.destination {
            flightPresenter.fetchFlightsForCurrentUserWithSource(source, destination: destination) { [weak self](flights, error) in
                switch error {
                case nil:
                    self?.flights = flights
                    self?.tableView.reloadData()
                default:
                    self?.showAlertWithTitle(NSLocalizedString("Error!", comment: ""), message: NSLocalizedString("There was an error when trying to fetch flight details!", comment: ""))
                }
            }
            }
        }
    }
    
    private func registerCells() {
        self.tableView?.register(UITableViewCell.self, forCellReuseIdentifier: "FlightCell")
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.flightPresenter.attachPresentingView(self)
      
    
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(true)
        self.flightPresenter.detachPresentingView(self)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
   
}

// MARK: - Table view data source
extension FlightListingTableViewController {
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return self.flights?.count ?? 0
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 1
    }
    
    
     override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:FlightCell = tableView.dequeueReusableCell(withIdentifier: "FlightCell", for: indexPath) as! FlightCell
        
        guard let flights = self.flights else {
            return cell
        }
        
        if flights.count > indexPath.section {
            let flight = flights[indexPath.section]
            
            
            cell.airlineValue = flight["name"] as? String
            if let fare = flight["price"] as? CGFloat {
                   cell.fareValue = "$ \(String(describing: fare))"
            }
            else {
                cell.fareValue = "$ \(String(describing: flight["price"] as? Int ))"
            }
         
            cell.departureTimeValue = flight["utc"] as? String
            cell.flightValue = flight["flight"] as? String
         
            
        }

        let uncheckImage = #imageLiteral(resourceName: "check_notfilled")
        
        cell.accessoryView =  UIImageView.init(image:uncheckImage)
        cell.accessoryView?.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
        cell.selectionStyle = .none
        return cell
     }
     

}

// MARK: - Table view data source
extension FlightListingTableViewController {
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        // Logic to allow selection of only one flight listing
        let cell = tableView.cellForRow(at: indexPath)
        if indexPathOfCurrentSelectedFlight == indexPath {
            let uncheckImage = #imageLiteral(resourceName: "check_notfilled")
            
            cell?.accessoryView = UIImageView.init(image:uncheckImage)
            cell?.accessoryView?.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
            
            indexPathOfCurrentSelectedFlight = nil
            delegate?.onSelectedFlight(nil)
            
        }
        else {
            if let indexPathOfCurrentSelectedFlight = indexPathOfCurrentSelectedFlight {
                let prevSelectedCell = tableView.cellForRow(at: indexPathOfCurrentSelectedFlight)
                prevSelectedCell?.accessoryType = UITableViewCellAccessoryType.none
            }
            indexPathOfCurrentSelectedFlight = indexPath
            let checkImage = #imageLiteral(resourceName: "check_filled")
            
            cell?.accessoryView = UIImageView.init(image:checkImage)
            cell?.accessoryView?.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
            
            let flight = flights?[indexPath.section]
            delegate?.onSelectedFlight(flight)
        }
    }
}

extension FlightListingTableViewController:PresentingViewProtocol {
    // Nothing to override. Just go with the default implementation
}
