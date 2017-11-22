// 
// HotelListModel.cs
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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Couchbase.Lite;
using Couchbase.Lite.Query;
using Newtonsoft.Json;
using TravelSample.Core.ViewModels;
using static TravelSample.Core.Util.Constants;
using Hotel = System.Collections.Generic.IReadOnlyDictionary<string, object>;
using Hotels = System.Collections.Generic.List<System.Collections.Generic.IReadOnlyDictionary<string, object>>;

namespace TravelSample.Core.Models
{
    /// <summary>
    /// The model class for the hotel list view
    /// </summary>
    public sealed class HotelListModel : BaseModel<HotelListViewModelParameter>
    {
        #region Constants

        private static readonly IExpression AddressProperty = Expression.Property("address");
        private static readonly IExpression CityProperty = Expression.Property("city");
        private static readonly IExpression CountryProperty = Expression.Property("country");
        private static readonly IExpression DescriptionProperty = Expression.Property("description");
        private static readonly IExpression StateProperty = Expression.Property("state");

        #endregion

        #region Properties

        private CouchbaseSession UserSession => _param.Session;

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="param">The data to construct the model from (session and bookmark data)</param>
        public HotelListModel(HotelListViewModelParameter param) 
            : base(param)
        {
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Gets the hotels from either the backend web server (for guest) or
        /// local database (for logged in users) that match the given
        /// description and location
        /// </summary>
        /// <param name="description">Words to search for in the description and name (optional)</param>
        /// <param name="location">Words to search for in the location (address, city, etc)</param>
        /// <returns>An <c>await</c>able <see cref="Task"/> that contains the results of the search</returns>
        public Task<Hotels> FetchHotelsAsync(string description, string location)
        {
            return UserSession.IsGuest
                ? FetchHotelsFromWebAsync(description, location)
                : FetchHotelsFromLocalAsync(description, location);
        }

        /// <summary>
        /// Either bookmarks or unbookmarks the given hotel
        /// </summary>
        /// <param name="hotel">The item from the hotel list to operate on</param>
        public void ToggleBookmark(HotelListCellModel hotel)
        {
            using (var document = UserSession.FetchGuestBookmarkDocument()?.ToMutable()) {
                var doc = document;
                if (document == null) {
                    if (hotel.IsBookmarked) {
                        throw new InvalidOperationException("Guest bookmark document not found");
                    }

                    doc = new MutableDocument(new Dictionary<string, object> {["type"] = "bookmarkedhotels"});
                }

                var bookmarked = doc.GetArray("hotels") ?? new MutableArray();
                if (hotel.IsBookmarked) {
                    // Remove the bookmark
                    for (int i = 0; i < bookmarked.Count; i++) {
                        if (bookmarked[i].ToString() == hotel.Source["id"] as string) {
                            bookmarked.RemoveAt(i);
                            break;
                        }
                    }
                } else {
                    bookmarked.Add(hotel.Source["id"] as string);
                }

                doc.Set("hotels", bookmarked);
                UserSession.Database.Save(doc);

                // Add the hotel details document
                if (hotel.Source["id"] is string id) {
                    using (var detailDoc = UserSession.Database.GetDocument(id).ToMutable() ?? new MutableDocument(id)) {
                        detailDoc.Set(hotel.Source.ToDictionary(x => x.Key, x => x.Value));
                        UserSession.Database.Save(detailDoc);
                    }
                }
            }
        }

        #endregion

        #region Private Methods

        private Task<Hotels> FetchHotelsFromLocalAsync(string description, string location)
        {
            // Description is looked up in the "description" and "name" content
            // Location is looking up in country, city, state, and address
            // Reference: https://developer.couchbase.com/documentation/server/4.6/sdk/sample-application.html
            // MATCH can only appear at top-leve, or in a top-level AND

            IExpression descExp = null;
            if (!String.IsNullOrWhiteSpace(description)) {
                descExp = DescriptionProperty.Match(description);
            }

            var locationExp = CountryProperty.Like($"%{location}%")
                .Or(CityProperty.Like($"%{location}%"))
                .Or(StateProperty.Like($"%{location}%"))
                .Or(AddressProperty.Like($"%{location}%"));

            var searchExp = locationExp;
            if (descExp != null) {
                searchExp = descExp.And(locationExp);
            }

            using (var hotelSearchQuery = Query
                .Select(SelectResult.All())
                .From(DataSource.Database(UserSession.Database))
                .Where(TypeProperty.EqualTo("hotel").And(searchExp))) {
                using (var results = hotelSearchQuery.Run()) {
                    var hotels = results.Select(x => x.GetDictionary(0).ToDictionary(y => y.Key, y => y.Value) as Hotel).ToList();
                    return Task.FromResult(hotels);
                }
            }
        }

        private async Task<Hotels> FetchHotelsFromWebAsync(string description, string location)
        {
            // Description is looked up in the "description" and "name" content
            // Location is looking up in country, city, state, and address
            // Reference: https://developer.couchbase.com/documentation/server/4.6/sdk/sample-application.html
            // Example query: http://localhost:8080/api/hotel/<description>/<location>

            var escapedDescStr = Uri.EscapeUriString(description ?? "*");
            var escapedLocationStr = Uri.EscapeUriString(location ?? String.Empty);
            var searchPath = $"hotel/{escapedDescStr}/{escapedLocationStr}";
            var url = new Uri(ServerBackendUrl, searchPath);
            var result = await UserSession.HttpClient.GetStringAsync(url);
            return JsonConvert.DeserializeObject<IDictionary<string, Hotels>>(result)["data"];
        }

        #endregion
    }
}