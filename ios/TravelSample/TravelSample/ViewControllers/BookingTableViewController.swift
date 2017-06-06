//
//  ViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/30/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

class BookingTableViewController: UITableViewController {

    lazy var bookingPresenter:BookingPresenter = BookingPresenter()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.title = NSLocalizedString("Bookings", comment: "")
        
        self.registerCells()
        self.initializeTable()
        
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        self.bookingPresenter.attachPresentingView(self)
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(true)
        self.bookingPresenter.detachPresentingView(self)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.bookingPresenter.fetchBookingsForCurrentUser(observeChanges: false)
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
        self.tableView.tableHeaderView = searchHeaderView()
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
        searchBar.delegate = self
        return view

    }
    
}

// MARK:UISearchBarDelegate
extension BookingTableViewController:UISearchBarDelegate {
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        let searchText = searchBar.text
        print("FTS on bookings for \(String(describing: searchText))")
    }
  
}

//MARK:UITableViewDataSource
extension BookingTableViewController {
    public override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    
    override public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = UITableViewCell(style: UITableViewCellStyle.subtitle, reuseIdentifier: "BookingCell")
        if bookingPresenter.bookings.count > indexPath.section {
            let booking = bookingPresenter.bookings[indexPath.section]
            
            cell.textLabel?.text = booking["flight"] as? String
        }
        cell.selectionStyle = .none
        return cell
        
    }
    
    override public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
        
    }
    
    
    public override func numberOfSections(in tableView: UITableView) -> Int {
        return (bookingPresenter.bookings.count) 
    }
    
    override public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
}

extension BookingTableViewController:BookingPresentingViewProtocol {
    func updateUIWithUpdatedBookings(_ bookings: Bookings?, error: Error?) {
         print(#function)
        switch error {
            case nil:
                self.tableView.reloadData()
            default:
                self.showAlertWithTitle(NSLocalizedString("Error!", comment: ""), message: NSLocalizedString("Fai", comment: ""))
        }
        //  Ideally, we want to add/remove table cells instead of entire reload but single document here with nested flight details!
     
    }
}

