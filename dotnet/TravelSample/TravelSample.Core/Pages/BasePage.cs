// 
// BasePage.cs
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
using System.Diagnostics;
using TravelSample.Core.ViewModels;
using Xamarin.Forms;
using Xamarin.Forms.Internals;

namespace TravelSample.Core.Pages
{
    /// <summary>
    /// Empty abstract class for use with collections
    /// </summary>
    public abstract class BasePage : ContentPage
    {}

    /// <summary>
    /// An abstract base page that knows what type of view model it has
    /// </summary>
    /// <typeparam name="TViewModel">The type of view model that the page uses</typeparam>
    public class BasePage<TViewModel> : BasePage where TViewModel : BaseViewModel
    {
        #region Variables

        private readonly Dictionary<Type, Type> _viewModelToPage = new Dictionary<Type, Type>();
        private TViewModel _viewModel;

        #endregion

        #region Properties

        protected TViewModel ViewModel
        {
            get => _viewModel;
            set {
                if (_viewModel != null) {
                    _viewModel.NavigationRequested -= PerformNavigation;
                    _viewModel.BackRequested -= PerformBack;
                }

                _viewModel = value;
                BindingContext = value;

                if (_viewModel != null) {
                    _viewModel.NavigationRequested += PerformNavigation;
                    _viewModel.BackRequested += PerformBack;
                }
            }
        }

        #endregion

        #region Constructors

        protected BasePage()
        {
            ViewModel = Activator.CreateInstance<TViewModel>();
            _viewModelToPage[typeof(TViewModel)] = GetType();
        }

        protected BasePage(TViewModel viewModel)
        {
            ViewModel = viewModel;
            _viewModelToPage[typeof(TViewModel)] = GetType();
        }

        #endregion

        #region Private Methods

        private Type InferPageType(Type viewModelType)
        {
            if (!viewModelType.Name.EndsWith("ViewModel")) {
                return null;
            }

            var pageTypeName = viewModelType.FullName.Replace("ViewModel", "Page");
            var retVal = Type.GetType(pageTypeName, false);
            if (retVal != null) {
                _viewModelToPage[viewModelType] = retVal;
            }

            return retVal;
        }

        private void PerformBack(object sender, EventArgs e)
        {
            Navigation.PopAsync(true);
        }

        private void PerformNavigation(object sender, BaseViewModel e)
        {
            var pageType = default(Type);
            var viewModelType = e.GetType();
            if (!_viewModelToPage.TryGetValue(viewModelType, out pageType) &&
                (pageType = InferPageType(viewModelType)) == null) {
                Debug.WriteLine($"Page type not found for {viewModelType.Name}");
                return;
            }

            #if DEBUG
            if (!typeof(BasePage).IsAssignableFrom(pageType)) {
                throw new InvalidOperationException(
                    $"Registered type for {e.GetType().Name} is not a page type ({pageType.Name})");
            }
            #endif

            var page = (BasePage)Activator.CreateInstance(pageType, e);
            Navigation.PushAsync(page, true);
        }

        #endregion
    }
}