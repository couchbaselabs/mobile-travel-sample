// 
// DatabaseSeedService.cs
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
using System.Threading.Tasks;
using TravelSample.Core.Services;
using Windows.ApplicationModel;
using Windows.Storage;

namespace TravelSample.UWP.Services
{
    /// <summary>
    /// The implementation of <see cref="IDatabaseSeedService"/> that copies a prebuilt
    /// database from the Assets folder
    /// </summary>
    public sealed class DatabaseSeedService : IDatabaseSeedService
    {
        #region IDatabaseSeedService

        /// <inheritdoc />
        public async Task CopyDatabaseAsync(string directoryPath)
        {
            var finalPath = Path.Combine(directoryPath, "travel-sample.cblite2");
            Directory.CreateDirectory(finalPath);
            var destFolder = await StorageFolder.GetFolderFromPathAsync(finalPath);
            var assetsFolder = await Package.Current.InstalledLocation.GetFolderAsync("Assets\\travel-sample.cblite2");
            var filesList = await assetsFolder.GetFilesAsync();
            foreach (var file in filesList) {
                await file.CopyAsync(destFolder);
            }
        }

        #endregion
    }
}