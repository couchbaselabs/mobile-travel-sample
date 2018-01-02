//
//  QueryConsts.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 8/7/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import CouchbaseLiteSwift

struct TravelSampleWebService {
    static let serverBackendUrl:URL? = URL.init(string: "http://localhost:8080/api/")

}
struct _Property {
    // Query: Property Expressions
    static let DOCID        = Meta.id
    
    static let TYPE         = Expression.property("type")
    static let USERNAME     = Expression.property("username")
    static let FLIGHTS      = Expression.property("flights")
    static let DESCRIPTION  = Expression.property("description")
    static let NAME         = Expression.property("name")
    static let COUNTRY      = Expression.property("country")
    static let CITY         = Expression.property("city")
    static let STATE        = Expression.property("state")
    static let ADDRESS      = Expression.property("address")
    static let FAA          = Expression.property("faa")
    static let ICAO         = Expression.property("icao")
    static let AIRPORTNAME  = Expression.property("airportname")
}

struct _SelectColumn {    
    // Query: Select Results
    static let NAMERESULT         = SelectResult.expression(_Property.NAME)
    static let AIRPORTNAMERESULT  = SelectResult.expression(_Property.AIRPORTNAME)
    static let FLIGHTSRESULT      = SelectResult.expression(_Property.FLIGHTS)
    static let DOCIDRESULT        = SelectResult.expression(_Property.DOCID)
    static let COUNTRESULT        = SelectResult.expression(Function.count(1))
    static let ALLRESULT          = SelectResult.all()
}

