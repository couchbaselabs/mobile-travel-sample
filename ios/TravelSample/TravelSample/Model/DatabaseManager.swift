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
    //var kRemoteSyncUrl = "ws://localhost:4984"
    fileprivate let kRemoteSyncUrl = "ws://localhost:4984"
    
   
    
    fileprivate var _db:Database?
    
    
    fileprivate var _pushPullRepl:Replicator?
    fileprivate var _pushPullReplListener:ListenerToken?
    
    
    fileprivate var _applicationDocumentDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).last
    
    fileprivate var _applicationSupportDirectory = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).last
    
    static let shared:DatabaseManager = {
        
        let instance = DatabaseManager()
        instance.initialize()
        return instance
    }()
    
    func initialize() {
        enableCrazyLevelConsoleLogging()
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
            let options = DatabaseConfiguration()
            guard let defaultDBPath = _applicationSupportDirectory else {
                fatalError("Could not open Application Support Directory for app!")
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
            let options = DatabaseConfiguration()
            guard let defaultDBPath = _applicationSupportDirectory else {
                fatalError("Could not open Application Support Directory for app!")
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
            if Database.exists(withName: kDBName, inDirectory: userFolderPath) == false {
                // Load prebuilt database from App Bundle and copy over to Applications support path
                if let prebuiltPath = Bundle.main.path(forResource: kDBName, ofType: "cblite2") {
                    try Database.copy(fromPath: prebuiltPath, toDatabase: "\(kDBName)", withConfig: options)
                    
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
            
            // Add change listener
            /***** Uncomment for optional testing ***
            _db?.addChangeListener({ [weak self](change) in
                guard let `self` = self else {
                    return
                }
                for docId in change.documentIDs   {
                    if let docString = docId as? String {
                        let doc = self._db?.document(withID:docString)
                       
                        print("doc.isDeleted = \(doc?.isDeleted)")
                    }
                }
                
            })
            *****/
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
        try _db?.createIndex(IndexBuilder.valueIndex(items:  ValueIndexItem.expression(Expression.property("type"))), withName: "typeIndex")
        try _db?.createIndex(IndexBuilder.valueIndex(items:ValueIndexItem.expression(Expression.property("name"))), withName: "nameIndex")
        try _db?.createIndex(IndexBuilder.valueIndex(items:ValueIndexItem.expression(Expression.property("airportname"))), withName: "airportIndex")
    
        // For Full text search on airports and hotels
        try _db?.createIndex(IndexBuilder.fullTextIndex(items: FullTextIndexItem.property("description")).ignoreAccents(false), withName: "descFTSIndex")
        
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
       
        let config = ReplicatorConfiguration.init(database: db, target: URLEndpoint.init(url:dbUrl))
    
        config.replicatorType = .pushAndPull
        config.continuous =  true
        config.authenticator =  BasicAuthenticator(username: user, password: password)

        // Uncomment if you want to use conflict resolver : Only 2.6
        // You can override the resolve function for testing
        //config.conflictResolver = self
        
        // This should match what is specified in the sync gateway config
        // Only pull documents from this user's channel
        let userChannel = "channel.\(user)"
        config.channels = [userChannel]
        
        _pushPullRepl = Replicator.init(config: config)
        
        _pushPullReplListener = _pushPullRepl?.addChangeListener({ [weak self] (change) in
            let s = change.status
            print("PushPull Replicator: \(s.progress.completed)/\(s.progress.total), error: \(String(describing: s.error)), activity = \(s.activity)")
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
            _pushPullRepl?.removeChangeListener(withToken:  pushPullReplListener)
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
    // Only in 2.5 : File log options
    fileprivate func enableCrazyLevelConsoleLogging() {
        Database.log.console.level = .verbose
        
    }
    
    fileprivate func enableCrazyLevelFileLogging() {
        guard let logPath = _applicationSupportDirectory else {
            fatalError("Could not open Support folder for app!")
        }
        
        let logPathUrl = logPath.appendingPathComponent("cbllog")
        var logConfig =  LogFileConfiguration.init(directory:logPathUrl.path)
        logConfig.usePlainText = true
        Database.log.file.config = logConfig
        Database.log.file.level = .verbose
        
        print("log file path is \(Database.log.file.config?.directory)")
    }
    
    fileprivate func enableCrazyLevelCustomLogging() {
        Database.log.custom = LogTestLogger()
    }
    
    fileprivate class LogTestLogger: Logger {
        
        var lines: [String] = []
        
        var level: LogLevel = .verbose
        
        func reset() {
            lines.removeAll()
        }
        
        func log(level: LogLevel, domain: LogDomain, message: String) {
            lines.append(message)
            print("log is \(message)")
        }
    }
 
}

// MARK: Custom conflict resolver
// Only starting 2.6
extension DatabaseManager:ConflictResolverProtocol {

    public func resolve(conflict: CouchbaseLiteSwift.Conflict) -> CouchbaseLiteSwift.Document? {
        print(#function)
        // To implement custom resolver
        let local = conflict.localDocument
        let remote = conflict.remoteDocument
        if let _ = local?.toDictionary()["type"] as? String {
            // All documents except the user document has a type.
            // In theory, none of these documents would never be updated
            // If you want to, then you can change the airline documents on server and change the SG config file filter
            // function to sync to clients
            
            // Just use the default resolver
            
            return
                ConflictResolver.default.resolve(conflict:conflict)
            
        }
        else {
            // This is the user document and is the only document that can be updated by clients. It has no type
            // Always let local win unless deleted
             if local != nil {
                return local
            }
            
            return remote
        }
        
    }
}
/*** Deprecated in DB023
extension DatabaseManager:ConflictResolver {
    public func resolve(conflict: CouchbaseLiteSwift.Conflict) -> CouchbaseLiteSwift.Document? {
        print(#function)
        // To implement custom resolver
        let mine = conflict.mine
        let theirs = conflict.theirs
        if let _ = mine.toDictionary()["type"] as? String {
            // All documents except the user document has a type.
            // In theory, none of these documents would never be updated
            // If you want to, then you can change the airline documents on server and change the SG config file filter
            // function to sync to clients
            
            // Just use the default resolver
           
            return DatabaseConfiguration.init().conflictResolver.resolve(conflict: conflict)
            
        }
        else {
            // This is the user document and is the only document that can be updated by clients. It has no type
            
            if theirs.isDeleted {
                return theirs
            }
            else if mine.isDeleted {
                return mine
            }
            print("Implemented custom resolver for user type")
            return mine
        }
    
    }
}

*****/

