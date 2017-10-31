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
using System;
using Foundation;
using TravelSample.Core.Pages;
using TravelSample.iOS.Renderers;
using UIKit;
using Xamarin.Forms;
using Xamarin.Forms.Platform.iOS;

[assembly: ExportRenderer(typeof(AutoCompleteView), typeof(AutoCompleteViewRenderer))]
namespace TravelSample.iOS.Renderers
{
    /// <summary>
    /// A renderer for the AutoCompleteView
    /// </summary>
    /// <remarks>
    /// Not currently implemented; simply displays a text view
    /// </remarks>
    public class AutoCompleteViewRenderer : ViewRenderer<AutoCompleteView, UITextField>
    {
        #region Variables

        private IDisposable _notificationObserver;

        #endregion

        #region Private Methods

        private void TextChanged(NSNotification obj)
        {
            Element.Text = Control.Text;
        }

        #endregion

        #region Overrides

        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);

            _notificationObserver?.Dispose();
        }

        protected override void OnElementChanged(ElementChangedEventArgs<AutoCompleteView> e)
        {
            base.OnElementChanged(e);

            if (Control == null) {
                SetNativeControl(new UITextField() {
                    BorderStyle = UITextBorderStyle.RoundedRect
                });

                _notificationObserver = NSNotificationCenter.DefaultCenter.AddObserver(UITextField.TextFieldTextDidChangeNotification, Control,
                    null, TextChanged);
            }

            Control.Placeholder = e.NewElement.Placeholder;
            Control.Text = e.NewElement.Text ?? String.Empty;
        }

        #endregion
    }
}