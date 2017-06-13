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
    /*
    override func numberOfSections(in tableView: UITableView) -> Int {
        return TableViewSections.count
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
        case TableViewSections.name.rawValue:
            return TableViewSections.name.rows
        case TableViewSections.overview.rawValue :
           return TableViewSections.overview.rows
        case TableViewSections.description.rawValue :
          return TableViewSections.description.rows
        default:
            return 0
            
        }

    }
    
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    switch indexPath.section {
        case TableViewSections.name.rawValue:
            let cell = tableView.dequeueReusableCell(withIdentifier: "HotelNameCell", for: indexPath)
            cell.textLabel?.text = hotelDesc?["title"] as? String ?? ""
            cell.detailTextLabel?.text = hotelDesc?["url"] as? String ?? ""
            cell.selectionStyle = .none
            return cell 
        case TableViewSections.overview.rawValue :
            switch indexPath.row {
                case OverviewRowIndex.address.rawValue:
                    let cell = tableView.dequeueReusableCell(withIdentifier: "HotelAddressCell", for: indexPath)
                    cell.textLabel?.text =  "\(hotelDesc?["address"] as? String ?? "") ,\(hotelDesc?["city"] as? String ?? "") ,\(hotelDesc?["country"] as? String ?? "")"
                    cell.detailTextLabel?.text = hotelDesc?["email"] as? String ?? ""
                    cell.selectionStyle = .none

                    return cell

            case OverviewRowIndex.pet.rawValue:
                let cell = tableView.dequeueReusableCell(withIdentifier: "HotelPetsCell", for: indexPath)
                cell.detailTextLabel?.text = hotelDesc?["pets_ok"] as? Bool == true ? "true" : "false"
                cell.selectionStyle = .none
            case OverviewRowIndex.breakfast.rawValue:
                let cell = tableView.dequeueReusableCell(withIdentifier: "HotelBreakfastCell", for: indexPath)
                cell.detailTextLabel?.text = hotelDesc?["free_breakfast"] as? Bool == true ? "true" : "false"
                cell.selectionStyle = .none
            case OverviewRowIndex.internet.rawValue:
                let cell = tableView.dequeueReusableCell(withIdentifier: "HotelInternetCell", for: indexPath)
                cell.detailTextLabel?.text = hotelDesc?["free_internet"] as? Bool == true ? "true" : "false"
                cell.selectionStyle = .none
            case OverviewRowIndex.parking.rawValue:
                let cell = tableView.dequeueReusableCell(withIdentifier: "HotelParkingCell", for: indexPath)
                cell.detailTextLabel?.text = hotelDesc?["free_parking"] as? Bool == true ? "true" : "false"
                cell.selectionStyle = .none
            default:
               return UITableViewCell()
                
            }
        case TableViewSections.description.rawValue :
            let cell = tableView.dequeueReusableCell(withIdentifier: "HotelDetailCell", for: indexPath) as? HotelDetailCell
            cell?.detailTextView.text = hotelDesc?["description"] as? String ?? ""
            cell?.selectionStyle = .none
            return cell ?? UITableViewCell()

        
        default:
            return UITableViewCell()
        
        
        }
        return UITableViewCell()
    }
 */
    
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

