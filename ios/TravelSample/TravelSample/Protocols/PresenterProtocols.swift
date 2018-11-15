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
        DispatchQueue.main.async {
            UIApplication.shared.isNetworkActivityIndicatorVisible = true

        }
    }

    public func dataFinishedLoading() {
        DispatchQueue.main.async {
        
             UIApplication.shared.isNetworkActivityIndicatorVisible = false
            
        }
    }
    
}

// implementation of INVPresentingViewProtocol only in cases where the presenting view is a UIViewController
extension PresentingViewProtocol where Self:UIViewController {
    
    func showAlertWithTitle(_ title:String?, message:String) {
        
        let alertController = UIAlertController(title: title ?? "", message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: NSLocalizedString("OK",comment:""), style: .default) { (result : UIAlertAction) -> Void in
            self.dismiss(animated: true, completion: {
                
            })
        }
        
        alertController.addAction(okAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
}
