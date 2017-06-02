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
    
    enum SegmentIndex:Int {
        case outbound = 0
        case inbound
    }
    
    fileprivate var  inboundFlightListingTVC:FlightListingTableViewController  {
        let storyboard = UIStoryboard.getStoryboard(.Main)
        let _vc = storyboard.instantiateViewController(withIdentifier: "FlightListingTableViewController") as! FlightListingTableViewController
        _vc.category = "One"
        return _vc
    }
    fileprivate var outboundFlightListingTVC:FlightListingTableViewController {
        let storyboard = UIStoryboard.getStoryboard(.Main)
         let _vc = storyboard.instantiateViewController(withIdentifier: "FlightListingTableViewController") as! FlightListingTableViewController
        _vc.category = "Two"
        return _vc
    }
    
    private var firstTime = false

    fileprivate var currentFlightListingVC:FlightListingTableViewController?
    
    // MARK: View Related
    override public func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    public override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if firstTime == false {
            firstTime = true
            switchToViewController(outboundFlightListingTVC)
        }
    }
    
    public override func viewWillDisappear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
  
    
}

extension AddBookingContainerViewController {
    
    @IBAction func onCancelTapped(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func onSegmentSelected(_ sender:UISegmentedControl) {
        if sender.selectedSegmentIndex == SegmentIndex.inbound.rawValue {
            switchToViewController(inboundFlightListingTVC)
        }
        if sender.selectedSegmentIndex == SegmentIndex.outbound.rawValue {
            switchToViewController(outboundFlightListingTVC)
        }
        
    }
    
    
    
}

extension AddBookingContainerViewController {
    fileprivate func switchToViewController (_ controller:FlightListingTableViewController) {
        if currentFlightListingVC == controller {
            return
        }
        
        currentFlightListingVC?.view.removeFromSuperview()
        currentFlightListingVC?.removeFromParentViewController()
        
        self.addChildViewController(controller)
        controller.didMove(toParentViewController: self)
        controller.view.frame = listingContainerView.bounds
        listingContainerView.addSubview(controller.view)
        
        currentFlightListingVC = controller
    }
}
