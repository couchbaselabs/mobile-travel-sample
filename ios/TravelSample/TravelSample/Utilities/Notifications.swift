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
            case user = "user"
        
        }
    }
    enum loginInFailure :String {
        case name = "LoginFailure"
        enum userInfoKeys : String {
            case user = "user"
            
        }
    }
    
    enum logout:String {
        case name = "LogOut"
    }
    
    enum replicationInProgress:String {
        case name = "InProgress"
    }
    enum replicationStopped:String {
        case name = "Stopped"
    }
    enum replicationFailed:String {
        case name = "Failed"
    }
}

extension Notification {
    
    public static func notificationForLoginSuccess(_ userEmail:String)-> Notification {
        let userInfo = [AppNotifications.loginInSuccess.userInfoKeys.user.rawValue:userEmail] as [String : Any]
        return Notification(name: Notification.Name(rawValue: AppNotifications.loginInSuccess.name.rawValue), object: nil, userInfo: userInfo)
        
    }
    
    public static func notificationForLoginFailure(_ userEmail:String)-> Notification {
        let userInfo = [AppNotifications.loginInFailure.userInfoKeys.user.rawValue:userEmail ]
        return Notification(name: Notification.Name(rawValue: AppNotifications.loginInFailure.name.rawValue), object: nil, userInfo: userInfo)
        
    }
    
    public static func notificationForLogOut()-> Notification {
        return Notification(name: Notification.Name(rawValue: AppNotifications.logout.name.rawValue), object: nil, userInfo: nil)
        
    }
    
    public static func notificationForReplicationInProgress()-> Notification {
        return Notification(name: Notification.Name(rawValue: AppNotifications.replicationInProgress.name.rawValue), object: nil, userInfo: nil)
        
    }
    
    public static func notificationForReplicationStopped()-> Notification {
        return Notification(name: Notification.Name(rawValue: AppNotifications.replicationStopped.name.rawValue), object: nil, userInfo: nil)
        
    }
    
    public static func notificationForReplicationFailed()-> Notification {
        return Notification(name: Notification.Name(rawValue: AppNotifications.replicationFailed.name.rawValue), object: nil, userInfo: nil)
        
    }
    
    
}
