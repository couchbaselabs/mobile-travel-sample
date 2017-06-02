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
    fileprivate let kDBName:String = "travel-sample-user"
    
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

//// MARK: Private
//extension DatabaseManager {
//    fileprivate func openOrCreateDatabase() {
//        do {
//            var options = DatabaseConfiguration()
//            if let documentsPath = _applicationDocumentDirectory?.path {
//                options.directory = documentsPath
//            }
//            _db = try Database(name: kDBName, options: options)
//        }catch {
//        
//            lastError = error
//        }
//    }
//}

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
            _db = try Database(name: kDBName, config: options)
            currentUserCredentials = (user,password)
            handler(nil)
        }catch {
            
            lastError = error
            handler(lastError)
        }
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
        
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(replicationProgress(notification:)),
                                               name: NSNotification.Name.ReplicatorChange,
                                               object: _pushPullRepl)
        
        _pushPullRepl?.start()

    }
    
  
    
    
    // Stops database sync/replication
    func stopAllReplications() {
        stopPullReplication()
        stopPushReplication()
    }
    
    // stop Push Replication
    func stopPushReplication() {
        _pushRepl?.stop()
    }
    
    // stop Pull Replication
    func stopPullReplication() {
        _pullRepl?.stop()
    }
    
    
}
//
//extension DatabaseManager:ReplicationDelegate {
//    public func replication(_ replication: CBLReplication, didChange status: CouchbaseLiteSwift.Replication.Status) {
//        print("\(#function) with status \(status)")
//    }
//    
//    /** Called when a replication stops, either because it finished or due to an error. */
//    public func replication(_ replication: CBLReplication, didStopWithError error: Error?) {
//        print(#function)
//    }
//}

extension DatabaseManager {
    
    fileprivate func configureCBManagerForSharedData() -> Bool {
        do {
            
            return true
        }
        catch {
            return false
            
        }
    }
    
    
    
    
    private func enableCrazyLevelLogging() {
   
        
    }
    
}



// MARK: Replication Observer
extension DatabaseManager {
    @objc func replicationProgress(notification: NSNotification) {
        let s = notification.userInfo![ReplicatorStatusUserInfoKey] as! Replicator.Status
        let e = notification.userInfo![ReplicatorErrorUserInfoKey] as? NSError
        
        print("[Todo] Replicator: \(s.progress.completed)/\(s.progress.total), error: \(e?.description ?? ""), activity = \(s.activity)")
      
    }
    
}

