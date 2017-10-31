// 
// HotelListPage.xaml.cs
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
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace TravelSample.Core.Pages
{
    /// <summary>
    /// A page showing a list of hotels matching a certain criteria
    /// </summary>
    [XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class HotelListPage : BasePage<HotelListViewModel>
	{
	    #region Constructors

        /// <summary>
        /// Default constructor (for design-time use)
        /// </summary>
	    public HotelListPage()
            : base(null)
	    {
	        InitializeComponent();
	        BindingContext = new DesignHotelListViewModel();
	    }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="viewModel">The view model to populate the page with</param>
	    public HotelListPage (HotelListViewModel viewModel)
            : base(viewModel)
		{
			InitializeComponent ();
		}

	    #endregion

	    #region Private Methods

	    private void OnItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            if (e.SelectedItem == null) {
                return;
            }

            ViewModel.ShowDetails(e.SelectedItem as HotelListCellModel);
            ((ListView)sender).SelectedItem = null;
        }

	    #endregion
	}
}