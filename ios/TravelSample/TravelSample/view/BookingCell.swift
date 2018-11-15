//
//  BookingCell.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/9/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

class BookingCell: UITableViewCell {

    @IBOutlet weak var fare:UILabel!
    @IBOutlet weak var airline:UILabel!
    @IBOutlet weak var date:UILabel!
    @IBOutlet weak var departureAirport:UILabel!
    @IBOutlet weak var arrivalAirport:UILabel!
    
    var fareValue:String? {
        didSet {
            updateUI()
        }
    }
    var airlineValue:String? {
        didSet {
            updateUI()
        }
    }
    var dateValue:String? {
        didSet {
            updateUI()
        }
    }
    var departureAirportValue:String? {
        didSet {
            updateUI()
        }
    }
    var arrivalAirportValue:String? {
        didSet {
            updateUI()
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    private func updateUI() {
        if let fareValue = fareValue {
            self.fare.text = fareValue
        }
        
        if let airlineValue = airlineValue {
            self.airline.text = airlineValue
        }
        
        if let departureValue = departureAirportValue {
            let traits = [UIFontDescriptor.AttributeName.face:"Bold Condensed"] // UIFontWeightBold / UIFontWeightRegular
            var titleFontDescriptor = UIFontDescriptor(fontAttributes: [UIFontDescriptor.AttributeName.family: "Helvetica Neue"])
            titleFontDescriptor = titleFontDescriptor.addingAttributes([UIFontDescriptor.AttributeName.traits:traits])
            
            let titleAttributes = [NSAttributedString.Key.foregroundColor: UIColor.darkText, NSAttributedString.Key.font: UIFont.init(descriptor: titleFontDescriptor, size: 15.0)]
            let valueAttributes = [NSAttributedString.Key.foregroundColor: UIColor.darkGray, NSAttributedString.Key.font: UIFont.systemFont(ofSize:15)]
            
            let title = NSMutableAttributedString(string: NSLocalizedString("Departure : ",comment:""), attributes: titleAttributes)
            let value = NSMutableAttributedString(string: departureValue, attributes: valueAttributes)
            
            let combination = NSMutableAttributedString()
            
            combination.append(title)
            combination.append(value)
            self.departureAirport.attributedText = combination
        }
        
        if let arrivalValue = arrivalAirportValue {
            let traits = [UIFontDescriptor.AttributeName.face:"Bold Condensed"] // UIFontWeightBold / UIFontWeightRegular
            var titleFontDescriptor = UIFontDescriptor(fontAttributes: [UIFontDescriptor.AttributeName.family: "Helvetica Neue"])
            titleFontDescriptor = titleFontDescriptor.addingAttributes([UIFontDescriptor.AttributeName.traits:traits])
            
            let titleAttributes = [NSAttributedString.Key.foregroundColor: UIColor.darkText, NSAttributedString.Key.font: UIFont.init(descriptor: titleFontDescriptor, size: 15.0)]
            let valueAttributes = [NSAttributedString.Key.foregroundColor: UIColor.darkGray, NSAttributedString.Key.font: UIFont.systemFont(ofSize:15)]
            
            let title = NSMutableAttributedString(string: NSLocalizedString("Arrival : ",comment:""), attributes: titleAttributes)
            let value = NSMutableAttributedString(string: arrivalValue, attributes: valueAttributes)
            
            let combination = NSMutableAttributedString()
            
            combination.append(title)
            combination.append(value)
            self.arrivalAirport.attributedText = combination
        }
        
        if let dateValue = dateValue {
             self.date.text = dateValue
        }
        
    }

}
