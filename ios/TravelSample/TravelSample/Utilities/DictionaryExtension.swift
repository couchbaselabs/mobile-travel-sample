//
//  DictionaryExtension.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 6/12/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit

extension Dictionary {
    mutating func updateKey( fromKey:Key, toKey:Key){
        self[toKey] = self[fromKey]
        self.removeValue(forKey: fromKey)
    }
}

