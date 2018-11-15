//
//  AddBookingContainerViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/2/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

class AddBookingContainerViewController:UIViewController {
    
    @IBOutlet var listingContainerView:UIView!
    
    @IBOutlet weak var searchContainerView: UIView!
    @IBOutlet weak var searchViewHeightConstraint: NSLayoutConstraint!
    
    enum SearchViewHeight:CGFloat {
        case regular = 165.0
        case expanded = 250.0
        
    }
    fileprivate var outboundFlightSelection:Booking?
    fileprivate var returnFlightSelection:Booking?
    fileprivate var departureSearchCriteria:FlightSearchCriteria?
    fileprivate var returnSearchCriteria:FlightSearchCriteria?
    
    lazy var bookingPresenter:BookingPresenter = BookingPresenter()
    
    
    enum SegmentIndex:Int {
        case outbound = 0
        case inbound
    }
    
    fileprivate var _inboundFlightListingTVC:FlightListingTableViewController?
    fileprivate var  inboundFlightListingTVC:FlightListingTableViewController?  {
        if _inboundFlightListingTVC == nil {
            let storyboard = UIStoryboard.getStoryboard(.Main)
            _inboundFlightListingTVC = storyboard.instantiateViewController(withIdentifier: "FlightListingTableViewController") as? FlightListingTableViewController
            _inboundFlightListingTVC?.delegate = self
        }
        return _inboundFlightListingTVC
    }
    
    fileprivate var _outboundFlightListingTVC:FlightListingTableViewController?
    fileprivate var outboundFlightListingTVC:FlightListingTableViewController? {
        if _outboundFlightListingTVC == nil {
            
            let storyboard = UIStoryboard.getStoryboard(.Main)
            _outboundFlightListingTVC = storyboard.instantiateViewController(withIdentifier: "FlightListingTableViewController") as? FlightListingTableViewController
            _outboundFlightListingTVC?.delegate = self
        }
        return _outboundFlightListingTVC
    }
    
    private var firstTime = false

    fileprivate var currentFlightListingVC:FlightListingTableViewController?
    
    // MARK: View Related
    override public func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        searchViewHeightConstraint.constant = SearchViewHeight.regular.rawValue
    }
    
    public override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if firstTime == false {
            firstTime = true
            switchToViewController(outboundFlightListingTVC)
        }
    }
    
    public override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        self.bookingPresenter.attachPresentingView(self)
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(true)
        self.bookingPresenter.detachPresentingView(self)
    }
    
    
}

extension AddBookingContainerViewController {
    private  var  dateFormatter:DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "MM/dd/yyyy"
        
        return formatter
    }
    @IBAction func onCancelTapped(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    @IBAction func onConfirmBooking(_ sender: UIBarButtonItem) {
        var bookings:Bookings = []
        if var outboundFlightSelection = outboundFlightSelection {
            if let date = departureSearchCriteria?.date, let utc = outboundFlightSelection["utc"]  {
                outboundFlightSelection["date"] = "\(dateFormatter.string(from: date)) \(utc)"
            }
            bookings.append(outboundFlightSelection)
        }
        if var returnFlightSelection = returnFlightSelection {
            if let date = returnSearchCriteria?.date , let utc = returnFlightSelection["utc"] {
                returnFlightSelection["date"] = "\(dateFormatter.string(from: date)) \(utc)"
            }
            
            bookings.append( returnFlightSelection)
        }
        if bookings.count > 0{
           
            self.bookingPresenter.addFlightBookings(bookings) { (error) in
                switch error {
                case nil :
                    print("Succesfully added flight bookings!")
                    self.dismiss(animated: true, completion: nil)

                default:
                    self.showAlertWithTitle(NSLocalizedString("Flight Booking Error!", comment: ""), message: (error?.localizedDescription)!)
                }
            }
        }
    }
    
    @IBAction func onSegmentSelected(_ sender:UISegmentedControl) {
        if sender.selectedSegmentIndex == SegmentIndex.inbound.rawValue {
            if let departureSearchCriteria = departureSearchCriteria,
                        let returnSearchCriteria = returnSearchCriteria {
                if let currentSourceCriteria = inboundFlightListingTVC?.searchCriteria?.source, let currentDestCriteria = inboundFlightListingTVC?.searchCriteria?.destination {
                    if !(currentSourceCriteria == returnSearchCriteria && currentDestCriteria == departureSearchCriteria){
                         inboundFlightListingTVC?.searchCriteria = (source:returnSearchCriteria,destination:departureSearchCriteria)
                    }
                   
                }
                else {
                    inboundFlightListingTVC?.searchCriteria = (source:returnSearchCriteria,destination:departureSearchCriteria)
                    
                }
            
            }
            
            switchToViewController(inboundFlightListingTVC)
        }
        if sender.selectedSegmentIndex == SegmentIndex.outbound.rawValue {
           
            if let departureSearchCriteria = departureSearchCriteria,
                let returnSearchCriteria = returnSearchCriteria {
                if let currentSourceCriteria = outboundFlightListingTVC?.searchCriteria?.source, let currentDestCriteria = outboundFlightListingTVC?.searchCriteria?.destination {
                    if !(currentSourceCriteria == departureSearchCriteria && currentDestCriteria == returnSearchCriteria){
                        outboundFlightListingTVC?.searchCriteria = (source:departureSearchCriteria,destination:returnSearchCriteria)
                    }
                    
                }
                else {
                    outboundFlightListingTVC?.searchCriteria = (source:departureSearchCriteria,destination:returnSearchCriteria)
                    
                }
               
            }
        
            
            switchToViewController(outboundFlightListingTVC)
        }

    }
 
}

extension AddBookingContainerViewController {
    fileprivate func switchToViewController (_ controller:FlightListingTableViewController?) {
        guard let controller = controller else {
            return
        }
        if currentFlightListingVC == controller {
            return
        }
        
        currentFlightListingVC?.view.removeFromSuperview()
        currentFlightListingVC?.removeFromParent()
        
        self.addChild(controller)
        controller.didMove(toParent: self)
        controller.view.frame = listingContainerView.bounds
        listingContainerView.addSubview(controller.view)
        
        currentFlightListingVC = controller
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if UIStoryboard.StoryboardSegue.searchFlight.identifier == segue.identifier {
            if let destVC = segue.destination as? FlightSearchViewController {
                destVC.delegate = self
            }
        }
    }
}

extension AddBookingContainerViewController:FlightListingProtocol {
    func onSelectedFlight(_ details:Booking?){
        if currentFlightListingVC == inboundFlightListingTVC {
            returnFlightSelection = details
        }
        if currentFlightListingVC == outboundFlightListingTVC {
            outboundFlightSelection = details
        }
        
    }
}

// MARK :BookingPresentingViewProtocol
extension AddBookingContainerViewController:BookingPresentingViewProtocol {
    
}

// MARK:FlightSearchProtocol
extension AddBookingContainerViewController:FlightSearchProtocol {
    func onUpdatedSearchCriteria(_ source:FlightSearchCriteria, destination:FlightSearchCriteria) {
        print(#function)
        departureSearchCriteria = source
        returnSearchCriteria = destination
        currentFlightListingVC?.searchCriteria = (source:source,destination:destination)
    }
    
    func onAirportSearchTextFieldWasSelected(_ selected:Bool) {
        if selected == true {
            self.searchViewHeightConstraint.constant = SearchViewHeight.expanded.rawValue
        }
        else {
            self.searchViewHeightConstraint.constant = SearchViewHeight.regular.rawValue
        }
    }
}

