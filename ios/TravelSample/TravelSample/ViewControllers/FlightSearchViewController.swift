//
//  FlightSearchViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/6/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

protocol FlightSearchProtocol:class {
    func onAirportSearchTextFieldWasSelected(_ selected:Bool)
    func onUpdatedSearchCriteria(_ source:FlightSearchCriteria, destination:FlightSearchCriteria)
    
}


class FlightSearchViewController: UIViewController {
    lazy var airportPresenter:AirportPresenter = AirportPresenter()
    weak var delegate:FlightSearchProtocol?
    var airports:Airports?

    @IBOutlet weak var returnDateTextField: UITextField!
    
    @IBOutlet weak var departureDateTextField: UITextField!
    @IBOutlet weak var toTextField: UITextField!
    @IBOutlet weak var fromTextField: UITextField!
    
    @IBOutlet weak var toTableView: UITableView!
    @IBOutlet weak var lookupButton: UIBarButtonItem!
    @IBOutlet weak var fromTableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.registerCells()
    }
    
   
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        self.airportPresenter.attachPresentingView(self)
        self.addTextFieldChangeObservers()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(true)
         self.airportPresenter.detachPresentingView(self)
        self.removeTextFieldChangeObservers()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    private func registerCells() {
        self.fromTableView?.register(UITableViewCell.self, forCellReuseIdentifier: "AirportCell")
        self.toTableView?.register(UITableViewCell.self, forCellReuseIdentifier: "AirportCell")
        
        
    }
    
    private func addTextFieldChangeObservers() {
        NotificationCenter.default.addObserver(forName: NSNotification.Name.UITextFieldTextDidChange, object: fromTextField, queue: nil) { (notification) in
            self.handleAirportSearchWith(self.fromTextField.text ?? "", isFromAirport: true)
        }
        
        NotificationCenter.default.addObserver(forName: NSNotification.Name.UITextFieldTextDidChange, object: toTextField, queue: nil) { (notification) in
            self.handleAirportSearchWith(self.toTextField.text ?? "", isFromAirport: false)
        }
        
    }
    
    private func removeTextFieldChangeObservers() {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UITextFieldTextDidChange, object: fromTextField)
         NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UITextFieldTextDidChange, object: toTextField)
        
        
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
    func textFieldDidBeginEditing(_ textField: UITextField) {
        if textField == fromTextField {
            self.fromTableView.isHidden = false
            self.toTableView.isHidden = true
            self.delegate?.onAirportSearchTextFieldWasSelected(true)
        }
        
        if textField == toTextField {
            self.toTableView.isHidden = false
            self.fromTableView.isHidden = true
            self.delegate?.onAirportSearchTextFieldWasSelected(true)
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        self.delegate?.onAirportSearchTextFieldWasSelected(false)
        self.fromTableView.isHidden = true
        self.toTableView.isHidden = true
     
    }
    
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let length = (textField.text?.characters.count)! - range.length + string.characters.count
        let fromLength = (textField == self.fromTextField) ? length : self.fromTextField.text?.characters.count
        let toLength = (textField == self.toTextField) ? length : self.toTextField.text?.characters.count
        let departureLength = (textField == self.departureDateTextField) ? length : self.departureDateTextField.text?.characters.count
        
        self.lookupButton.isEnabled = (fromLength! > 0 && toLength! > 0 && departureLength! > 0)
        
        return true;
    }
    
  
    
    
    fileprivate func handleAirportSearchWith(_ searchStr:String,isFromAirport:Bool) {
        if searchStr.characters.count == 0 {
            return
        }
        self.airportPresenter.fetchAirportsMatching(searchStr) { [weak self](airports, error) in
            switch error {
            case nil:
                self?.airports = airports
                if isFromAirport == true {
                    
                    self?.fromTableView.reloadData()
                }
                else {
                    self?.toTableView.reloadData()
                }
            default:
                print("No airport matches \(error?.localizedDescription)")
            }
        }
        
    }
    
}

// MARK: - Table view data source
extension FlightSearchViewController:UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.airports?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 1
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: UITableViewCellStyle.subtitle, reuseIdentifier: "AirportCell")
        guard let airports = self.airports else {
            return cell
        }
        if airports.count > indexPath.section {
            let airport = airports[indexPath.section]
            
            cell.textLabel?.text = airport
        }
        cell.selectionStyle = .none
        return cell
    }

}

extension FlightSearchViewController:PresentingViewProtocol {
    // Nothing to override. Just go with the default implementation
}
