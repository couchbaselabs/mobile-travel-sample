// 
// AutoCompleteViewRenderer.cs
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
using System.ComponentModel;
using TravelSample.Core.Pages;
using TravelSample.UWP.Renderers;
using Windows.UI.Xaml.Controls;
using Xamarin.Forms.Platform.UWP;

[assembly: ExportRenderer(typeof(AutoCompleteView), typeof(AutoCompleteViewRenderer))]
namespace TravelSample.UWP.Renderers
{
    /// <summary>
    /// A class for rendering <see cref="AutoCompleteView"/> using <see cref="AutoSuggestBox"/>
    /// </summary>
    public class AutoCompleteViewRenderer : ViewRenderer<AutoCompleteView, AutoSuggestBox>
    {
        #region Private Methods

        private void OnTextChanged(AutoSuggestBox sender, AutoSuggestBoxTextChangedEventArgs args)
        {
            if (args.CheckCurrent()) {
                Element.Text = Control.Text;
            }
        }

        #endregion

        #region Overrides

        protected override void OnElementChanged(ElementChangedEventArgs<AutoCompleteView> e)
        {
            base.OnElementChanged(e);

            if (Control == null) {
                SetNativeControl(new AutoSuggestBox());
            }

            Control.TextChanged += OnTextChanged;
            Control.PlaceholderText = e.NewElement.Placeholder;
            Control.ItemsSource = e.NewElement.Suggestions;
        }

        protected override void OnElementPropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            base.OnElementPropertyChanged(sender, e);

            if (e.PropertyName == "Suggestions") {
                Control.ItemsSource = Element.Suggestions;
            }
        }

        #endregion
    }
}