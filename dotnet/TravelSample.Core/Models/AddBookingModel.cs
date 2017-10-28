// 
// AddBookingModel.cs
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
using Airports = System.Collections.Generic.List<System.String>;
using Bookings = System.Collections.Generic.List<TravelSample.Core.ViewModels.FlightCellModel>;
using Flights = System.Collections.Generic.List<System.Collections.Generic.Dictionary<string, object>>;

namespace TravelSample.Core.Models
{
    /// <summary>
    /// The model class for the add booking view.
    /// </summary>
    public sealed class AddBookingModel : BaseModel<CouchbaseSession>
    {
        #region Constants

        private static readonly IExpression AirportNameProperty = Expression.Property("airportname");
        private static readonly ISelectResult AirportNameResult = SelectResult.Expression(AirportNameProperty);
        private static readonly IExpression FaaProperty = Expression.Property("faa");
        private static readonly IExpression IcaoProperty = Expression.Property("icao");

        #endregion

        #region Properties

        private CouchbaseSession UserSession => _param;

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="session">The current user session in progress</param>
        public AddBookingModel(CouchbaseSession session)
            : base(session)
        {
             
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Adds one or more flight bookings to the current user's database
        /// </summary>
        /// <param name="bookings">The list of bookings to add</param>
        public void AddFlightBookings(Bookings bookings)
        {
            var userDocID = UserSession.UserDocID;
            if (userDocID == null) {
                throw new InvalidOperationException("Unable to find current user!");
            }

            using (var flightDocument = UserSession.Database.GetDocument(userDocID)) {
                var documentBookings = flightDocument.GetArray("flights") ?? new ArrayObject();
                foreach (var b in bookings) {
                    b.Source["date"] = $"{b.DepartureDate} {b.Source["utc"]}";
                    documentBookings.Add(b.Source);
                }

                flightDocument.Set("flights", documentBookings);
                UserSession.Database.Save(flightDocument);
            }
        }

        /// <summary>
        /// Fetches the flights matching the given criteria for source and destination
        /// from the backend server
        /// </summary>
        /// <param name="source">The flight criteria for the source flight</param>
        /// <param name="destination">The flight criteria for the destination</param>
        /// <returns>An <c>await</c>able <c>Task</c> containing the resulting flights</returns>
        public async Task<Flights> FetchFlightsAsync(FlightSearchCriteria source, FlightSearchCriteria destination)
        {
            var leaveDate = source.Date ?? String.Empty;
            var searchPath = $"{source.Name}/{destination.Name}";
            var escapedSearchPath = Uri.EscapeUriString(searchPath);
            var fullPath = $"flightPaths/{escapedSearchPath}?leave={leaveDate}";
            var url = new Uri(ServerBackendUrl, fullPath);
            var result = await UserSession.HttpClient.GetStringAsync(url);
            var deserialized = JsonConvert.DeserializeObject<IDictionary<string, Flights>>(result);
            return deserialized["data"];
        }

        /// <summary>
        /// Fetches airports matching the given search string from the local database.
        /// Three letter searches will search for FAA codes, four letter searches will
        /// search for ICAO code, and all others will search for the airport name
        /// </summary>
        /// <param name="searchStr">The string to search for</param>
        /// <returns>The matching airports</returns>
        public Airports FetchMatchingAirports(string searchStr)
        {
            IQuery searchQuery;
            switch (searchStr.Length) {
                case (int)AirportCodeLength.FAA:
                    searchQuery = Query
                        .Select(AirportNameResult)
                        .From(DataSource.Database(UserSession.Database))
                        .Where(TypeProperty
                            .EqualTo("airport")
                            .And(FaaProperty
                                .EqualTo(searchStr.ToUpperInvariant())))
                        .OrderBy(Ordering.Property("datfield").Ascending());
                    break;
                case (int)AirportCodeLength.ICAO:
                    searchQuery = Query
                        .Select(AirportNameResult)
                        .From(DataSource.Database(UserSession.Database))
                        .Where(TypeProperty
                            .EqualTo("airport")
                            .And(IcaoProperty
                                .EqualTo(searchStr.ToUpperInvariant())));
                    break;
                default:
                    searchQuery = Query
                        .Select(AirportNameResult)
                        .From(DataSource.Database(UserSession.Database))
                        .Where(TypeProperty
                            .EqualTo("airport")
                            .And(AirportNameProperty
                                .Like($"{searchStr}%")));
                    break;
            }

            try {
                using (var results = searchQuery.Run()) {
                    return results.Select(x => x.GetString("airportname")).Where(x => x != null).ToList();
                }
            } finally {
                searchQuery.Dispose();
            }
        }

        #endregion

        #region Nested

        private enum AirportCodeLength
        {
            FAA = 3,
            ICAO = 4
        };

        #endregion
    }
}