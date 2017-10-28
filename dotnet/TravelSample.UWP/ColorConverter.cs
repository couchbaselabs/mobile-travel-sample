// 
// ColorConverter.cs
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
using MvvmCross.Platform;
using MvvmCross.Platform.UI;
using Windows.UI;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Media;

namespace TravelSample.UWP.Converters
{
    /// <summary>
    /// An <see cref="IValueConverter"/> that wraps <see cref="IMvxNativeColor"/> (the default one
    /// doesn't seem to work with UWP since it does not implement <see cref="IValueConverter"/>)
    /// </summary>
    public class ColorConverter : IValueConverter
    {
        #region IValueConverter

        public object Convert(object value, Type targetType, object parameter, string language)
        {
            if (value == null) {
                return new SolidColorBrush(Colors.Black);
            }
            var native = Mvx.Resolve<IMvxNativeColor>().ToNative((MvxColor) value);
            return native;
        }

        public object ConvertBack(object value, Type targetType, object parameter, string language)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}