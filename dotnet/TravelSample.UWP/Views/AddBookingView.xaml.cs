// 
// AddBookingView.xaml.cs
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
using Windows.UI.Xaml.Controls;
using MvvmCross.Uwp.Views;

namespace TravelSample.UWP.Views
{
    public
#if !DESIGN 
// Workaround for XAML designer unable to instantiate abstract class,
// but a non-abstract class throws an exception in MvvmCross
        abstract 
#endif
        class AddBookingPageAbstract : MvxWindowsPage<AddBookingViewModel>
    {}

    /// <summary>
    /// A view in which a booking can be added to a user's list
    /// </summary>
    public sealed partial class AddBookingView : AddBookingPageAbstract
    {
        #region Constructors

        /// <summary>
        /// Default constructor
        /// </summary>
        public AddBookingView()
        {
            InitializeComponent();
        }

        #endregion

        #region Private Methods

        private void AutoSuggestBox_SuggestionChosen(AutoSuggestBox sender, AutoSuggestBoxSuggestionChosenEventArgs args)
        {
            sender.Text = args.SelectedItem.ToString();
        }

        #endregion
    }
}
