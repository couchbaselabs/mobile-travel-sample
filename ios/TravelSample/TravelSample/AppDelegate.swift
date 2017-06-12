//
//  AppDelegate.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/30/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit
//import CouchbaseLiteSwift

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    fileprivate var loginViewController:LoginViewController?
    fileprivate var flightBookingsViewController:UINavigationController?
    

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        loadLoginViewController()
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

}

// MARK : Loading Root Views
extension AppDelegate {
    func loadLoginViewController() {
        if let loginVC = loginViewController {
            window?.rootViewController = loginVC
        }
        else {
            let storyboard = UIStoryboard.getStoryboard(.Main)
            loginViewController = storyboard.instantiateViewController(withIdentifier: "LoginViewController") as? LoginViewController
            
            window!.rootViewController = loginViewController
            
        }
        
        self.registerNotificationObservers()
    }
    
    
    func loadFlightBookingViewController() {
        
        if let flightVC = flightBookingsViewController {
            window?.rootViewController = flightVC
        }
        else {
            let storyboard = UIStoryboard.getStoryboard(.Main)
            if let navController = storyboard.instantiateViewController(withIdentifier: "FlightBookingNVC") as? UINavigationController{
                flightBookingsViewController = navController
                window?.rootViewController = navController
            }
        }
        
    }
    
    
    func logout() {
        self.deregisterNotificationObservers()
        loadLoginViewController()
    }
    
    
    
}

// MARK: Observers
extension AppDelegate {
    
    func registerNotificationObservers() {
        NotificationCenter.default.addObserver(forName: NSNotification.Name(rawValue: AppNotifications.loginInSuccess.name.rawValue), object: nil, queue: nil) { [unowned self] (notification) in
            
            let cbMgr = DatabaseManager.shared
            cbMgr.startPushAndPullReplicationForCurrentUser()
                
            if let userInfo = (notification as NSNotification).userInfo as? Dictionary<String,Any> {
                if let _ = userInfo[AppNotifications.loginInSuccess.userInfoKeys.user.rawValue]{
                    self.loadFlightBookingViewController()
                    
                }
            }
            
        }
        
        NotificationCenter.default.addObserver(forName: NSNotification.Name(rawValue: AppNotifications.loginInFailure.name.rawValue), object: nil, queue: nil) {[unowned self] (notification) in
            if let userInfo = (notification as NSNotification).userInfo as? Dictionary<String,String> {
                if let _ = userInfo[AppNotifications.loginInSuccess.userInfoKeys.user.rawValue]{
                    self.logout()
                }
                
            }
        }
        
        NotificationCenter.default.addObserver(forName: NSNotification.Name(rawValue: AppNotifications.logout.name.rawValue), object: nil, queue: nil) { [unowned self] (notification) in
            self.logout()
        }
    }
    
    
    func deregisterNotificationObservers() {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: AppNotifications.loginInSuccess.name.rawValue), object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: AppNotifications.loginInFailure.name.rawValue), object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: AppNotifications.logout.name.rawValue), object: nil)
        
        
    }
    
    
}

