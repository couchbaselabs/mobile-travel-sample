// 
// HotelListView.xaml.cs
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
using MvvmCross.Uwp.Views;
using TravelSample.Core.ViewModels;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Input;

namespace TravelSample.UWP.Views
{
    public
#if !DESIGN 
// Workaround for XAML designer unable to instantiate abstract class,
// but a non-abstract class throws an exception in MvvmCross
        abstract 
#endif
        class HotelListPageAbstract : MvxWindowsPage<HotelListViewModel>
    {}

    /// <summary>
    /// A view that shows a list of hotels matching criteria that can be specified
    /// </summary>
    public sealed partial class HotelListView : HotelListPageAbstract
    {
        #region Constructors

        /// <summary>
        /// Default Constructor
        /// </summary>
        public HotelListView()
        {
            InitializeComponent();
        }

        #endregion

        #region Private Methods

        private void OnItemClicked(object sender, ItemClickEventArgs e)
        {
            ViewModel.ShowHotelDetail(e.ClickedItem as HotelListCellModel);
        }

        private void OnRightTap(object sender, RightTappedRoutedEventArgs e)
        {
            // Make sure that the correct row is passed to the view model
            var listView = (ListView) sender;
            CellFlyout.Items[0].DataContext = ((FrameworkElement) e.OriginalSource).DataContext;
            (CellFlyout.Items[0] as MenuFlyoutItem).Command = ViewModel.ToggleBookmarkCommand;
            (CellFlyout.Items[0] as MenuFlyoutItem).CommandParameter = ((FrameworkElement) e.OriginalSource).DataContext;
            CellFlyout.ShowAt(listView, e.GetPosition(listView));
        }

        #endregion
    }
}
