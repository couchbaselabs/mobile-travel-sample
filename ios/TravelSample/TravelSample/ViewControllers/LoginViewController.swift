//
//  LoginViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/30/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import UIKit

class LoginViewController:UIViewController {
    
    @IBOutlet weak var loginScrollView: UIScrollView!
    @IBOutlet weak var contentView:UIView!
    @IBOutlet weak var passwordEntryView:UIView!
    @IBOutlet weak var passwordTextEntry:UITextField!
    @IBOutlet weak var userEntryView:UIView!
    @IBOutlet weak var userTextEntry:UITextField!
    
    @IBOutlet weak var loginButton:UIButton!
    @IBOutlet weak var bgImageView:UIImageView!
    
  
    // MARK: View Related
    override public func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    public override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.registerKBNotifications()
    }
    
    public override func viewWillDisappear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.registerKBNotifications()
    }
    
    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    public func touchesShouldCancelInContentView(_ view: UIView) -> Bool {
        return true
    }
    
    public override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.passwordTextEntry.text = nil
    }
    
    
    
}


// MARK: UITextFieldDelegate
extension LoginViewController:UITextFieldDelegate {
    public func textFieldShouldReturn(_ textField: UITextField) -> Bool  {
        if textField == self.passwordTextEntry {
            textField.resignFirstResponder()
        }
        else if textField == self.userTextEntry {
            self.passwordTextEntry.becomeFirstResponder()
        }
        return true;
    }
    
    public func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let length = (textField.text?.characters.count)! - range.length + string.characters.count
        let userLength = (textField == self.userTextEntry) ? length : self.userTextEntry.text?.characters.count
        let passwordLength = (textField == self.passwordTextEntry) ? length : self.passwordTextEntry.text?.characters.count
        
         self.loginButton.isEnabled = (userLength! > 0 && passwordLength! > 0)
        
        return true;
    }
}


// MARK : IBOutlet handlers

extension LoginViewController {
   
    @IBAction func onLoginTapped(_ sender: UIButton) {
        if let userName = self.userTextEntry.text, let password = self.passwordTextEntry.text {
            let cbMgr = DatabaseManager.shared
            cbMgr.openOrCreateDatabaseForUser(userName, password: password, handler: { (error) in
                switch error {
                case nil:
                    NotificationCenter.default.post(Notification.notificationForLoginSuccess(userName))
                    
                default:
                    NotificationCenter.default.post(Notification.notificationForLoginFailure(userName))                                        
                    
                }
            })
        }
        
    }
    
    @IBAction func onGuestLoginTapped(_ sender: UIButton) {
            NotificationCenter.default.post(Notification.notificationForGuestLoginSuccess())
                    
    }
    
}


// MARK: KB extensions
extension LoginViewController {
    
    func registerKBNotifications() {
        let selectorShow = "kbWillShow:"
        let selectorHide = "kbWillHide:"
        
        NotificationCenter.default.addObserver(self, selector: #selector(LoginViewController.kbWillShow(notification:)), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(LoginViewController.kbWillHide(notification:)), name: .UIKeyboardWillHide, object: nil)
        
        
    }
    
    func deregisterKBNotifications() {
        
        NotificationCenter.default.removeObserver(self, name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: .UIKeyboardWillHide, object: nil)
        
        
    }
    
    func kbWillShow(notification:Notification)-> Void{
        var rect:CGRect = ((notification.userInfo?[UIKeyboardFrameBeginUserInfoKey]as? NSValue)?.cgRectValue)!
        rect = self.view.convert(rect, from: nil)
        
        let insets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: rect.size.height, right: 0.0)
        self.loginScrollView.contentInset = insets
        self.loginScrollView.scrollIndicatorInsets = insets
        
        var viewRect:CGRect = self.view.frame
        viewRect.size.height = viewRect.size.height - rect.size.height
        if viewRect.contains(loginButton.frame.origin) {
            self.loginScrollView.scrollRectToVisible(self.loginButton.frame, animated: true)
        }
    }
    
    func kbWillHide(notification:Notification)-> Void {
        let insets = UIEdgeInsetsMake(0, 0, 0, 0)
        self.loginScrollView.contentInset = insets
        self.loginScrollView.scrollIndicatorInsets = insets
    }
    


}
