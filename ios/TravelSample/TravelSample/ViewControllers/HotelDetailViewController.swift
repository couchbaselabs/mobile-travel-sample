//
//  HotelDetailViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/8/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

class HotelDetailViewController:UITableViewController {
    var hotelDesc:String? {
        didSet {
            self.tableView.reloadData()
        }
    }
    @IBOutlet weak var detailsTextView: UITextView!
    
   
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.rowHeight = self.view.frame.size.height
    }
    
}

// MARK: UITableViewDataSource
extension HotelDetailViewController {
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 1
    }
    
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "HotelDetailCell") as? HotelDetailCell
        cell?.detailTextView.text = hotelDesc
        cell?.selectionStyle = .none
        return cell ?? UITableViewCell()

    }
    
    
}
