// 
// BackgroundColorStyleSelector.cs
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
using TravelSample.Core.Util;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace TravelSample.UWP.Views
{
    /// <summary>
    /// A <see cref="StyleSelector" /> that chooses a background color between
    /// gray and white depending on whether a row in a list is at an even
    /// or odd index
    /// </summary>
    public sealed class BackgroundColorStyleSelector : StyleSelector
    {
        #region Properties

        /// <summary>
        /// Gets or sets the gray background object (from XAML)
        /// </summary>
        public Style GrayBackground { get; set; }

        /// <summary>
        /// Gets or sets the white background object (from XAML)
        /// </summary>
        public Style WhiteBackground { get; set; }

        #endregion

        #region Overrides

        /// <inheritdoc />
        protected override Style SelectStyleCore(object item, DependencyObject container)
        {
            var flightCell = (IIndexable)item;
            return (flightCell.Index & 1) == 1 ? GrayBackground : WhiteBackground;
        }

        #endregion
    }
}