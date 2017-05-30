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
    
    var lastError:Error?
    
    // fileprivate
    fileprivate let kDBName:String = "travel-sample"
    
    // This is the remote URL of the Sync Gateway (public Port)
    fileprivate let kRemoteSyncUrl = "http://localhost:4984"
    
    fileprivate var _db:Database?
    
    fileprivate var _pushPullRepl:Replication?
    
    
    fileprivate var _pullRepl:CBLReplication?
    
    fileprivate var _pushRepl:CBLReplication?
    
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

// MARK: Private
extension DatabaseManager {
    fileprivate func openOrCreateDatabase() {
        do {
            var options = DatabaseOptions()
            if let documentsPath = _applicationDocumentDirectory?.path {
                options.directory = documentsPath
            }
            _db = try Database(name: kDBName, options: options)
        }catch {
        
            lastError = error
        }
    }
}

// MARK: Public
extension DatabaseManager {
   
    func openOrCreateDatabaseForUser(_ user:String, password:String, handler:(_ error:Error?)->Void) {
        do {
            var options = DatabaseOptions()
            guard let defaultDBPath = _applicationSupportDirectory else {
                fatalError("Could not open Application Support Directory for app!")
                return
            }
            // Create a folder for the logged in user
            let userFolderPath = defaultDBPath.appendingPathComponent(user, isDirectory: true)
            options.directory = userFolderPath.absoluteString
            print("Database created at path \(userFolderPath)")
            _db = try Database(name: kDBName, options: options)
            handler(nil)
        }catch {
            
            lastError = error
            handler(lastError)
        }
    }
    
    func startPushAndPullReplication() {
        guard let remoteUrl = URL.init(string: kRemoteSyncUrl) else {
            // TODO: Set lastError = ...
            return
        }
        _pushPullRepl = _db?.replication(with: remoteUrl)
        
        //TODO: Filter by channels belonging to user
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



// MARK: Internal
extension DatabaseManager {
    
    
}

