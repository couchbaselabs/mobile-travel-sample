// 
// FlightBookingViewModel.cs
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
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Threading.Tasks;
using System.Windows.Input;
using Couchbase.Lite;
using TravelSample.Core.Models;
using Xamarin.Forms;
using static TravelSample.Core.Util.Constants;

namespace TravelSample.Core.ViewModels
{

    /// <summary>
    /// A model for a cell in a list of bookings
    /// </summary>
    public sealed class BookingCellModel
    {
        #region Properties

        /// <summary>
        /// Gets the arrival airport for the flight (FAA code)
        /// </summary>
        public string ArrivalAirport => Source["destinationairport"].ToString();

        /// <summary>
        /// Gets the date and time for the departure of this flight
        /// </summary>
        public string Date => Source["date"].ToString();

        /// <summary>
        /// Gets the departure airport for the flight (FAA code)
        /// </summary>
        public string DepartureAirport => Source["sourceairport"].ToString();

        /// <summary>
        /// Gets the price of the flight (in US dollars)
        /// </summary>
        public string FarePrice => Source["price"].Float.ToString("C", UsCulture);

        /// <summary>
        /// Gets the airline and flight number for the current flight
        /// </summary>
        public string Flight =>
            $"{Source["name"].ToString() ?? String.Empty} : {Source["flight"].ToString() ?? String.Empty}";

        /// <summary>
        /// Gets the raw information about the flight
        /// </summary>
        public IDictionaryObject Source { get; }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="source">The source information for the flight</param>
        public BookingCellModel(IDictionaryObject source)
        {
            Source = source;
        }

        #endregion
    }

    /// <summary>
    /// The view model for the flight bookings list view
    /// </summary>
    public sealed class FlightBookingViewModel : BaseViewModel<FlightBookingModel, CouchbaseSession>, IDisposable
    {
        #region Properties

        /// <summary>
        /// Gets the command for showing the view to add a booking
        /// </summary>
        public ICommand AddBookingCommand => new Command(
            () => RequestNavigation(new AddBookingViewModel(Model.UserSession)));

        /// <summary>
        /// Gets the list of bookings
        /// </summary>
        public ObservableCollection<BookingCellModel> BookingsList { get; } = new ObservableCollection<BookingCellModel>();

        /// <summary>
        /// Gets the command to remove a booking from the list
        /// </summary>
        public ICommand RemoveBookingCommand => new Command<BookingCellModel>(RemoveBooking);

        /// <summary>
        /// Gets the command for showing the hotels list view
        /// </summary>
        public ICommand ShowHotelsCommand => new Command(
            () => RequestNavigation(
                new HotelListViewModel(new HotelListViewModelParameter(Model.UserSession, false, null))));

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="session">The current user session</param>
        public FlightBookingViewModel(CouchbaseSession session)
            : base(session)
        {
            
        }

        #endregion

        #region Public Methods

        public async Task Refresh()
        {
            Model.BookingsChanged += (sender, args) =>
            {
                Device.BeginInvokeOnMainThread(() =>
                {
                    BookingsList.Clear();
                    foreach (var booking in args.NewBookings) {
                        BookingsList.Add(booking);
                    }
                });
            };
            await Model.FetchBookings();
        }

        #endregion

        #region Private Methods

        private void RemoveBooking(BookingCellModel booking)
        {
            try {
                Model.RemoveBooking(booking);
            } catch (Exception e) {
                Debug.WriteLine($"Error deleting booking: {e}");
                Application.Current.MainPage.DisplayAlert("Error", $"Failed to delete booking: {e.Message}", "OK");
            }
        }

        #endregion

        #region IDisposable

        public void Dispose()
        {
            Model.Dispose();
        }

        #endregion
    }

    // Design-time usage
    public sealed class DesignFlightBookingViewModel
    {
        #region Properties

        public ObservableCollection<BookingCellModel> BookingsList { get; } =
            new ObservableCollection<BookingCellModel>();

        #endregion

        #region Constructors

        public DesignFlightBookingViewModel()
        {
            var data = new MutableDictionaryObject()
                .SetString("destinationairport", "BBB")
                .SetString("sourceairport", "AAA")
                .SetString("date", DateTime.Now.ToString())
                .SetDouble("price", 999.99)
                .SetString("name", "Airplane Airlines")
                .SetString("flight", "AB123");

         

            BookingsList.Add(new BookingCellModel(data));

        }

        #endregion
    }
}