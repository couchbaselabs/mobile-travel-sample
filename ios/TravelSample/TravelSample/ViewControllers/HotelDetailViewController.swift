//
//  HotelDetailViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/8/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

class HotelDetailViewController:UITableViewController {
    var hotelDesc:Hotel? {
        didSet {
           // self.tableView.reloadData()
            self.updateUI()
        }
    }
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var descriptionTextView: UITextView!
    
    @IBOutlet weak var parkingLabel: UILabel!
    @IBOutlet weak var internetLabel: UILabel!
    @IBOutlet weak var breakfastLabel: UILabel!
    @IBOutlet weak var petsLabel: UILabel!
    @IBOutlet weak var addressLabel: UILabel!
    @IBOutlet weak var urlLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.rowHeight = UITableViewAutomaticDimension
        self.updateUI()
      //  self.registerCells()
    }
    
    private func registerCells() {
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "HotelNameCell")
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "HotelAddressCell")
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "HotelPetsCell")
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "HotelParkingCell")
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "HotelInternetCell")
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "HotelBreakfastCell")
        self.tableView.register(HotelCell.self, forCellReuseIdentifier: "HotelDetailCell")
        
    }

}

// MARK: UITableViewDataSource
extension HotelDetailViewController {
    enum TableViewSections:Int {
        case name = 0
        case overview = 1
        case description = 2
        
        var rows:Int {
            switch self {
            case .name:
                return 1
           
            case .overview :
                return 5
                
            case .description :
                return 1
            }
        }
        static var count: Int {
            return 3
        }
        
    }
    
    enum OverviewRowIndex:Int {
        case address = 0
        case pet = 1
        case breakfast = 2
        case internet = 3
        case parking = 4
    }
       
    fileprivate func updateUI() {
        if let titleLabel = self.titleLabel {
            titleLabel.text = hotelDesc?["title"] as? String ?? ""
        }
        if let urlLabel = self.urlLabel {
            urlLabel.text = hotelDesc?["url"] as? String ?? ""
        }
        if let addressLabel = self.addressLabel {
            addressLabel.text = "\(hotelDesc?["address"] as? String ?? "") ,\(hotelDesc?["city"] as? String ?? "") ,\(hotelDesc?["country"] as? String ?? "")"
        }
        
        if let emailLabel = self.emailLabel {
            emailLabel.text = hotelDesc?["email"] as? String ?? ""

        }
        if let petsLabel = self.petsLabel {
            petsLabel.text = hotelDesc?["pets_ok"] as? Bool == true ? "true" : "false"
        }
        if let parkingLabel = self.parkingLabel {
            parkingLabel.text = hotelDesc?["free_parking"] as? Bool == true ? "true" : "false"
        }
        if let internetLabel = self.internetLabel {
            internetLabel.text = hotelDesc?["free_internet"] as? Bool == true ? "true" : "false"
        }
        if let breakfastLabel = self.breakfastLabel {
            breakfastLabel.text = hotelDesc?["free_breakfast"] as? Bool == true ? "true" : "false"
        }
        if let descriptionTextView = self.descriptionTextView {
            descriptionTextView.text = hotelDesc?["description"] as? String ?? ""
        }
    }
    
}

