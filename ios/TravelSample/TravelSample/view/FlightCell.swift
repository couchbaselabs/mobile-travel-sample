//
//  FlightCell.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/9/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

class FlightCell: UITableViewCell {

    @IBOutlet weak var fare:UILabel!
    @IBOutlet weak var airline:UILabel!
    @IBOutlet weak var flight:UILabel!
    @IBOutlet weak var departureTime:UILabel!
    
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
    var flightValue:String? {
        didSet {
            updateUI()
        }
    }
    var departureTimeValue:String? {
        didSet {
            updateUI()
        }
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
        
        if let flightValue = flightValue {
            self.flight.text = flightValue
        }
        if let departureTimeValue = departureTimeValue {
            let traits = [UIFontDescriptor.AttributeName.face:"Bold Condensed"] // UIFontWeightBold / UIFontWeightRegular
            var titleFontDescriptor = UIFontDescriptor(fontAttributes: [UIFontDescriptor.AttributeName.family: "Helvetica Neue"])
            titleFontDescriptor = titleFontDescriptor.addingAttributes([UIFontDescriptor.AttributeName.traits:traits])
            
            let titleAttributes = [NSAttributedString.Key.foregroundColor: UIColor.darkText, NSAttributedString.Key.font: UIFont.init(descriptor: titleFontDescriptor, size: 15.0)]
            let valueAttributes = [NSAttributedString.Key.foregroundColor: UIColor.darkGray, NSAttributedString.Key.font: UIFont.systemFont(ofSize:15)]
            
            let title = NSMutableAttributedString(string: NSLocalizedString("Departure Time : ",comment:""), attributes: titleAttributes)
            let value = NSMutableAttributedString(string: departureTimeValue, attributes: valueAttributes)
            
            let combination = NSMutableAttributedString()
            
            combination.append(title)
            combination.append(value)
            self.departureTime.attributedText = combination
        }
               
    }

}
