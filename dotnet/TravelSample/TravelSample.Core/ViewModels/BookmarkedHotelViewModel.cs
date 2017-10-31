// 
// BookmarkedHotelViewModel.cs
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
using System.Windows.Input;
using Acr.UserDialogs;
using TravelSample.Core.Models;
using Xamarin.Forms;

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// The view model for a list of bookmarked hotels
    /// </summary>
    public sealed class BookmarkedHotelViewModel : BaseViewModel<BookmarkedHotelModel, CouchbaseSession>, IDisposable
    {
        #region Properties

        /// <summary>
        /// Gets the list of bookmarked hotels
        /// </summary>
        public ObservableCollection<HotelListCellModel> HotelsList { get; } =
            new ObservableCollection<HotelListCellModel>();

        /// <summary>
        /// Gets the command to remove a bookmark from the bookmarks list
        /// </summary>
        public ICommand RemoveBookmarkCommand => new Command<HotelListCellModel>(RemoveBookmark);

        /// <summary>
        /// Gets the command to navigate to the hotel list view
        /// </summary>
        public ICommand ShowHotelsCommand => new Command(ShowHotels);

        #endregion

        #region Constructors

        /// <summary>
        /// Constructors
        /// </summary>
        /// <param name="session">The current user session</param>
        public BookmarkedHotelViewModel(CouchbaseSession session)
            : base(session)
        {
        }

        #endregion

        #region Public Methods

        public void Refresh()
        {
            // Refresh the list of hotels, because they might have changed
            // in the hotel list view (this could also be accomplished with live
            // query, as with the flight booking view)
            try {
                var hotels = Model.FetchBookmarkedHotels();
                HotelsList.Clear();
                foreach (var h in hotels) {
                    HotelsList.Add(new HotelListCellModel(h));
                }
            } catch (Exception e) {
                Debug.WriteLine($"Failed to fetch bookmarked hotels: {e}");
                UserDialogs.Instance.Alert($"Failed to fetch bookmarked hotels: {e.Message}", "Error");
            }
        }

        #endregion

        #region Private Methods

        private void RemoveBookmark(HotelListCellModel bookmark)
        {
            try {
                Model.RemoveBookmark(bookmark);
            } catch (Exception e) {
                Debug.WriteLine($"Failed to remove bookmark: {e}");
                UserDialogs.Instance.Alert($"Failed to remove bookmark: {e.Message}", "Error");
                return;
            }

            HotelsList.Remove(bookmark);
        }

        private void ShowHotels()
        {
            RequestNavigation(new HotelListViewModel(new HotelListViewModelParameter(Model.UserSession, true,
                HotelsList.Select(x => x.Source).ToList())));
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
    public sealed class DesignBookmarkedHotelViewModel
    {
        public ObservableCollection<HotelListCellModel> HotelsList { get; } =
            new ObservableCollection<HotelListCellModel>();

        public DesignBookmarkedHotelViewModel()
        {
            HotelsList.Add(new HotelListCellModel(new Dictionary<string, object> {
                ["name"] = "The Awesome Hotel",
                ["address"] = "2748 Mulberry Lane, London, England",
                ["phone"] = "+44 1632 960015"
            }) {
                IsBookmarked = true
            });
        }
    }
}