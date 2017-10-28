// 
// BookmarkedHotelView.xaml.cs
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
using TravelSample.Core.ViewModels;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Input;
using MvvmCross.Uwp.Views;

namespace TravelSample.UWP.Views
{
    public
#if !DESIGN 
// Workaround for XAML designer unable to instantiate abstract class,
// but a non-abstract class throws an exception in MvvmCross
        abstract 
#endif
        class BookmarkedHotelPageAbstract : MvxWindowsPage<BookmarkedHotelViewModel>
    {}

    /// <summary>
    /// A view that shows a list of hotels that the guest user bookmarked
    /// </summary>
    public sealed partial class BookmarkedHotelView : BookmarkedHotelPageAbstract
    {
        #region Constructors

        /// <summary>
        /// Default Constructor
        /// </summary>
        public BookmarkedHotelView()
        {
            InitializeComponent();
        }

        #endregion

        #region Private Methods

        private void OnRightTap(object sender, RightTappedRoutedEventArgs e)
        {
            // Make sure that the correct row gets passed to the view model
            var listView = (ListView) sender;
            CellFlyout.Items[0].DataContext = ((FrameworkElement) e.OriginalSource).DataContext;
            (CellFlyout.Items[0] as MenuFlyoutItem).Command = ViewModel.RemoveBookmarkCommand;
            (CellFlyout.Items[0] as MenuFlyoutItem).CommandParameter =
                ((FrameworkElement) e.OriginalSource).DataContext;
            CellFlyout.ShowAt(listView, e.GetPosition(listView));
        }

        #endregion
    }
}
