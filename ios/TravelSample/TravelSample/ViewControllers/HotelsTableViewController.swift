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
    fileprivate var descriptionSearchBar:UISearchBar!
    fileprivate var locationSearchBar:UISearchBar!
    var hotels:Hotels?
    
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
         self.tableView.tableHeaderView = searchHeaderView()
    }
    
    
    private func registerCells() {
        self.tableView?.register(UITableViewCell.self, forCellReuseIdentifier: "HotelCell")
        
    }
    
    private func searchHeaderView() -> UIView {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: self.view.frame.width, height: 150))
        descriptionSearchBar =  UISearchBar.init(frame: CGRect(x: 0, y: 5, width: self.view.frame.width , height: 40))
        view.backgroundColor = UIColor.white
        descriptionSearchBar.barTintColor = UIColor(colorLiteralRed: 242.0/255, green: 252.0/255, blue: 255.0/255, alpha: 1.0)
        descriptionSearchBar.showsCancelButton = true
        descriptionSearchBar.placeholder = NSLocalizedString("Description (optional)", comment: "")
        view.addSubview(descriptionSearchBar)
        descriptionSearchBar.delegate = self
        
        
        locationSearchBar =  UISearchBar.init(frame: CGRect(x: 0, y: 50, width: self.view.frame.width , height: 40))
        view.backgroundColor = UIColor.white
        locationSearchBar.barTintColor = UIColor(colorLiteralRed: 242.0/255, green: 252.0/255, blue: 255.0/255, alpha: 1.0)        
        locationSearchBar.showsCancelButton = true
        locationSearchBar.placeholder = NSLocalizedString("Location : Eg.'New York', 'London'", comment: "")
        view.addSubview(locationSearchBar)
        locationSearchBar.delegate = self
        
        
        let searchButton = UIButton(frame: CGRect(x: 5, y: 100, width: self.view.frame.width - 10, height: 40))
        searchButton.addTarget(self, action: #selector(onHotelsLookup), for: UIControlEvents.touchUpInside)
        searchButton.titleLabel?.text = NSLocalizedString("Lookup", comment: "")
        searchButton.tintColor = UIColor.g
        view.addSubview(searchButton)
        return view
        
    }

    @IBAction func onCancelTapped(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
}

extension HotelsTableViewController {
    @objc func onHotelsLookup(sender:UIButton) {
        guard let locationStr = locationSearchBar.text else {
            return
        }
        self.hotelPresenter.fetchHotelsMatchingDescription(descriptionSearchBar.text, location: locationStr, handler: { [weak self](hotels, error) in
            switch error {
            case nil:
                self?.hotels = hotels
                self?.tableView.reloadData()
            default:
                print("Error when fetching hotels \(error?.localizedDescription)")
                
            }
        })
        
    }
}
// MARK:UISearchBarDelegate
extension HotelsTableViewController:UISearchBarDelegate {
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        let searchText = searchBar.text
        print("FTS on bookings for \(String(describing: searchText))")
    }
    
}

// MARK: - Table view data source
extension HotelsTableViewController {
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return self.hotels?.count ?? 0
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 1
    }
    
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: UITableViewCellStyle.subtitle, reuseIdentifier: "HotelCell")
        guard let hotels = self.hotels else {
            return cell
        }
        if hotels.count > indexPath.section {
            let hotel = hotels[indexPath.section]
            
            cell.textLabel?.text = hotel["title"] as? String
        }
        cell.selectionStyle = .none
        return cell
    }
    
    
}

// MARK: - Table view data source
extension HotelsTableViewController {
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
}

