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
using System.Linq;
using Android.Widget;
using TravelSample.Core.Pages;
using TravelSample.Droid.Renderers;
using Xamarin.Forms;
using Xamarin.Forms.Platform.Android;
using TextChangedEventArgs = Android.Text.TextChangedEventArgs;

[assembly: ExportRenderer(typeof(AutoCompleteView), typeof(AutoCompleteViewRenderer))]
namespace TravelSample.Droid.Renderers
{
    /// <summary>
    /// The class that renders the AutoCompleteView on Android
    /// </summary>
    public class AutoCompleteViewRenderer : Xamarin.Forms.Platform.Android.AppCompat.ViewRenderer<AutoCompleteView, AutoCompleteTextView>
    {
        #region Private Methods

        private void OnTextChanged(object sender, TextChangedEventArgs e)
        {
            Element.Text = Control.Text;
        }

        #endregion

        #region Overrides

        protected override void OnElementChanged(ElementChangedEventArgs<AutoCompleteView> e)
        {
            base.OnElementChanged(e);

            if (Element == null) {
                return;
            }

            if (Control == null) {
                SetNativeControl(new AutoCompleteTextView(Context));
                Control.TextChanged += OnTextChanged;
            }
            
            Control.Hint = e.NewElement.Placeholder;
            Control.Adapter = new ArrayAdapter(Context, Android.Resource.Layout.SimpleExpandableListItem1, e.NewElement.Suggestions.Cast<string>().ToArray());
        }

        protected override void OnElementPropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            base.OnElementPropertyChanged(sender, e);

            if (e.PropertyName == "Suggestions") {
                Control.Adapter = new ArrayAdapter(Context, Android.Resource.Layout.SimpleExpandableListItem1,
                    Element.Suggestions.Cast<string>().ToArray());
            }
        }

        #endregion
    }
}