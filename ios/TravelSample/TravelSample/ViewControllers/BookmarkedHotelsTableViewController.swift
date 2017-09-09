//
//  BookmarkedHotelsTableViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/30/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

class BookmarkedHotelsTableViewController: UITableViewController {

    lazy var bookmarkHotelPresenter:HotelPresenter = HotelPresenter()
    var hotels:Hotels?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.title = NSLocalizedString("Bookmarked Hotels", comment: "")
        
         self.initializeTable()
        
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(true)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    private func initializeTable() {
        //    self.tableView.backgroundColor = UIColor.darkGray
        self.tableView.backgroundColor = UIColor(colorLiteralRed: 252.0/255, green: 252.0/255, blue: 252.0/255, alpha: 1.0)
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.sectionHeaderHeight = 10.0
        self.tableView.sectionFooterHeight = 10.0
        self.tableView.rowHeight = 120        
    }
    
    
    
    @IBAction func onLogoutTapped(_ sender: UIBarButtonItem) {
        let cbMgr = DatabaseManager.shared
        let _ = cbMgr.closeDatabaseForCurrentUser()
        NotificationCenter.default.post(Notification.notificationForLogOut())
    }

    
}

//MARK:UITableViewDataSource
extension BookmarkedHotelsTableViewController {
    public override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    
    override public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:HotelCell = tableView.dequeueReusableCell(withIdentifier: "HotelCell", for: indexPath) as! HotelCell
        guard let hotels = self.hotels else {
            return cell
        }
        if hotels.count > indexPath.section {
            let hotel = hotels[indexPath.section]
            
            cell.name.text = hotel["name"] as? String
            cell.address.text = hotel["address"] as? String
            cell.phone.text = hotel["phone"] as? String
        }
        cell.selectionStyle = .none
        return cell
        
    }
    
    
    public override func numberOfSections(in tableView: UITableView) -> Int {
        return self.hotels?.count ?? 0
    }
    
    override public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
    
}

// MARK: UITableViewDelegate
extension BookmarkedHotelsTableViewController {
    override public func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        let bookmarkAction = UITableViewRowAction(style: .normal, title: NSLocalizedString("UnBookmark", comment: ""), handler: { [weak self] (action, indexPath) in
            
            
        })
        return [bookmarkAction]
        
    }


}
// MARK: Navigation
extension BookmarkedHotelsTableViewController {
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if UIStoryboard.StoryboardSegue.searchHotelInGuestMode.identifier == segue.identifier {
            if let destVC = segue.destination as? UINavigationController, let hotelVC = destVC.topViewController as? HotelsTableViewController {
                hotelVC.inGuestMode = true
            }
        }
    }
}


