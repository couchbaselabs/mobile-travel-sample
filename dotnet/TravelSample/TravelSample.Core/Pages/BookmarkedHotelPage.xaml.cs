// 
// BookmarkedHotelPage.xaml.cs
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
	/// A page that shows a list of bookmarked hotels (guest user)
	/// </summary>
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class BookmarkedHotelPage : BasePage<BookmarkedHotelViewModel>
	{
		#region Constructors

		/// <summary>
		/// Default constructor (for design-time viewing)
		/// </summary>
		public BookmarkedHotelPage()
			: base(null)
		{
			InitializeComponent();
			BindingContext = new DesignBookmarkedHotelViewModel();
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="model">The view model to use for this page's data</param>
		public BookmarkedHotelPage (BookmarkedHotelViewModel model)
			: base(model)
		{
			InitializeComponent ();
			FileImageSource icon = null;
			switch (Device.RuntimePlatform) {
				case Device.iOS:
					icon = new FileImageSource {
						File = "feature-search"
					};
					break;
				case Device.Android:
					icon = new FileImageSource {
						File = "feature_search.png"
					};
					break;
				case Device.UWP:
					icon = new FileImageSource {
						File = "Assets/feature.search.png"
					};
					break;
			}

			var hotelIcon = new ToolbarItem() {
				Text = "Hotels",
				Icon = icon,
				Command = ViewModel.ShowHotelsCommand
			};

			ToolbarItems.Add(hotelIcon);
		}

		#endregion

		#region Overrides

		protected override void OnAppearing()
		{
			base.OnAppearing();

			ViewModel?.Refresh();
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