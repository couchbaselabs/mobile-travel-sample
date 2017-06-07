//
//  HotelsTableViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/7/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit
class HotelsTableViewController:UITableViewController {
    
    lazy var hotelPresenter:HotelPresenter = HotelPresenter()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.title = NSLocalizedString("Bookings", comment: "")
        
        self.registerCells()
        self.initializeTable()
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
  //      self.hotelPresenter.attachPresentingView(self)
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(true)
   //     self.hotelPresenter.detachPresentingView(self)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
      //  self.bookingPresenter.fetchBookingsForCurrentUser(observeChanges: false)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    private func initializeTable() {
        //    self.tableView.backgroundColor = UIColor.darkGray
        self.tableView.backgroundColor = UIColor(colorLiteralRed: 242.0/255, green: 252.0/255, blue: 255.0/255, alpha: 1.0)
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.rowHeight = UITableViewAutomaticDimension
        self.tableView.sectionHeaderHeight = 10.0
        self.tableView.sectionFooterHeight = 10.0
        //  self.tableView.tableHeaderView = searchHeaderView()
    }
    
    
    private func registerCells() {
        self.tableView?.register(UITableViewCell.self, forCellReuseIdentifier: "BookingCell")
        
    }
    
    private func searchHeaderView() -> UIView {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: self.view.frame.width, height: 80))
        let searchBar =  UISearchBar.init(frame: CGRect(x: 0, y: 5, width: self.view.frame.width , height: 70))
        view.backgroundColor = UIColor.white
        searchBar.barTintColor = UIColor(colorLiteralRed: 242.0/255, green: 252.0/255, blue: 255.0/255, alpha: 1.0)
        
        searchBar.showsCancelButton = true
        searchBar.placeholder = NSLocalizedString("Search For Booking", comment: "")
        view.addSubview(searchBar)
  //      searchBar.delegate = self
        return view
        
    }

    @IBAction func onCancelTapped(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
}
