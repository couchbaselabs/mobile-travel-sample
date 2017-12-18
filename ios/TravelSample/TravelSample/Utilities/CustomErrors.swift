//
//  CustomErrors.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/12/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import Foundation
enum TravelSampleError:Error {
    case DatabaseNotInitialized
    case UserNotFound
    case RemoteDatabaseNotReachable
    case DataParseError
    case UserCredentialsNotProvided
    case DocumentFetchException
    case ImageProcessingFailure
    case ImageTooBig
}
