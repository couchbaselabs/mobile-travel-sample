//
// Copyright (c) 2019 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.travelsample.model;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class Trip {
    @Nonnull
    private final Flight outboundFlight;
    @Nonnull
    private final Flight returnFlight;
    private final long departureDate;
    private final long returnDate;

    public Trip(
        @Nonnull Flight outboundFlight,
        @Nonnull Flight returnFlight,
        @Nullable Date departureDate,
        @Nullable Date returnDate) {
        this.outboundFlight = outboundFlight;
        this.returnFlight = returnFlight;
        this.departureDate = (departureDate == null) ? 0 : departureDate.getTime();
        this.returnDate = (returnDate == null) ? 0 : returnDate.getTime();
    }

    @Nonnull
    public Flight getOutboundFlight() { return outboundFlight; }

    @Nonnull
    public Flight getReturnFlight() { return returnFlight; }

    @Nonnull
    public Date getDepartureDate() { return new Date(departureDate); }

    @Nonnull
    public Date getReturnDate() { return new Date(returnDate); }
}
