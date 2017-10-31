// 
// HotelListViewModel.cs
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
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using Acr.UserDialogs;
using Couchbase.Lite.Util;
using TravelSample.Core.Models;
using TravelSample.Core.Util;
using Xamarin.Forms;
using Hotel = System.Collections.Generic.IReadOnlyDictionary<string, object>;
using Hotels = System.Collections.Generic.List<System.Collections.Generic.IReadOnlyDictionary<string, object>>;

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// The parameter for setting up the hotel list view model
    /// </summary>
    public sealed class HotelListViewModelParameter
    {
        #region Properties

        /// <summary>
        /// Gets whether or not this view will allow bookmarking
        /// </summary>
        public bool AllowBookmark { get; }

        /// <summary>
        /// Gets the list of bookmarked hotels to populate the initial list of
        /// bookmarks from
        /// </summary>
        public Hotels BookmarkedHotels { get; }

        /// <summary>
        /// Gets the current user session
        /// </summary>
        public CouchbaseSession Session { get; }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="session">The current user session</param>
        /// <param name="allowBookmark">Whether or not to allow bookmarking</param>
        /// <param name="bookmarkedHotels">A list of already bookmarked hotels</param>
        public HotelListViewModelParameter(CouchbaseSession session, bool allowBookmark, Hotels bookmarkedHotels)
        {
            Session = session;
            AllowBookmark = allowBookmark;
            BookmarkedHotels = bookmarkedHotels;
        }

        #endregion
    }

    /// <summary>
    /// A model for an entry in a hotel list view
    /// </summary>
    public sealed class HotelListCellModel : NotifyPropertyChanged, IEquatable<HotelListCellModel>
    {
        #region Variables

        private bool _isBookmarked;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the address of the hotel
        /// </summary>
        public string Address => Source.GetCast<string>("address");

        /// <summary>
        /// Gets the bookmark image to use for the cell
        /// </summary>
        public ImageSource BookmarkImage
        {
            get {
                if (IsBookmarked) {
                    switch (Device.RuntimePlatform) {
                        case Device.Android:
                            return ImageSource.FromFile("Bookmark_Filled.png");
                        case Device.iOS:
                            return ImageSource.FromFile("Bookmark-Filled");
                        case Device.UWP:
                            return ImageSource.FromFile("Assets/Bookmark-Filled.png");
                    }
                }

                return null;
            }
        }

        /// <summary>
        /// Gets the bookmark text to use for the cell context menu
        /// </summary>
        public string BookmarkText => IsBookmarked ? "UnBookmark" : "Bookmark";

        /// <summary>
        /// Gets or sets whether this item is bookmarked
        /// </summary>
        public bool IsBookmarked
        {
            get => _isBookmarked;
            set {
                SetProperty(ref _isBookmarked, value);
                RaisePropertyChanged(nameof(BookmarkText));
                RaisePropertyChanged(nameof(BookmarkImage));
            }
        }

        /// <summary>
        /// Gets the name of the hotel
        /// </summary>
        public string Name => Source.GetCast<string>("name");

        /// <summary>
        /// Gets the phone number of the hotel
        /// </summary>
        public string PhoneNumber => Source.GetCast<string>("phone");

        /// <summary>
        /// Gets the source information for this hotel
        /// </summary>
        public IReadOnlyDictionary<string, object> Source { get; }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="source">The source information for the hotel</param>
        public HotelListCellModel(IReadOnlyDictionary<string, object> source)
        {
            Source = source;
        }

        #endregion

        #region IEquatable<HotelListCellModel>

        public bool Equals(HotelListCellModel other)
        {
            return Name == other.Name && PhoneNumber == other.PhoneNumber && Address == other.Address;
        }

        #endregion
    }

    /// <summary>
    /// The view model for the hotel list view
    /// </summary>
    public sealed class HotelListViewModel : BaseViewModel<HotelListModel, HotelListViewModelParameter>
    {
        #region Variables

        private readonly HotelListViewModelParameter _param;

        private string _descriptionSearch;
        private string _locationSearch;

        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets the text to search for in the description / name
        /// of the hotel
        /// </summary>
        public string DescriptionSearch
        {
            get => _descriptionSearch;
            set => SetProperty(ref _descriptionSearch, value);
        }

        /// <summary>
        /// Gets the list of hotels matching the search criteria
        /// </summary>
        public ObservableCollection<HotelListCellModel> HotelsList { get; } = new ObservableCollection<HotelListCellModel>();

        /// <summary>
        /// Gets or sets the text to search for in the location / address
        /// of the hotel
        /// </summary>
        public string LocationSearch
        {
            get => _locationSearch;
            set => SetProperty(ref _locationSearch, value);
        }

        /// <summary>
        /// Gets the command to search for hotels matching the criteria in the view
        /// </summary>
        public ICommand LookupCommand => new Command(async () => await LookupAsync());

        /// <summary>
        /// Gets the command for toggling bookmarks
        /// </summary>
        public ICommand ToggleBookmarkCommand => new Command<HotelListCellModel>(ToggleBookmark);

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="parameter">The parameter used to initialize the view model</param>
        public HotelListViewModel(HotelListViewModelParameter parameter)
            : base(parameter)
        {
            _param = parameter;
        }

        #endregion

        #region Public Methods

        public void ShowDetails(HotelListCellModel hotel)
        {
            RequestNavigation(new HotelDetailViewModel(hotel.Source));
        }

        #endregion

        #region Private Methods

        private bool IsBookmarked(Hotel hotel)
        {
            return _param.BookmarkedHotels?.Any(x => x["id"] as string == hotel["id"] as string) ?? false;
        }

        private async Task LookupAsync()
        {
            Hotels hotels;
            try {
                
                hotels = await Model.FetchHotelsAsync(DescriptionSearch, LocationSearch);
            } catch (Exception e) {
                Debug.WriteLine($"Error fetching hotels: {e}");
                await UserDialogs.Instance.AlertAsync($"Failed to fetch hotels: {e.Message}", "Error");
                return;
            }
            
            HotelsList.Clear();
            foreach (var hotel in hotels.Select(x => new HotelListCellModel(x))) {
                hotel.IsBookmarked = IsBookmarked(hotel.Source);
                HotelsList.Add(hotel);
            }
        }

        private void ToggleBookmark(HotelListCellModel hotel)
        {
            try {
                Model.ToggleBookmark(hotel);
            } catch (Exception e) {
                Debug.WriteLine($"Error toggling bookmark: {e}");
                UserDialogs.Instance.Alert($"Failed to toggle bookmark: {e.Message}", "Error");
                return;
            }

            hotel.IsBookmarked = !hotel.IsBookmarked;
        }

        #endregion
    }

    public sealed class DesignHotelListViewModel
    {
        #region Properties

        public ObservableCollection<HotelListCellModel> HotelsList { get; } =
            new ObservableCollection<HotelListCellModel>();

        #endregion

        #region Constructors

        public DesignHotelListViewModel()
        {
            HotelsList.Add(new HotelListCellModel(new Dictionary<string, object> {
                ["name"] = "The Awesome Hotel",
                ["address"] = "2748 Mulberry Lane, London, England",
                ["phone"] = "+44 1632 960015"
            }) {
                IsBookmarked = true
            });
        }

        #endregion
    }
}