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
    
    // This is the remote URL of the Sync Gateway (public Port)
    fileprivate let kRemoteSyncUrl = "blip://demo:password@localhost:4984"
    
    
    fileprivate var _db:Database?
    

    
    fileprivate var _pushPullRepl:Replicator?
    
    
    fileprivate var _pullRepl:Replicator?
    
    fileprivate var _pushRepl:Replicator?
    
    fileprivate var _applicationDocumentDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).last
    
    fileprivate var _applicationSupportDirectory = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).last
    
    static let shared:DatabaseManager = {
        let instance = DatabaseManager()
        instance.initialize()
        return instance
    }()
    
    func initialize() {
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
            print("Database created at path \(userFolderPath)")
            if Database.exists(kDBName, inDirectory: userFolderPath) == false {
                // Load prebuilt database from App Bundle and copy over to Applications support path
                if let prebuiltPath = Bundle.main.path(forResource: kDBName, ofType: "cblite2") {
                    let destinationDBPath = userFolderPath.appending("/\(kDBName).cblite2")
                    try fileManager.copyItem(atPath: prebuiltPath, toPath: destinationDBPath)
                    
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
            // Get handle to DB  specified path
            try _db?.close()
            return true
            
        }
        catch {
            return false
        }
    }
    
    func createDatabaseIndexes() throws{
        // For searches on type property
        try _db?.createIndex(["type"])
        
        // For Full text search on airports and hotels
        try _db?.createIndex(["airportname"], options: IndexOptions.fullTextIndex(language: nil, ignoreDiacritics: true))
    }

    
    func startPushAndPullReplicationForCurrentUser() {
        guard let remoteUrl = URL.init(string: kRemoteSyncUrl) else {
            // TODO: Set lastError = ...
            return
        }
        let dbUrl = remoteUrl.appendingPathComponent(kDBName)
        print(dbUrl)
        var config = ReplicatorConfiguration()
        config.database = db
        config.target = ReplicatorTarget.url(dbUrl)
        
        config.replicatorType = .pushAndPull
        config.continuous = true
        _pushPullRepl = Replicator.init(config: config)
        
        //TODO: Set push filter to avoid pushing up the static docs related to airline, airport, route, hotel
        
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(replicationProgress(notification:)),
                                               name: NSNotification.Name.ReplicatorChange,
                                               object: _pushPullRepl)
        
        _pushPullRepl?.start()

    }
    
 
   
    
    // start Push Replication
    func startPushReplication() {
        guard let remoteUrl = URL.init(string: kRemoteSyncUrl) else {
            lastError = TravelSampleError.RemoteDatabaseNotReachable
            
            return
        }
        let dbUrl = remoteUrl.appendingPathComponent(kDBName)
        print(dbUrl)
        var config = ReplicatorConfiguration()
        config.database = db
        config.target = ReplicatorTarget.url(dbUrl)
        
        config.replicatorType = .push
        config.continuous = true
        _pushRepl = Replicator.init(config: config)
        
        //TODO: Set push filter to avoid pushing up the static docs related to airline, airport, route, hotel
        
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(pushReplicationProgress(notification:)),
                                               name: NSNotification.Name.ReplicatorChange,
                                               object: _pushRepl)
        
        _pushRepl?.start()
    }
    
    // start Pull Replication
    func startPullReplication() {
        guard let remoteUrl = URL.init(string: kRemoteSyncUrl) else {
            lastError = TravelSampleError.RemoteDatabaseNotReachable
            
            return
        }
        let dbUrl = remoteUrl.appendingPathComponent(kDBName)
        print(dbUrl)
        var config = ReplicatorConfiguration()
        config.database = db
        config.target = ReplicatorTarget.url(dbUrl)
        
        config.replicatorType = .pull
        config.continuous = true
        _pullRepl = Replicator.init(config: config)
        
        //TODO: Set pull filter to filter on channels belonging to user
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(pullReplicationProgress(notification:)),
                                               name: NSNotification.Name.ReplicatorChange,
                                               object: _pullRepl)
        
        _pullRepl?.start()
    }
    
    func stopAllReplicationForCurrentUser() {
        _pushPullRepl?.stop()
    }
    
    func stopPullReplicationForCurrentUser() {
        _pullRepl?.stop()
    }

    func stopPushReplicationForCurrentUser() {
        _pushRepl?.stop()
    }

    
}

// MARK: Utils
extension DatabaseManager {
    
    private func enableCrazyLevelLogging() {
   
    
    }
    
}



// MARK: Replication Observer
extension DatabaseManager {
    @objc func replicationProgress(notification: NSNotification) {
        let s = notification.userInfo![ReplicatorStatusUserInfoKey] as! Replicator.Status
        let e = notification.userInfo![ReplicatorErrorUserInfoKey] as? NSError
        
        print("PushPull Replicator: \(s.progress.completed)/\(s.progress.total), error: \(e?.description ?? ""), activity = \(s.activity)")
        postNotificationOnReplicationState(s.activity)
    }
    
    @objc func pullReplicationProgress(notification: NSNotification) {
        let s = notification.userInfo![ReplicatorStatusUserInfoKey] as! Replicator.Status
        let e = notification.userInfo![ReplicatorErrorUserInfoKey] as? NSError
        
        print("Pull Replicator: \(s.progress.completed)/\(s.progress.total), error: \(e?.description ?? ""), activity = \(s.activity)")
        postNotificationOnReplicationState(s.activity)
        
    }

    @objc func pushReplicationProgress(notification: NSNotification) {
        let s = notification.userInfo![ReplicatorStatusUserInfoKey] as! Replicator.Status
        let e = notification.userInfo![ReplicatorErrorUserInfoKey] as? NSError
        
        print("Push Replicator: \(s.progress.completed)/\(s.progress.total), error: \(e?.description ?? ""), activity = \(s.activity)")
        postNotificationOnReplicationState(s.activity)
        
    }

    private func postNotificationOnReplicationState(_ status:Replicator.ActivityLevel) {
        switch status {
        case .stopped:
            NotificationCenter.default.post(Notification.notificationForReplicationStopped())
        case .idle:
            NotificationCenter.default.post(Notification.notificationForReplicationStopped())
        case .busy:
            NotificationCenter.default.post(Notification.notificationForReplicationInProgress())
            
            
        }
    }
    
}

