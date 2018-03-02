//
//  HotelsTableViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/7/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit
class HotelsTableViewController:UITableViewController ,UIViewControllerPreviewingDelegate, PresentingViewProtocol{
    
    lazy var hotelPresenter:HotelPresenter = HotelPresenter()
    fileprivate var descriptionSearchBar:UISearchBar!
    fileprivate var locationSearchBar:UISearchBar!
    fileprivate var searchButton:UIButton!
    var hotels:Hotels?
    
    var inGuestMode:Bool = false
    var bookmarkedHotels:Hotels? // Used only in guest mode to mark ones that were bookmarked
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.title = NSLocalizedString("Hotels", comment: "")
        registerForPreviewing(with: self, sourceView: self.tableView)
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
        self.tableView.rowHeight = 80
        self.tableView.sectionHeaderHeight = 10.0
        self.tableView.sectionFooterHeight = 10.0
        self.tableView.tableHeaderView = searchHeaderView()
    }
    
    
    
    private func searchHeaderView() -> UIView {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: self.view.frame.width, height: 150))
        descriptionSearchBar =  UISearchBar.init(frame: CGRect(x: 0, y: 5, width: self.view.frame.width , height: 40))
        view.backgroundColor = UIColor.white
        descriptionSearchBar.barTintColor = UIColor(colorLiteralRed: 228.0/255, green: 244.0/255, blue: 248.0/255, alpha: 1.0)
        descriptionSearchBar.showsCancelButton = true
        descriptionSearchBar.placeholder = NSLocalizedString("Description (optional)", comment: "")
        view.addSubview(descriptionSearchBar)
        descriptionSearchBar.delegate = self
        
        
        locationSearchBar =  UISearchBar.init(frame: CGRect(x: 0, y: 50, width: self.view.frame.width , height: 40))
        view.backgroundColor = UIColor.white
        locationSearchBar.barTintColor = UIColor(colorLiteralRed: 228.0/255, green: 244.0/255, blue: 248.0/255, alpha: 1.0)        
        locationSearchBar.showsCancelButton = true
        locationSearchBar.placeholder = NSLocalizedString("Location : Eg.'France', 'London'", comment: "")
        view.addSubview(locationSearchBar)
        locationSearchBar.delegate = self
        
        
        searchButton =  UIButton.init(type: .custom)
        searchButton.frame =  CGRect(x: 5, y: 100, width: self.view.frame.width - 10, height: 44)
        searchButton.addTarget(self, action: #selector(onHotelsLookup), for: UIControlEvents.touchUpInside)
        searchButton.setTitle(NSLocalizedString("Lookup", comment: ""), for: UIControlState.normal)
        searchButton.setBackgroundImage(#imageLiteral(resourceName: "cyan"), for: UIControlState.normal)
        searchButton.setTitleColor(UIColor.gray, for: UIControlState.disabled)
         searchButton.setTitleColor(UIColor.white, for: UIControlState.normal)
        searchButton.isEnabled = false
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
        if inGuestMode == false {
            self.hotelPresenter.fetchHotelsMatchingDescription(descriptionSearchBar.text, location: locationStr, fromLocalStore: true, handler: { [weak self](hotels, error) in
                switch error {
                case nil:
                    self?.hotels = hotels
                    self?.tableView.reloadData()
                default:
                    self?.showAlertWithTitle(NSLocalizedString("Failed to Fetch Hotel Info!", comment: ""), message: error?.localizedDescription ?? "")
                    
                    print("Error when fetching hotels \(error?.localizedDescription)")
                
                }
            })
        }
        else {
            self.hotelPresenter.fetchHotelsMatchingDescription(descriptionSearchBar.text, location: locationStr, fromLocalStore: false, handler: { [weak self](hotels, error) in
                switch error {
                case nil:
                    self?.hotels = hotels
                    self?.tableView.reloadData()
                default:
                    self?.showAlertWithTitle(NSLocalizedString("Failed to Fetch Hotel Info!", comment: ""), message: error?.localizedDescription ?? "")
                    
                    print("Error when fetching hotels \(error?.localizedDescription)")
                    
                }
            })
            
        }
        
    }
}
// MARK:UISearchBarDelegate
extension HotelsTableViewController:UISearchBarDelegate {
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        let searchText = searchBar.text
        print("FTS on hotels for \(String(describing: searchText))")
    }
    
    func searchBar(_ searchBar: UISearchBar, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        let length = (searchBar.text?.count)! - range.length + text.count
        let locationLength = (searchBar == self.locationSearchBar) ? length : self.locationSearchBar.text?.count
        
        self.searchButton.isEnabled = (locationLength! > 0 )
        
        return true;
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
        let cell:HotelCell = tableView.dequeueReusableCell(withIdentifier: "HotelCell", for: indexPath) as! HotelCell
        
        guard let hotels = self.hotels else {
            return cell
        }
        if hotels.count > indexPath.section {
            let hotel = hotels[indexPath.section]
            let isBookmarked = bookmarkedHotels?.filter({ (dict) -> Bool in
                if let id1 = dict["id"] as? String, let id2 = hotel["id"] as? String{
                    return id1 == id2
                }
                return false
            })
            
            cell.name.text = hotel["name"] as? String
            cell.address.text = hotel["address"] as? String
            cell.phone.text = hotel["phone"] as? String
            if let count = isBookmarked?.count {
                cell.isBookmarked = count > 0 ? true: false
            }
        }
        cell.selectionStyle = .none
        return cell
    }
    
    
}



// MARK:UIViewControllerPreviewingDelegate
extension HotelsTableViewController {
    public func previewingContext(_ previewingContext: UIViewControllerPreviewing, viewControllerForLocation location: CGPoint) -> UIViewController? {
        guard let indexPath = tableView.indexPathForRow(at: location) else { return nil }
        if let detailVC = detailViewControllerFor(indexPath:indexPath) {
            let cellRect = tableView.rectForRow(at: indexPath)
            let sourceRect = previewingContext.sourceView.convert(cellRect, from: tableView)
            previewingContext.sourceRect = sourceRect
            return detailVC
        }
        
        return nil
        
    }
    
    @objc(previewingContext:commitViewController:) public func previewingContext(_ previewingContext: UIViewControllerPreviewing, commit viewControllerToCommit: UIViewController) {
        
        show(viewControllerToCommit, sender: self)
    }
    fileprivate func detailViewControllerFor(indexPath:IndexPath) -> HotelDetailViewController? {
        let storyboard = UIStoryboard.getStoryboard(.Main)
        
        guard let detailVC = storyboard.instantiateViewController(withIdentifier: "HotelDetailViewController") as? HotelDetailViewController else {
            return nil
        }
        guard let hotels = self.hotels else {
            return detailVC
        }
        if hotels.count > indexPath.section {
            let hotel = hotels[indexPath.section]
            detailVC.hotelDesc = hotel
            
        }
               
        return detailVC
        
    }
    
}


// MARK:UITableViewDelegate
extension HotelsTableViewController {
    public override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let detailVC = self.detailViewControllerFor(indexPath: indexPath) {
            self.navigationController?.pushViewController(detailVC, animated: true)
            
        }
    }
    
    override public func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        
        if inGuestMode == false {
            return []
        }
        guard let cell = tableView.cellForRow(at: indexPath) as? HotelCell  else {
            return nil
        }
        let actionType = cell.isBookmarked == true ? NSLocalizedString("UnBookmark", comment: ""): NSLocalizedString("Bookmark", comment: "")
        let actionStyle:UITableViewRowActionStyle  = cell.isBookmarked == true ? .normal :.default
        
        switch actionType {
            case "Bookmark":
                let bookmarkAction = UITableViewRowAction(style: actionStyle, title: actionType, handler: { [weak self] (action, indexPath) in
                    // bookmark hotel document at index
                    if let hotelToBM = self?.hotels?[indexPath.section] {
                        
                        self?.hotelPresenter.bookmarkHotels([hotelToBM], handler: { (error) in
                            if let error = error {
                                self?.showAlertWithTitle(NSLocalizedString("Failed to Bookmark!", comment: ""), message: error.localizedDescription)
                            }
                            else {
                                cell.isBookmarked = !cell.isBookmarked
                                
                            }
                            tableView.setEditing(false, animated: true)
                        })
                        
                        
                    }
                })
                return [bookmarkAction]
            case "UnBookmark":
                let bookmarkAction = UITableViewRowAction(style: actionStyle, title: actionType, handler: { [weak self] (action, indexPath) in
                    // bookmark hotel document at index
                    if let hotelToUBM = self?.hotels?[indexPath.section] {
                        
                        self?.hotelPresenter.unbookmarkHotels([hotelToUBM], handler: { (error) in
                            if let error = error {
                                self?.showAlertWithTitle(NSLocalizedString("Failed to UnBookmark!", comment: ""), message: error.localizedDescription)
                            }
                            else {
                                cell.isBookmarked = !cell.isBookmarked
                                
                            }
                            tableView.setEditing(false, animated: true)
                        })
                        
                        
                    }
                })
                return [bookmarkAction]
            
        default:
            return nil
        }
        
    }

}

