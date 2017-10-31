// 
// FlightBookingPage.xaml.cs
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
    /// A page that shows a list of bookings that a user has created
    /// </summary>
    [XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class FlightBookingPage : BasePage<FlightBookingViewModel>
	{
	    #region Constructors

        /// <summary>
        /// Default constructor (for design-time viewing)
        /// </summary>
	    public FlightBookingPage()
            : base(null)
	    {
	        InitializeComponent();
	        BindingContext = new DesignFlightBookingViewModel();
	    }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="viewModel">The view model to populate the page with</param>
	    public FlightBookingPage(FlightBookingViewModel viewModel)
            : base(viewModel)
		{
			InitializeComponent ();

            FileImageSource hotelsImage = null;
		    FileImageSource addImage = null;
		    switch (Device.RuntimePlatform) {
		        case Device.iOS:
		            hotelsImage = new FileImageSource {
		                File = "feature-search"
		            };

		            addImage = new FileImageSource {
		                File = "add"
		            };
                    break;
                case Device.Android:
		            hotelsImage = new FileImageSource {
		                File = "feature_search.png"
		            };

		            addImage = new FileImageSource {
		                File = "add.png"
		            };
                    break;
                case Device.UWP:
                    hotelsImage = new FileImageSource {
                        File = "Assets/feature.search.png"
                    };

                    addImage = new FileImageSource {
                        File = "Assets/add.png"
                    };
                    break;
		    }

		    var hotelsIcon = new ToolbarItem {
		        Text = "Hotels",
                Icon = hotelsImage,
		        Command = ViewModel.ShowHotelsCommand
		    };

		    var addBookingIcon = new ToolbarItem {
                Text = "Add",
                Icon = addImage,
		        Command = ViewModel.AddBookingCommand
		    };

		    ToolbarItems.Add(hotelsIcon);
		    ToolbarItems.Add(addBookingIcon);
		}

	    #endregion

	    #region Overrides

	    protected override async void OnAppearing()
        {
            base.OnAppearing();

            await ViewModel?.Refresh();
        }

	    protected override void OnParentSet()
	    {
	        base.OnParentSet();

	        if (Navigation.NavigationStack.Count <= 1) {
	            // HACK: Intercept navigation back to login screen
	            ViewModel.Dispose();
	        }
	    }

	    #endregion
	}
}