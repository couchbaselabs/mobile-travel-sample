// 
// HotelDetailViewModel.cs
// 
// Author:
//     Jim Borden  <jim.borden@couchbase.com>
// 
// Copyright (c) 2017 Couchbase, Inc All rights reserved.
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
using System.Collections.Generic;
using Couchbase.Lite.Util;

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// Gets the view model for the hotel details view
    /// </summary>
    /// <remarks>
    /// This view model acts entirely on readonly in-memory data, and so has no model
    /// </remarks>
    public sealed class HotelDetailViewModel : BaseViewModel<IReadOnlyDictionary<string, object>>
    {
        #region Properties

        /// <summary>
        /// Gets the full address of the hotel
        /// </summary>
        //public string Address => $"{Model.ContainsKey("address") ? Model["address"] as string : null}, {Model.ContainsKey("city") ? Model["city"] as string : null }, {Model.ContainsKey("country") ? Model["country"] as string : null}"; 

        public string Address => Model.ContainsKey("address") ? Model["address"] as string : null;

        /// <summary>
        /// Gets the description of the hotel
        /// </summary>
        public string Description => Model.ContainsKey("description") ? Model["description"] as string : "Best. Hotel. Ever.";

        /// <summary>
        /// Gets whether or not this hotel offer free breakfast
        /// </summary>
        public bool FreeBreakfast => Model.ContainsKey("free_breakfast") && (bool)Model["free_breakfast"];
        /// <summary>
        /// Gets whether or not this hotel offer free Internet
        /// </summary>
        public bool FreeInternet => Model.ContainsKey("free_internet") && (bool)Model["free_internet"];

        /// <summary>
        /// Gets whether or not this hotel offer free parking
        /// </summary>
        public bool FreeParking => Model.ContainsKey("free_parking") && (bool)Model["free_parking"]; 

        /// <summary>
        /// Gets the name of the hotel
        /// </summary>
        public string Name => Model.ContainsKey("name") ? Model["name"] as string : "Weird...no name for this hotel";

        /// <summary>
        /// Gets whether or not the hotel allows pets
        /// </summary>
        public bool PetsOk => Model.ContainsKey("pets_ok") && (bool)Model["pets_ok"];

        /// <summary>
        /// Gets the url of the hotel website
        /// </summary>
        public string Website => Model.ContainsKey("url") ? Model["url"] as string : null;

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="hotelDetails">The details to show in the page</param>
        public HotelDetailViewModel(IReadOnlyDictionary<string, object> hotelDetails)
            : base(hotelDetails)
        {
        }

        #endregion
    }
}
