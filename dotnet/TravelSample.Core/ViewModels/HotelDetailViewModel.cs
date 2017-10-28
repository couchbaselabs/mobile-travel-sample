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
using MvvmCross.Core.ViewModels;

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// Gets the view model for the hotel details view
    /// </summary>
    /// <remarks>
    /// This view model acts entirely on readonly in-memory data, and so has no model
    /// </remarks>
    public sealed class HotelDetailViewModel : MvxViewModel<IReadOnlyDictionary<string, object>>
    {
        #region Variables

        private IReadOnlyDictionary<string, object> _hotelDetails;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the full address of the hotel
        /// </summary>
        public string Address => $"{_hotelDetails.GetCast<string>("address")}, {_hotelDetails.GetCast<string>("city")}, {_hotelDetails.GetCast<string>("country")}";

        /// <summary>
        /// Gets the description of the hotel
        /// </summary>
        public string Description => _hotelDetails.GetCast<string>("description") ?? "Best. Hotel. Ever.";

        /// <summary>
        /// Gets whether or not this hotel offer free breakfast
        /// </summary>
        public bool FreeBreakfast => _hotelDetails.GetCast<bool>("free_breakfast");

        /// <summary>
        /// Gets whether or not this hotel offer free Internet
        /// </summary>
        public bool FreeInternet => _hotelDetails.GetCast<bool>("free_internet");

        /// <summary>
        /// Gets whether or not this hotel offer free parking
        /// </summary>
        public bool FreeParking => _hotelDetails.GetCast<bool>("free_parking");

        /// <summary>
        /// Gets the name of the hotel
        /// </summary>
        public string Name => _hotelDetails.GetCast<string>("name") ?? "Weird...no name for this hotel";

        /// <summary>
        /// Gets whether or not the hotel allows pets
        /// </summary>
        public bool PetsOk => _hotelDetails.GetCast<bool>("pets_ok");

        /// <summary>
        /// Gets the url of the hotel website
        /// </summary>
        public string Website => _hotelDetails.GetCast<string>("url");

        #endregion

        #region Overrides

        /// <inheritdoc />
        public override void Prepare(IReadOnlyDictionary<string, object> parameter)
        {
            _hotelDetails = parameter;
        }

        #endregion
    }
}
