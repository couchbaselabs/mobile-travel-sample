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
using TravelSample.Core.Models;
using Xamarin.Forms;
using Bookings = System.Collections.Generic.List<TravelSample.Core.ViewModels.FlightCellModel>; // IList<Booking>
using Flight = System.Collections.Generic.Dictionary<string, object>;
using Flights = System.Collections.Generic.List<System.Collections.Generic.Dictionary<string, object>>; // IList<Flight>

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// A model for a cell in a list of flights
    ///  </summary>
    public sealed class FlightCellModel
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

        /// <summary>
        /// Gets the flight identifier for the flight
        /// </summary>
        public string Flight => Source.GetCast<string>("flight");

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
        public FlightCellModel(IDictionary<string, object> source, string departureDate)
        {
            Source = source;
            DepartureDate = departureDate;
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
        #region Variables

        private readonly ObservableCollection<FlightCellModel> _outboundFlights = new ObservableCollection<FlightCellModel>();
        private readonly ObservableCollection<FlightCellModel> _returnFlights = new ObservableCollection<FlightCellModel>();
        private string _departureDateSearch;
        private string _destinationSearch = "San Francisco Intl";
        private List<string> _destinationSuggestions;
        private bool _justSwitchedList;
        private string _originSearch = "Detroit Metro Wayne Co";
        private List<string> _originSuggestions;
        private string _returnDateSearch;
        private bool _returnSelected;
        private FlightCellModel _selectedDeparture;

        private string _selectedOrigin;
        private FlightCellModel _selectedReturn;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the command for confirming the booking based on the information chosen
        /// in the view
        /// </summary>
        public ICommand ConfirmBookingCommand => new Command(DoConfirm);

        /// <summary>
        /// Gets the color of the text for the departure date (red indicates invalid
        /// and green indicates valid)
        /// </summary>
        public Color DepartureColor => ValidateDate(DepartureDateSearch) ? Color.Green : Color.Red;

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
            !ReturnSelected ? _outboundFlights : _returnFlights;

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
        /// Gets the command for executing the search for flights matching
        /// the search criteria in the view
        /// </summary>
        public ICommand PerformSearch => new Command(async () => await SearchForFlightsAsync());

        /// <summary>
        /// Gets the color of the text for the return date (red indicates invalid
        /// and green indicates valid)
        /// </summary>
        public Color ReturnColor => ValidateDate(ReturnDateSearch) ? Color.Green : Color.Red;

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
        /// Gets or sets whether or not the user is looking at
        /// outbound flights (as opposed to return flights)
        /// </summary>
        public bool ReturnSelected
        {
            get => _returnSelected;
            set {
                _justSwitchedList = _returnSelected != value;
                SetProperty(ref _returnSelected, value);
                RaisePropertyChanged(nameof(FlightsList));
                RaisePropertyChanged(nameof(SelectedFlight));
            }
        }

        /// <summary>
        /// Gets or sets the currently selected flight in the list of flights
        /// </summary>
        public FlightCellModel SelectedFlight
        {
            get => !ReturnSelected ? _selectedDeparture : _selectedReturn;
            set {
                if (_justSwitchedList && value == null) {
                    // Don't let deselection happen as a result of switching list views
                    return;
                }

                _justSwitchedList = false;
                if (!ReturnSelected) {
                    SetProperty(ref _selectedDeparture, value);
                } else {
                    SetProperty(ref _selectedReturn, value);
                }
            }
        }

        public string SelectedOrigin
        {
            get => _selectedOrigin;
            set => SetProperty(ref _selectedOrigin, value);
        }

        #endregion

        #region Constructors

        public AddBookingViewModel(CouchbaseSession session)
            : base(session)
        {
        }

        #endregion

        #region Private Methods

        private void DoConfirm()
        {
            var bookings = new Bookings();
            if (_selectedDeparture != null) {
                bookings.Add(_selectedDeparture);
            }

            if (_selectedReturn != null) {
                bookings.Add(_selectedReturn);
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

            RequestBack();
        }

        private async Task SearchForFlightsAsync()
        {
            if (String.IsNullOrWhiteSpace(OriginSearch) || String.IsNullOrWhiteSpace(DestinationSearch)) {
                UserDialogs.Instance.Alert("Invalid departure or return airport entered", "Error");
                return;
            }

            if (ReturnSelected && !ValidateDate(ReturnDateSearch)) {
                UserDialogs.Instance.Alert("Invalid return date entered", "Error");
                return;
            } 
            
            if (!ReturnSelected && !ValidateDate(DepartureDateSearch)) {
                UserDialogs.Instance.Alert("Invalid departure date entered", "Error");
                return;
            }

            var source = new FlightSearchCriteria(OriginSearch, DepartureDateSearch);
            var destination = new FlightSearchCriteria(DestinationSearch, ReturnDateSearch);
            for (var i = 0; i < 2; i++) {
                if ((i == 0 && !ValidateDate(DepartureDateSearch)) || (i == 1 && !ValidateDate(ReturnDateSearch))) {
                    continue;
                }

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
                foreach (Flight f in flights) {
                    flightCells.Add(new FlightCellModel(f, i == 0 ? source.Date : destination.Date));
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
                return false;
            }

            DateTime temp;
            return DateTime.TryParseExact(input, "MM/dd/yyyy", CultureInfo.CurrentCulture, DateTimeStyles.AllowWhiteSpaces, out temp);
        }

        #endregion
    }
}