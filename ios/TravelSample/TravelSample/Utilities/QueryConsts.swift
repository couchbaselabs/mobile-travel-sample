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
    
    static let TYPE         = PropertyExpression.property("type")
    static let USERNAME     = PropertyExpression.property("username")
    static let FLIGHTS      = PropertyExpression.property("flights")
    static let DESCRIPTION  = PropertyExpression.property("description")
    static let NAME         = PropertyExpression.property("name")
    static let COUNTRY      = PropertyExpression.property("country")
    static let CITY         = PropertyExpression.property("city")
    static let STATE        = PropertyExpression.property("state")
    static let ADDRESS      = PropertyExpression.property("address")
    static let FAA          = PropertyExpression.property("faa")
    static let ICAO         = PropertyExpression.property("icao")
    static let AIRPORTNAME  = PropertyExpression.property("airportname")
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

