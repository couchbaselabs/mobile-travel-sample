// 
// StringToImageSourceValueConverter.cs
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
using System.IO;
using Windows.ApplicationModel;
using Windows.Storage;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Media.Imaging;

namespace TravelSample.UWP.Services
{
    /// <summary>
    /// An <see cref="IValueConverter"/> that converts from <see cref="String"/>
    /// to <see cref="Windows.UI.Xaml.Media.ImageSource"/>
    /// </summary>
    public class StringToImageSourceValueConverter : IValueConverter
    {
        #region IValueConverter

        public object Convert(object value, Type targetType, object parameter, string language)
        {
            if (!(value is string str)) {
                return null;
            }

            var pathToAsset = Path.Combine(Package.Current.InstalledLocation.Path, "Assets", str);
            var file = StorageFile.GetFileFromPathAsync(pathToAsset).AsTask().ConfigureAwait(false).GetAwaiter().GetResult();
            using (var fileStream = file.OpenReadAsync().AsTask().ConfigureAwait(false).GetAwaiter().GetResult()) {
                var bitmapImage = new BitmapImage();
                bitmapImage.SetSource(fileStream);
                return bitmapImage;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, string language)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}