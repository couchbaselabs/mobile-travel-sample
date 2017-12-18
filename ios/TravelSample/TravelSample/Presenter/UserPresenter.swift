//
//  UserPresenter.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 12/15/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
import CouchbaseLiteSwift
import UIKit


class UserPresenter:UserPresenterProtocol {
    
    fileprivate var dbMgr:DatabaseManager = DatabaseManager.shared
    
    weak var associatedView: PresentingViewProtocol?
    
    var userDocId:String?
    
}

// MARK: Local DB Queries via CBM 2.0. Both Regular queries and FTS are used
extension UserPresenter {
    func fetchLoggedInUserProfile( handler: @escaping (_ user: User?, _ error: Error?) -> Void) {
        guard let db = dbMgr.db else {
            handler(nil,TravelSampleError.DatabaseNotInitialized)
            return
        }
        
        guard let user = dbMgr.currentUserCredentials?.user else {
            handler(nil,TravelSampleError.UserNotFound)
            return
        }
        
        // Every user MUST be associated with a single user document that is created when the
        // user signs up. If a user does not have this user document, then we assume that
        // the user is not a valid user
        let userQuery = Query
            .select(_SelectColumn.DOCIDRESULT,
                    _SelectColumn.ALLRESULT)
            .from(DataSource.database(db))
            .where(_Property.USERNAME.equalTo(user))
        do {
            // V1.0. There should be only one document for a user.
                
            for (_, row) in try userQuery.run().enumerated() {
                // There isnt a convenience API to get to documentId from Result. So use "id" key
                // Tracking - https://github.com/couchbaselabs/couchbase-lite-apiv2/issues/123
                    userDocId = row.string(forKey: "id")
                    if var userVal = row.dictionary(forKey: "travel-sample")?.toDictionary() {
                        if let profileImage = userVal["imageprofile"] as? Blob {
                            userVal["imageProfile"] = profileImage.content
                            
                        }
                        handler(userVal,nil)
                        break
                    }
            }
                
                
        }catch {
            handler (nil,TravelSampleError.UserNotFound)
        }
    }
    
    func updateProfileImage(_ image: UIImage, handler: @escaping (Error?) -> Void) {
        guard let db = dbMgr.db else {
            handler(TravelSampleError.DatabaseNotInitialized)
            return
        }
        
        guard let user = dbMgr.currentUserCredentials?.user , let userDocId = userDocId else {
            handler(TravelSampleError.UserNotFound)
            return
        }
        
        if var userDocument = db.getDocument(userDocId)?.toMutable() {
            
            let maxUploadImageSize = 20000000 // 20MB
            
            guard let imageData = UIImageJPEGRepresentation(image, 0.75) else {
                handler(TravelSampleError.ImageProcessingFailure)
                return
            }
            
            if imageData.count > maxUploadImageSize {
                handler(TravelSampleError.ImageTooBig)
                return
            }
            
            let blob = Blob.init(contentType: "image/jpeg", data: imageData)
            userDocument.setBlob(blob, forKey: "imageprofile")
            
            do {
                // Set this flag to true to simulate a conflict by delaying the application of the change
                // To simulate conflict, do the following
                // 1. Set flag to true
                // 2. Run app in two devices simultaneously
                // 3. Update the profile image from both devices. Tap Save
                // 4. There would be a race condition for when the document would get updated
                // 5. This would result in a conflict triggering the resolver
                let testingConflicts = false
                
                if testingConflicts {
                    DispatchQueue.main.asyncAfter(deadline: .now() + .milliseconds(10000)) {
                        try? db.save(userDocument)
                        handler(nil)
                    }
                    
                }
                else {
                    try db.save(userDocument)
                    handler(nil)
                }
                
            }
            catch{
                handler(error)
                return
            }
            
        }
        else {
            handler(nil)
            return
        }
    }
}


// MARK: PresenterProtocol
extension UserPresenter:PresenterProtocol {
    func detachPresentingView(_ view: PresentingViewProtocol) {
            self.associatedView = nil
    }
    
    func attachPresentingView(_ view:PresentingViewProtocol) {
        self.associatedView = view
        
    }
   
}

