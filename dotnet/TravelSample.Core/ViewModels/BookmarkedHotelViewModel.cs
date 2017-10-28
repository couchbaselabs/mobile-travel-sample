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
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Windows.Input;
using Acr.UserDialogs;
using MvvmCross.Core.Navigation;
using MvvmCross.Core.Navigation.EventArguments;
using MvvmCross.Core.ViewModels;
using TravelSample.Core.Models;

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// The view model for a list of bookmarked hotels
    /// </summary>
    public sealed class BookmarkedHotelViewModel : BaseViewModel<BookmarkedHotelModel, CouchbaseSession>
    {
        #region Variables

        private readonly IMvxNavigationService _navigationService;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the list of bookmarked hotels
        /// </summary>
        public ObservableCollection<HotelListCellModel> HotelsList { get; } =
            new ObservableCollection<HotelListCellModel>();

        /// <summary>
        /// Gets the command to remove a bookmark from the bookmarks list
        /// </summary>
        public ICommand RemoveBookmarkCommand => new MvxCommand<HotelListCellModel>(RemoveBookmark);

        /// <summary>
        /// Gets the command to navigate to the hotel list view
        /// </summary>
        public ICommand ShowHotelsCommand => new MvxCommand(() =>
            _navigationService.Navigate<HotelListViewModel, HotelListViewModelParameter>(
                new HotelListViewModelParameter(Model.UserSession, false, null)));

        #endregion

        #region Constructors

        /// <summary>
        /// Constructors
        /// </summary>
        /// <param name="navigationService">The injected navigation service</param>
        public BookmarkedHotelViewModel(IMvxNavigationService navigationService)
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

        private void RemoveBookmark(HotelListCellModel bookmark)
        {
            try {
                Model.RemoveBookmark(bookmark);
            } catch (Exception e) {
                Debug.WriteLine($"Failed to remove bookmark: {e}");
                UserDialogs.Instance.Alert($"Failed to remove bookmark: {e.Message}", "Error");
                return;
            }

            for (int i = bookmark.Index + 1; i < HotelsList.Count; i++) {
                HotelsList[i].Index--;
            }

            HotelsList.RemoveAt(bookmark.Index);
        }

        #endregion

        #region Overrides

        public override void ViewAppearing()
        {
            // Refresh the list of hotels, because they might have changed
            // in the hotel list view (this could also be accomplished with live
            // query, as with the flight booking view)
            base.ViewAppearing();

            try {
                var hotels = Model.FetchBookmarkedHotels();
                HotelsList.Clear();
                var i = 0;
                foreach (var h in hotels) {
                    HotelsList.Add(new HotelListCellModel(h, i++));
                }
            } catch (Exception e) {
                Debug.WriteLine($"Failed to fetch bookmarked hotels: {e}");
                UserDialogs.Instance.Alert($"Failed to fetch bookmarked hotels: {e.Message}", "Error");
            }
        }

        #endregion
    }
}