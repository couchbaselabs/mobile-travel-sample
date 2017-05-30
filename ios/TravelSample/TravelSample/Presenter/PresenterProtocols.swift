//
//  PresenterProtocols.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/30/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import Foundation
import UIKit

// MVP pattern
// The View COntroller or view that handles presentation of data must implement this protocol
public protocol PresentingViewProtocol: class {
    func dataStartedLoading()
    func dataFinishedLoading()
    func showAlertWithTitle(_ title:String?, message:String)
}

// All Presenters must implement this protocol
public protocol PresenterProtocol: class {
    func attachPresentingView(_ view:PresentingViewProtocol)
    func detachPresentingView(_ view:PresentingViewProtocol)
}

// default implementation of PresentingViewProtocol
extension PresentingViewProtocol {
    public func dataStartedLoading() {
        //noop
    }
    public func dataFinishedLoading() {
        //noop
    }
    
}

// implementation of INVPresentingViewProtocol only in cases where the presenting view is a UIViewController
extension PresentingViewProtocol where Self:UIViewController {
    
    func showAlertWithTitle(_ title:String?, message:String) {
        
        let alertController = UIAlertController(title: title ?? "", message: message, preferredStyle: UIAlertControllerStyle.alert)
        let okAction = UIAlertAction(title: NSLocalizedString("OK",comment:""), style: UIAlertActionStyle.default) { (result : UIAlertAction) -> Void in
            self.dismiss(animated: true, completion: {
                
            })
        }
        
        alertController.addAction(okAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
}
