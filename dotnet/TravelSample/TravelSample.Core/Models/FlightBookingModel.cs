// 
// FlightBookingModel.cs
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
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;
using Couchbase.Lite;
using Couchbase.Lite.Query;
using Newtonsoft.Json;
using TravelSample.Core.ViewModels;
using static TravelSample.Core.Util.Constants;

namespace TravelSample.Core.Models
{
    /// <summary>
    /// Event arguments for <see cref="FlightBookingModel.BookingsChanged"/>
    /// </summary>
    public sealed class BookingsUpdateEventArgs : EventArgs
    {
        #region Properties

        /// <summary>
        /// The new set of bookings reflecting the current state of the database
        /// </summary>
        public IEnumerable<BookingCellModel> NewBookings { get; }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="newBookings">The new set of bookings</param>
        public BookingsUpdateEventArgs(IEnumerable<BookingCellModel> newBookings)
        {
            NewBookings = newBookings;
        }

        #endregion
    }

    /// <summary>
    /// The model class for the flight bookings view
    /// </summary>
    public sealed class FlightBookingModel : BaseModel<CouchbaseSession>, IDisposable
    {
        #region Constants

        private static readonly ISelectResult FlightsResult = SelectResult.Expression(Expression.Property("flights"));

        #endregion

        #region Variables

        /// <summary>
        /// An event fired when the set of user bookings changes
        /// </summary>
        public event EventHandler<BookingsUpdateEventArgs> BookingsChanged;

        private IQuery _bookingQuery;

        private ListenerToken _cancelToken;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the current user session
        /// </summary>
        public CouchbaseSession UserSession => _param;

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="session">The current user session</param>
        public FlightBookingModel(CouchbaseSession session)
            : base(session)
        {
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Gets the current user bookings from the database
        /// </summary>
        /// <returns>An <c>await</c>able <see cref="Task"/> that will complete when the 
        /// first set of results are ready</returns>
        public Task FetchBookings()
        {
            var tcs = new TaskCompletionSource<bool>();
            _bookingQuery = Query
                .Select(FlightsResult)
                .From(DataSource.Database(UserSession.Database))
                .Where(UsernameProperty.EqualTo(UserSession.Username));

            var retVal = tcs.Task;
            _cancelToken = _bookingQuery.AddChangeListener(null, (sender, args) =>
            {
                foreach (var row in args.Rows) {
                    var bookings = row.GetArray("flights");
                    var eventArgs = new BookingsUpdateEventArgs(EnumerateBookings(bookings));
                    BookingsChanged?.Invoke(this, eventArgs);
                }

                var oldTcs = Interlocked.Exchange(ref tcs, null);
                oldTcs?.SetResult(true);
            });
            
            return retVal;
        }

        /// <summary>
        /// Removes a given booking from the list
        /// </summary>
        /// <param name="booking">The item from the flight bookings list to remove</param>
        public void RemoveBooking(BookingCellModel booking)
        {
            var userDocId = UserSession.UserDocID;
            if (userDocId == null) {
                throw new InvalidOperationException("Cannot find current user!");
            }

            using (var flightDocument = UserSession.Database.GetDocument(userDocId).ToMutable()) {
                var documentBookings = flightDocument.GetArray("flights") ?? new MutableArray();
                for (int i = 0; i < documentBookings.Count; i++) {
                    if (IsEqualBooking(documentBookings.GetDictionary(i), booking.Source)) {
                        documentBookings.RemoveAt(i);
                        break;
                    }
                }

                Debug.WriteLine(
                    $"Updated booking after delete is {JsonConvert.SerializeObject(documentBookings.ToList())}");
                flightDocument.SetArray("flights", documentBookings);
                UserSession.Database.Save(flightDocument);
            }
        }

        #endregion

        #region Private Methods

        private static bool FloatEqual(float? left, float? right)
        {
            if (!left.HasValue || !right.HasValue) {
                return false;
            }

            return Math.Abs(left.Value - right.Value) < Single.Epsilon;
        }

        private static bool IsEqualBooking(IDictionaryObject left, IDictionaryObject right)
        {
            return left["destinationairport"].ToString() == right["destinationairport"].ToString() &&
                   left["equipment"].ToString() == right["equipment"].ToString() &&
                   left["flight"].ToString() == right["flight"].ToString() &&
                   left["flighttime"].ToString() == right["flighttime"].ToString() &&
                   left["name"].ToString() == right["name"].ToString() &&
                   FloatEqual(left["price"].Float, right["price"].Float) &&
                   left["sourceairport"].ToString() == right["sourceairport"].ToString() &&
                   left["utc"].ToString() == right["utc"].ToString();
        }

        private IEnumerable<BookingCellModel> EnumerateBookings(IArray bookings)
        {
            if (bookings != null)
            {
                for (var i = 0; i < bookings.Count; i++)
                {
                    var booking = bookings.GetDictionary(i);
                    yield return new BookingCellModel(booking);
                }
            }
        }

        #endregion

        #region IDisposable

        public void Dispose()
        {
            _bookingQuery?.RemoveChangeListener(_cancelToken);
            UserSession.Dispose();
        }

        #endregion
    }
}