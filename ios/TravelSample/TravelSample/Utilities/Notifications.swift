//
//  Notifications.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/30/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import Foundation

import Foundation
enum AppNotifications {
    enum loginInSuccess : String{
        case name = "LoginSuccess"
        
        
        enum userInfoKeys : String{
            case userEmail = "userEmail"
            
        }
    }
    enum loginInFailure :String {
        case name = "LoginFailure"
        
        enum userInfoKeys : String {
            case userEmail = "userEmail"
            
        }
    }
    
    enum logout:String {
        case name = "LogOut"
        
    }
    
}

extension Notification {
    
    public static func notificationForLoginSuccess(_ userEmail:String)-> Notification {
        let userInfo = [AppNotifications.loginInSuccess.userInfoKeys.userEmail.rawValue:userEmail] as [String : Any]
        return Notification(name: Notification.Name(rawValue: AppNotifications.loginInSuccess.name.rawValue), object: nil, userInfo: userInfo)
        
    }
    
    public static func notificationForLoginFailure(_ userEmail:String)-> Notification {
        let userInfo = [AppNotifications.loginInFailure.userInfoKeys.userEmail.rawValue:userEmail ]
        return Notification(name: Notification.Name(rawValue: AppNotifications.loginInFailure.name.rawValue), object: nil, userInfo: userInfo)
        
    }
    
    public static func notificationForLogOut()-> Notification {
        return Notification(name: Notification.Name(rawValue: AppNotifications.logout.name.rawValue), object: nil, userInfo: nil)
        
    }
    
    
    
}
