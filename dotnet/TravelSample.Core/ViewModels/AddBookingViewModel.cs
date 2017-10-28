// 
// AddBookingViewModel.cs
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
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Globalization;
using System.Threading.Tasks;
using System.Windows.Input;
using Acr.UserDialogs;
using Couchbase.Lite.Util;
using MvvmCross.Core.Navigation;
using MvvmCross.Core.ViewModels;
using MvvmCross.Platform.UI;
using TravelSample.Core.Models;
using TravelSample.Core.Util;
using Bookings = System.Collections.Generic.List<TravelSample.Core.ViewModels.FlightCellModel>; // IList<Booking>
using Flight = System.Collections.Generic.Dictionary<string, object>;
using Flights = System.Collections.Generic.List<System.Collections.Generic.Dictionary<string, object>>; // IList<Flight>

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// A model for a cell in a list of flights
    ///  </summary>
    public sealed class FlightCellModel : IIndexable
    {
        #region Constants

        private static readonly IFormatProvider UsFormat = new CultureInfo("en-US");

        #endregion

        #region Properties

        /// <summary>
        /// Gets the airline serving the flight
        /// </summary>
        public string Airline => Source.GetCast<string>("name");

        /// <summary>
        /// Gets the departure date of the flight
        /// </summary>
        public string DepartureDate { get; }

        /// <summary>
        /// Gets the departure time of the flight
        /// </summary>
        public string DepartureTime => Source.GetCast<string>("utc");

        /// <summary>
        /// Gets the fare of the flight in dollars
        /// </summary>
        public string Fare => Source.GetCast<float>("price").ToString("C", UsFormat);

        /// <inheritdoc />
        public int Index { get; }

        /// <summary>
        /// Gets the raw source information for the flight
        /// </summary>
        public IDictionary<string, object> Source { get; }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="source">The source information for this flight</param>
        /// <param name="departureDate">The departure date for this flight</param>
        /// <param name="index">The index for this entry</param>
        public FlightCellModel(IDictionary<string, object> source, string departureDate, int index)
        {
            Source = source;
            DepartureDate = departureDate;
            Index = index;
        }

        #endregion
    }

    /// <summary>
    /// Criteria for searching for a flight
    /// </summary>
    public sealed class FlightSearchCriteria
    {
        #region Properties

        /// <summary>
        /// The date of the flight to search for
        /// </summary>
        public string Date { get; }

        /// <summary>
        /// The name of the flight to search for
        /// </summary>
        public string Name { get; }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="name">The name of the flight to search for</param>
        /// <param name="date">The date of the flight to search for</param>
        public FlightSearchCriteria(string name, string date)
        {
            Name = name;
            Date = date;
        }

        #endregion
    }

    /// <summary>
    /// The view model for the add booking view
    /// </summary>
    public sealed class AddBookingViewModel : BaseViewModel<AddBookingModel, CouchbaseSession>
    {
        #region Constants

        private static readonly MvxColor Green = new MvxColor(0, 128, 0);
        private static readonly MvxColor Red = new MvxColor(128, 0, 0);

        #endregion

        #region Variables
        
        private readonly ObservableCollection<FlightCellModel> _outboundFlights = new ObservableCollection<FlightCellModel>();
        private readonly ObservableCollection<FlightCellModel> _returnFlights = new ObservableCollection<FlightCellModel>();
        private string _departureDateSearch;
        private string _destinationSearch;
        private List<string> _destinationSuggestions;
        private string _originSearch;
        private List<string> _originSuggestions;
        private bool _outboundSelected = true;
        private bool _justSwitchedList;
        private string _returnDateSearch;
        private int _selectedDepartureIndex = -1;
        private int _selectedReturnIndex = -1;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the command for confirming the booking based on the information chosen
        /// in the view
        /// </summary>
        public ICommand ConfirmBookingCommand => new MvxCommand(DoConfirm);

        /// <summary>
        /// Gets the color of the text for the departure date (red indicates invalid
        /// and green indicates valid)
        /// </summary>
        public MvxColor DepartureColor => ValidateDate(DepartureDateSearch) ? Green : Red;

        /// <summary>
        /// Gets or sets the departure date to search for
        /// </summary>
        public string DepartureDateSearch
        {
            get => _departureDateSearch;
            set  {
                SetProperty(ref _departureDateSearch, value);
                RaisePropertyChanged(nameof(DepartureColor));
            }
        }

        /// <summary>
        /// Gets or sets the destination airport to search for
        /// </summary>
        public string DestinationSearch
        {
            get => _destinationSearch;
            set {
                SetProperty(ref _destinationSearch, value);
                UpdateDestinationSuggestions();
            }
        }

        /// <summary>
        /// Gets a list of destination airport suggestions
        /// </summary>
        public List<string> DestinationSuggestions
        {
            get => _destinationSuggestions;
            private set => SetProperty(ref _destinationSuggestions, value);
        }

        /// <summary>
        /// Gets the list of flights matching the given search criteria
        /// </summary>
        public ObservableCollection<FlightCellModel> FlightsList =>
            OutboundSelected ? _outboundFlights : _returnFlights;

        /// <summary>
        /// Gets or sets the origin airport to search for
        /// </summary>
        public string OriginSearch
        {
            get => _originSearch;
            set {
                SetProperty(ref _originSearch, value);
                UpdateOriginSuggestions();
            }
        }

        /// <summary>
        /// Gets a list of origin airport suggestions
        /// </summary>
        public List<string> OriginSuggestions
        {
            get => _originSuggestions;
            private set => SetProperty(ref _originSuggestions, value);
        }

        /// <summary>
        /// Gets or sets whether or not the user is looking at
        /// outbound flights (as opposed to return flights)
        /// </summary>
        public bool OutboundSelected
        {
            get => _outboundSelected;
            set {
                _justSwitchedList = _outboundSelected != value;
                SetProperty(ref _outboundSelected, value);
                RaisePropertyChanged(nameof(FlightsList));
                RaisePropertyChanged(nameof(SelectedFlightIndex));
            }
        }

        /// <summary>
        /// Gets the command for executing the search for flights matching
        /// the search criteria in the view
        /// </summary>
        public ICommand PerformSearch => new MvxAsyncCommand(SearchForFlightsAsync);

        /// <summary>
        /// Gets the color of the text for the return date (red indicates invalid
        /// and green indicates valid)
        /// </summary>
        public MvxColor ReturnColor => ValidateDate(ReturnDateSearch) ? Green : Red;

        /// <summary>
        /// Gets or sets the return date to search for
        /// </summary>
        public string ReturnDateSearch
        {
            get => _returnDateSearch;
            set {
                SetProperty(ref _returnDateSearch, value);
                RaisePropertyChanged(nameof(ReturnColor));
            }
        }

        /// <summary>
        /// Gets or sets the currently selected flight in the list of flights
        /// </summary>
        public int SelectedFlightIndex
        {
            get => OutboundSelected ? _selectedDepartureIndex : _selectedReturnIndex;
            set {
                if (_justSwitchedList && value == -1) {
                    // Don't let deselection happen as a result of switching list views
                    return;
                }

                _justSwitchedList = false;
                if (OutboundSelected) {
                    SetProperty(ref _selectedDepartureIndex, value);
                } else {
                    SetProperty(ref _selectedReturnIndex, value);
                }
            }
        }

        #endregion

        #region Private Methods

        private void DoConfirm()
        {
            var bookings = new Bookings();
            if (_selectedDepartureIndex > -1) {
                bookings.Add(_outboundFlights[_selectedDepartureIndex]);
            }

            if (_selectedReturnIndex > -1) {
                bookings.Add(_returnFlights[_selectedReturnIndex]);
            }

            if (bookings.Count == 0) {
                UserDialogs.Instance.Alert("Please select at least one flight", "Error");
                return;
            }

            try {
                Model.AddFlightBookings(bookings);
            } catch (Exception e) {
                Debug.WriteLine($"Failed to add bookings {e}");
                UserDialogs.Instance.Alert($"Failed to add bookings: {e.Message}", "Error");
            }
        }

        private async Task SearchForFlightsAsync()
        {
            if (String.IsNullOrWhiteSpace(OriginSearch) && String.IsNullOrWhiteSpace(DestinationSearch)
                || !ValidateDate(DepartureDateSearch) || !ValidateDate(ReturnDateSearch)) {
                UserDialogs.Instance.Alert("Invalid information entered", "Error");
                return;
            }

            var source = new FlightSearchCriteria(OriginSearch, DepartureDateSearch);
            var destination = new FlightSearchCriteria(DestinationSearch, ReturnDateSearch);
            for (var i = 0; i < 2; i++) {
                
                Flights flights;
                try {
                    flights = i == 0
                        ? await Model.FetchFlightsAsync(source, destination)
                        : await Model.FetchFlightsAsync(destination, source);
                } catch (Exception e) {
                    Debug.WriteLine($"Error fetching flights: {e}");
                    await UserDialogs.Instance.AlertAsync(e.Message, "Error fetching flights from server");
                    return;
                }

                var flightCells = i == 0 ? _outboundFlights : _returnFlights;
                flightCells.Clear();
                var index = 0;
                foreach (Flight f in flights) {
                    flightCells.Add(new FlightCellModel(f, i == 0 ? source.Date : destination.Date, index++));
                }
            }
        }

        private void UpdateDestinationSuggestions()
        {
            DestinationSuggestions = Model.FetchMatchingAirports(DestinationSearch);
        }

        private void UpdateOriginSuggestions()
        {
            OriginSuggestions = Model.FetchMatchingAirports(OriginSearch);
        }

        private bool ValidateDate(string input)
        {
            if (String.IsNullOrEmpty(input)) {
                return true;
            }

            DateTime temp;
            return DateTime.TryParseExact(input, "MM/dd/yyyy", CultureInfo.CurrentCulture, DateTimeStyles.AllowWhiteSpaces, out temp);
        }

        #endregion

        #region Overrides

#if DEBUG
        public override void Prepare(CouchbaseSession parameter)
        {
            base.Prepare(parameter);
            DepartureDateSearch = "05/04/2017";
            OriginSearch = "Heathrow";
            ReturnDateSearch = "05/04/2017";
            DestinationSearch = "San Diego Intl";
        }
#endif

        #endregion
    }
}