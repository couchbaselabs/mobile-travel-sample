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
using Acr.UserDialogs;
using Couchbase.Lite;
using MvvmCross.Core.Navigation;
using MvvmCross.Core.Navigation.EventArguments;
using MvvmCross.Core.ViewModels;
using TravelSample.Core.Models;
using TravelSample.Core.Util;
using static TravelSample.Core.Util.Constants;

namespace TravelSample.Core.ViewModels
{

    /// <summary>
    /// A model for a cell in a list of bookings
    /// </summary>
    public sealed class BookingCellModel : IIndexable
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
        public string FarePrice => Source["price"].ToFloat().ToString("C", UsCulture);

        /// <summary>
        /// Gets the airline and flight number for the current flight
        /// </summary>
        public string Flight =>
            $"{Source["name"].ToString() ?? String.Empty} : {Source["flight"].ToString() ?? String.Empty}";

        /// <inheritdoc />
        public int Index { get; }

        /// <summary>
        /// Gets the raw information about the flight
        /// </summary>
        public IReadOnlyDictionary Source { get; }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="source">The source information for the flight</param>
        /// <param name="index">The index of this entry in the list</param>
        public BookingCellModel(IReadOnlyDictionary source, int index)
        {
            Source = source;
            Index = index;
        }

        #endregion
    }

    /// <summary>
    /// The view model for the flight bookings list view
    /// </summary>
    public sealed class FlightBookingViewModel : BaseViewModel<FlightBookingModel, CouchbaseSession>
    {
        #region Variables

        private readonly IMvxNavigationService _navigationService;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the command for showing the view to add a booking
        /// </summary>
        public ICommand AddBookingCommand => new MvxCommand(
            () => _navigationService.Navigate<AddBookingViewModel, CouchbaseSession>(Model.UserSession));

        /// <summary>
        /// Gets the list of bookings
        /// </summary>
        public ObservableCollection<BookingCellModel> BookingsList { get; } = new ObservableCollection<BookingCellModel>();

        /// <summary>
        /// Gets the command to remove a booking from the list
        /// </summary>
        public ICommand RemoveBookingCommand => new MvxCommand<BookingCellModel>(RemoveBooking);

        /// <summary>
        /// Gets the command for showing the hotels list view
        /// </summary>
        public ICommand ShowHotelsCommand => new MvxCommand(
            () => _navigationService.Navigate<HotelListViewModel, HotelListViewModelParameter>(
                new HotelListViewModelParameter(Model.UserSession, false, null)));

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="navigationService">The injected navigation service</param>
        public FlightBookingViewModel(IMvxNavigationService navigationService)
        {
            _navigationService = navigationService;
            _navigationService.AfterClose += OnClose;
        }

        #endregion

        #region Private Methods

        private void OnClose(object sender, NavigateEventArgs e)
        {
            if (e.ViewModel != this) {
                return;
            }

            Model.UserSession.Dispose();
            _navigationService.AfterClose -= OnClose;
        }

        private void RemoveBooking(BookingCellModel booking)
        {
            try {
                Model.RemoveBooking(booking);
            } catch (Exception e) {
                Debug.WriteLine($"Error deleting booking: {e}");
                UserDialogs.Instance.Alert($"Failed to delete booking: {e.Message}", "Error");
            }
        }

        #endregion

        #region Overrides

        
        /// <inheritdoc />
        public override async Task Initialize()
        {
            Model.BookingsChanged += (sender, args) =>
            {
                InvokeOnMainThread(() =>
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
    }
}