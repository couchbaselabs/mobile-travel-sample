// 
// AutoCompleteView.cs
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
using System.Collections;
using Xamarin.Forms;

namespace TravelSample.Core.Pages
{
    /// <summary>
    /// A text view that will pop up suggestions
    /// </summary>
    /// <remarks>
    /// Doesn't function on iOS currently
    /// </remarks>
    public sealed class AutoCompleteView : View
    {
        #region Constants

        public static readonly BindableProperty PlaceholderProperty = BindableProperty.Create(nameof(Placeholder),
            typeof(string), typeof(AutoCompleteView), String.Empty, propertyChanged: OnPlaceholderChanged);

        public static readonly BindableProperty SuggestionsProperty = BindableProperty.Create(nameof(Suggestions),
            typeof(IEnumerable), typeof(AutoCompleteView), propertyChanged: OnSuggestionsChanged);

        public static readonly BindableProperty TextProperty = BindableProperty.Create(nameof(Text),
            typeof(string), typeof(AutoCompleteView), String.Empty, propertyChanged: OnTextChanged);

        #endregion

        #region Properties

        public string Placeholder
        {
            get => (string) GetValue(PlaceholderProperty);
            set => SetValue(PlaceholderProperty, value);
        }

        public IEnumerable Suggestions
        {
            get => (IEnumerable)GetValue(SuggestionsProperty);
            set => SetValue(SuggestionsProperty, value);
        }

        public string Text
        {
            get => (string) GetValue(TextProperty);
            set => SetValue(TextProperty, value);
        }

        #endregion

        #region Private Methods

        private static void OnPlaceholderChanged(BindableObject bindable, object value, object newValue)
        {
            ((AutoCompleteView)bindable).Placeholder = (string)newValue;
        }

        private static void OnSuggestionsChanged(BindableObject bindable, object value, object newValue)
        {
            ((AutoCompleteView)bindable).Suggestions = (IEnumerable)newValue;
        }

        private static void OnTextChanged(BindableObject bindable, object value, object newValue)
        {
            ((AutoCompleteView) bindable).Text = (string)newValue;
        }

        #endregion
    }
}