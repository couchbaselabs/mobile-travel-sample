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
using System.Threading.Tasks;
using Foundation;
using TravelSample.Core.Services;

namespace TravelSample.iOS.Services
{
    /// <summary>
    /// An implementation of <see cref="IDatabaseSeedService"/> that reads the prebuilt
    /// database from the main application bundle
    /// </summary>
    public class DatabaseSeedService : IDatabaseSeedService
    {
        #region IDatabaseSeedService

        public async Task CopyDatabaseAsync(string directoryPath)
        {
            var finalPath = Path.Combine(directoryPath, "travel-sample.cblite2");
            Directory.CreateDirectory(finalPath);
            var sourcePath = Path.Combine(NSBundle.MainBundle.ResourcePath, "travel-sample.cblite2");
            var dirInfo = new DirectoryInfo(sourcePath);
            foreach (var file in dirInfo.EnumerateFiles()) {
                using(var inStream = File.OpenRead(file.FullName))
                using (var outStream = File.OpenWrite(Path.Combine(finalPath, file.Name))) {
                    await inStream.CopyToAsync(outStream);
                }
            }
        }

        #endregion
    }
}