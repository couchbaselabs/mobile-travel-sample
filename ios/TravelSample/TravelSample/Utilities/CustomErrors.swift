//
//  CustomErrors.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/12/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
enum TravelSampleError: LocalizedError , CustomStringConvertible{
    case DatabaseNotInitialized
    case UserNotFound
    case RemoteDatabaseNotReachable
    case DataParseError
    case UserCredentialsNotProvided
    case DocumentFetchException
    case ImageProcessingFailure
    case ImageTooBig
   
}

extension TravelSampleError {
    /// Retrieve the localized description for this error.
    var description: String {
        switch self {
        case .DatabaseNotInitialized :
            return NSLocalizedString("Couchbase Lite Database not initialized", comment: "")
        case .UserNotFound:
            return NSLocalizedString("User does not exist. Create user via the web app and try again.", comment: "")
        case .RemoteDatabaseNotReachable:
            return NSLocalizedString("Could not access remote sync gateway URL", comment: "")
        case .DataParseError:
            return NSLocalizedString("Could not parse response. Appears to be in invalid format ", comment: "")
        case .UserCredentialsNotProvided:
            return NSLocalizedString("Please provide right credentials to sync with Sync Gateway ", comment: "")
        case .DocumentFetchException:
            return NSLocalizedString("Could not load document from database", comment: "")
        case .ImageProcessingFailure:
            return NSLocalizedString("Failed to process image ", comment: "")
        case .ImageTooBig:
            return NSLocalizedString("Image size too big!", comment: "")
        }
   
    }
 
}
extension LocalizedError where Self: CustomStringConvertible {
    var errorDescription: String? {
        return description
    }
    
    
}
