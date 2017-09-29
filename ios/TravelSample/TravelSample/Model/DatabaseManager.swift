//
//  DatabaseManager.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 5/30/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import CouchbaseLiteSwift



class DatabaseManager {
    
    // public
    var db:Database? {
        get {
            return _db
        }
    }
    
    
    // For demo purposes only. In prod apps, credentials must be stored in keychain
    public fileprivate(set) var currentUserCredentials:(user:String,password:String)?
    
    var lastError:Error?
    

    // fileprivate
    fileprivate let kDBName:String = "travel-sample"
    fileprivate let kGuestDBName:String = "guest"
    
    // This is the remote URL of the Sync Gateway (public Port)
    fileprivate let kRemoteSyncUrl = "blip://localhost:4984"
    
    
    fileprivate var _db:Database?
    
    
    fileprivate var _pushPullRepl:Replicator?
    fileprivate var _pushPullReplListener:NSObjectProtocol?
    
    
    fileprivate var _applicationDocumentDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).last
    
    fileprivate var _applicationSupportDirectory = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).last
    
    static let shared:DatabaseManager = {
        
        let instance = DatabaseManager()
        instance.initialize()
        return instance
    }()
    
    func initialize() {
        enableCrazyLevelLogging()
    }
    // Don't allow instantiation . Enforce singleton
    private init() {
        
    }
    
    deinit {
        // Stop observing changes to the database that affect the query
        do {
            try self._db?.close()
        }
        catch  {
            
        }
    }
    
}

// MARK: Public
extension DatabaseManager {
   
    
    func openOrCreateDatabaseForGuest( handler:(_ error:Error?)->Void) {
        do {
            var options = DatabaseConfiguration()
            guard let defaultDBPath = _applicationSupportDirectory else {
                fatalError("Could not open Application Support Directory for app!")
                return
            }
            // Create a folder for Guest Account if one does not exist
            let guestFolderUrl = defaultDBPath.appendingPathComponent("guest", isDirectory: true)
            let guestFolderPath = guestFolderUrl.path
            let fileManager = FileManager.default
            if !fileManager.fileExists(atPath: guestFolderPath) {
                try fileManager.createDirectory(atPath: guestFolderPath,
                                                withIntermediateDirectories: true,
                                                attributes: nil)
                
            }
            
            options.directory = guestFolderPath
            // Gets handle to existing DB at specified path
            _db = try Database(name: kGuestDBName, config: options)

            handler(nil)
        }catch {
            
            lastError = error
            handler(lastError)
        }
    }

    
    func openOrCreateDatabaseForUser(_ user:String, password:String, handler:(_ error:Error?)->Void) {
        do {
            var options = DatabaseConfiguration()
            guard let defaultDBPath = _applicationSupportDirectory else {
                fatalError("Could not open Application Support Directory for app!")
                return
            }
            // Create a folder for the logged in user
            let userFolderUrl = defaultDBPath.appendingPathComponent(user, isDirectory: true)
            let userFolderPath = userFolderUrl.path
            let fileManager = FileManager.default
            if !fileManager.fileExists(atPath: userFolderPath) {
                try fileManager.createDirectory(atPath: userFolderPath,
                                                    withIntermediateDirectories: true,
                                                    attributes: nil)
                
            }
            
            options.directory = userFolderPath
            print("WIll open/create DB  at path \(userFolderPath)")
            if Database.exists(kDBName, inDirectory: userFolderPath) == false {
                // Load prebuilt database from App Bundle and copy over to Applications support path
                if let prebuiltPath = Bundle.main.path(forResource: kDBName, ofType: "cblite2") {
                     try Database.copy(fromPath: prebuiltPath, toDatabase: "\(kDBName)", config: options)
                    
                }
                // Get handle to DB  specified path
                _db = try Database(name: kDBName, config: options)
                 try createDatabaseIndexes()
                
            }
            else
            {
                // Gets handle to existing DB at specified path
                 _db = try Database(name: kDBName, config: options)
                
            }
            currentUserCredentials = (user,password)
            handler(nil)
        }catch {
            
            lastError = error
            handler(lastError)
        }
    }
    
    
    func closeDatabaseForCurrentUser() -> Bool {
        do {
            print(#function)
            // Get handle to DB  specified path
            if let db = self.db {
                switch db.name {
                case kDBName:
                        stopAllReplicationForCurrentUser()
                        try _db?.close()
                        _db = nil
                case kGuestDBName:
                    try _db?.close()
                    _db = nil
                default:
                    return false
                }
              
            }
            
          
            return true
            
        }
        catch {
            return false
        }
    }
    
    
    func createDatabaseIndexes() throws{
        // For searches on type property
        try _db?.createIndex(["type"])
        try _db?.createIndex(["name"])
        try _db?.createIndex(["airportname"])

    
        // For Full text search on airports and hotels
        
        try _db?.createIndex(["description"], options: IndexOptions.fullTextIndex(language: nil, ignoreDiacritics: false))
        

    }

    
    func startPushAndPullReplicationForCurrentUser() {
        print(#function)
        guard let remoteUrl = URL.init(string: kRemoteSyncUrl) else {
            lastError = TravelSampleError.RemoteDatabaseNotReachable
            
            return
        }
        
        guard let user = self.currentUserCredentials?.user,let password = self.currentUserCredentials?.password  else {
            lastError = TravelSampleError.UserCredentialsNotProvided
            return
        }
        
        guard let db = db else {
            lastError = TravelSampleError.RemoteDatabaseNotReachable
            return
        }

        if _pushPullRepl != nil {
            // Replication is already started
            return
        }
        
        let dbUrl = remoteUrl.appendingPathComponent(kDBName)
       
        var config = ReplicatorConfiguration(database: db, targetURL: dbUrl)
        
        //TODO: Set push filter to avoid pushing up the static docs related to airline, airport, route, hotel
        config.replicatorType = .pushAndPull
        config.continuous = true
        config.authenticator = BasicAuthenticator(username: user, password: password)
        
        // This should match what is specified in the sync gateway config
        // Only pull documents from this user's channel
        let userChannel = "channel.\(user)"
        config.channels = [userChannel]
        
        
        _pushPullRepl = Replicator.init(config: config)
        
        _pushPullReplListener = _pushPullRepl?.addChangeListener({ [weak self] (change) in
            let s = change.status
            print("PushPull Replicator: \(s.progress.completed)/\(s.progress.total), error: \(String(describing: s.error)), activity = \(s.activity)")
            // Workarond for BUG :https://github.com/couchbase/couchbase-lite-ios/issues/1816.
            if s.progress.completed == s.progress.total {
                self?.postNotificationOnReplicationState(.idle)
            }
            else {
                self?.postNotificationOnReplicationState(s.activity)
            }
        })
        
        
        _pushPullRepl?.start()

    }
    
 
   
    func stopAllReplicationForCurrentUser() {
        _pushPullRepl?.stop()
        if let pushPullReplListener = _pushPullReplListener{
            print(#function)
            _pushPullRepl?.removeChangeListener(pushPullReplListener)
            _pushPullRepl = nil
            _pushPullReplListener = nil
        }
      
    }
   

    fileprivate func postNotificationOnReplicationState(_ status:Replicator.ActivityLevel) {
        switch status {
        case .offline:
            NotificationCenter.default.post(Notification.notificationForReplicationOffline())
        case .connecting:
            NotificationCenter.default.post(Notification.notificationForReplicationConnecting())            
        case .stopped:
            NotificationCenter.default.post(Notification.notificationForReplicationStopped())
        case .idle:
            NotificationCenter.default.post(Notification.notificationForReplicationIdle())
        case .busy:
            NotificationCenter.default.post(Notification.notificationForReplicationInProgress())
            
            
      
            
        }
    }
    
    
}

// MARK: Utils
extension DatabaseManager {
    
    fileprivate func enableCrazyLevelLogging() {
   
        Database.setLogLevel(.debug, domain: .database)
    }
    
}



