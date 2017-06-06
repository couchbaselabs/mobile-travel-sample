//
//  FlightSearchViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/6/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

protocol FlightSearchProtocol:class {
    func onUpdatedSearchCriteria(_ source:FlightSearchCriteria, destination:FlightSearchCriteria)
    
}

class FlightSearchViewController: UIViewController {
    @IBOutlet weak var returnDateTextField: UITextField!
    
    @IBOutlet weak var departureDateTextField: UITextField!
    @IBOutlet weak var toTextField: UITextField!
    @IBOutlet weak var fromTextField: UITextField!
    
    @IBOutlet weak var lookupButton: UIBarButtonItem!
    weak var delegate:FlightSearchProtocol?
    override func viewDidLoad() {
        super.viewDidLoad()
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
}

// MARK: IBAction
extension FlightSearchViewController {
    fileprivate  var  dateFormatter:DateFormatter {
        // TODO: THis stuff will go away when date picker is used to select date
        let formatter = DateFormatter()
        formatter.dateFormat = "MM/dd/yyyy"
        
        return formatter
    }
    @IBAction func onFlightLookupSelected(_ sender: UIBarButtonItem) {
        guard let from = fromTextField.text, let to = toTextField.text, let departureDate = departureDateTextField.text else {
            return
        }
        
        let source = FlightSearchCriteria(name:from, date:dateFormatter.date(from: departureDate))
        var destination = FlightSearchCriteria(name:to, date:nil)
        if let returnDate = returnDateTextField.text {
            destination.date = dateFormatter.date(from: returnDate)
        }
    
        self.delegate?.onUpdatedSearchCriteria(source, destination: destination)
    }
}

extension FlightSearchViewController :UITextFieldDelegate{
    
    public func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let length = (textField.text?.characters.count)! - range.length + string.characters.count
        let fromLength = (textField == self.fromTextField) ? length : self.fromTextField.text?.characters.count
        let toLength = (textField == self.toTextField) ? length : self.toTextField.text?.characters.count
        let departureLength = (textField == self.departureDateTextField) ? length : self.departureDateTextField.text?.characters.count
        
        self.lookupButton.isEnabled = (fromLength! > 0 && toLength! > 0 && departureLength! > 0)
        
        return true;
    }
    

}
