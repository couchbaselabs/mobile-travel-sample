//
//  HotelCell.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/13/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

class HotelCell: UITableViewCell {
    
    @IBOutlet weak var name:UILabel!
    @IBOutlet weak var address:UILabel!
    @IBOutlet weak var phone:UITextView!
    
    @IBOutlet weak var bookmarkImageView: UIImageView! {
        didSet {
            isBookmarked = false
        }
    }
    var nameValue:String? {
        didSet {
            updateUI()
        }
    }
    var addressValue:String? {
        didSet {
            updateUI()
        }
    }
    var phoneValue:String? {
        didSet {
            updateUI()
        }
    }
  
    var isBookmarked:Bool = false{
        didSet {
            updateUI()
        }
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
    private func updateUI() {
        if let nameValue = nameValue {
            self.name.text = nameValue
        }
        
        if let addressValue = addressValue {
            self.address.text = addressValue
        }
        
        if let phoneValue = phoneValue {
            self.phone.text = phoneValue
        }
        if bookmarkImageView != nil {
            self.bookmarkImageView.isHidden = isBookmarked == false ? true : false
        }
        
    }
    
}
