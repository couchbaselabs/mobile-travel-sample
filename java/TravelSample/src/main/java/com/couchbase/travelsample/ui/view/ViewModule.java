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
package com.couchbase.travelsample.ui.view;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;


@Module
public interface ViewModule {
    @Binds
    @Named("StartPage")
    Page<?> startPage(LoginView v);

    @Binds
    @IntoSet
    Page<?> loginView(LoginView v);

    @Binds
    @IntoSet
    Page<?> guestView(GuestView v);

    @Binds
    @IntoSet
    Page<?> searchHotelView(HotelSearchView v);

    @Binds
    @IntoSet
    Page<?> hotelFlightView(HotelFlightView v);
}
