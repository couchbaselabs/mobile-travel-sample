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
using System.IO;
using System.IO.Compression;
using System.Threading.Tasks;
using Android.Content;
using TravelSample.Core.Services;

namespace TravelSample.Droid.Services
{
    /// <summary>
    /// An implementation of <see cref="IDatabaseSeedService"/> that reads a prebuilt
    /// database from Android Assets
    /// </summary>
    public class DatabaseSeedService : IDatabaseSeedService
    {
        #region Variables

        private readonly Context _context;

        #endregion

        #region Constructors

        public DatabaseSeedService(Context context)
        {
            _context = context;
        }

        #endregion

        #region IDatabaseSeedService

        public async Task CopyDatabaseAsync(string directoryPath)
        {
            Directory.CreateDirectory(directoryPath);

            var assetStream = _context.Assets.Open("travel-sample.zip");
            using (var archive = new ZipArchive(assetStream, ZipArchiveMode.Read)) {
                foreach (var entry in archive.Entries) {
                    var entryPath = Path.Combine(directoryPath, entry.FullName);
                    if (entryPath.EndsWith("/")) {
                        Directory.CreateDirectory(entryPath);
                    } else {
                        using (var entryStream = entry.Open())
                        using (var writeStream = File.OpenWrite(entryPath)) {
                            await entryStream.CopyToAsync(writeStream).ConfigureAwait(false);
                        }
                    }
                }
            }
        }

        #endregion
    }
}