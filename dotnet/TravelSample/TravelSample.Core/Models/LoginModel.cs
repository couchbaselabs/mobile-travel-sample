// 
// LoginModel.cs
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
using System.Diagnostics;
using System.IO;
using System.Threading.Tasks;
using Couchbase.Lite;
using Couchbase.Lite.DI;
using Couchbase.Lite.Query;
using Couchbase.Lite.Sync;
using Microsoft.Extensions.DependencyInjection;
using TravelSample.Core.Services;

namespace TravelSample.Core.Models
{
    /// <summary>
    /// The model class for the login view
    /// </summary>
    public sealed class LoginModel
    {
        #region Constants

        private const string DbName = "travel-sample";
        private static readonly Uri SyncUrl = new Uri("blip://localhost:4984");

        #endregion

        #region Public Methods

        /// <summary>
        /// Starts a new session for the app
        /// </summary>
        /// <param name="username">The username to start the session with</param>
        /// <param name="password">The password to start the session with</param>
        /// <returns>An <c>await</c>able <see cref="Task"/> which contains the resulting session</returns>
        public async Task<CouchbaseSession> StartSessionAsync(string username, string password)
        {
            var isGuest = username == "guest";
            var options = new DatabaseConfiguration();
            
            // Borrow this functionality from Couchbase Lite
            var defaultDirectory = Service.Provider.GetService<IDefaultDirectoryResolver>().DefaultDirectory();
            var userFolder = Path.Combine(defaultDirectory, username);
            if (!Directory.Exists(userFolder)) {
                Directory.CreateDirectory(userFolder);
            }
            
            options.Directory = userFolder;
            Database db;
            Debug.WriteLine($"Will open/create DB at path {userFolder}");
            if (!Database.Exists(DbName, userFolder)) {
                // Load prebuilt database to path
                var copier = Service.Provider.GetService<IDatabaseSeedService>();
                await copier.CopyDatabaseAsync(userFolder);

                db = new Database(DbName, options);
                CreateDatabaseIndexes(db);
            } else {
                db = new Database(DbName, options);
            }

            var repl = isGuest ? default(Replicator) : StartReplication(username, password, db);
            return new CouchbaseSession(db, repl, username);
        }

        #endregion

        #region Private Methods

        private void CreateDatabaseIndexes(Database db)
        {
            // For searches on type property
            db.CreateIndex("type", Index.ValueIndex(ValueIndexItem.Expression(Expression.Property("type"))));

            // For full text searches on airports and hotels
            db.CreateIndex("airportName",
                Index.FTSIndex(FTSIndexItem.Expression(Expression.Property("airportname"))));
            db.CreateIndex("description",
                Index.FTSIndex(FTSIndexItem.Expression(Expression.Property("description"))));
            db.CreateIndex("name", 
                Index.FTSIndex(FTSIndexItem.Expression(Expression.Property("name"))));
        }

        private Replicator StartReplication(string username, string password, Database db)
        {
            if (String.IsNullOrWhiteSpace(username) || String.IsNullOrWhiteSpace(password)) {
                throw new InvalidOperationException("User credentials not provided");
            }

            var dbUrl = new Uri(SyncUrl, DbName);
            var config = new ReplicatorConfiguration(db, dbUrl) {
                ReplicatorType = ReplicatorType.PushAndPull,
                Continuous = true,
                Authenticator = new BasicAuthenticator(username, password),
                Channels = new[] {$"channel.{username}"}
            };

            var repl = new Replicator(config);
            repl.AddChangeListener((sender, args) =>
            {
                var s = args.Status;
                Debug.WriteLine(
                    $"PushPull Replicator: {s.Progress.Completed}/{s.Progress.Total}, error {s.Error?.Message ?? "<none>"}, activity = {s.Activity}");

            });

            repl.Start();
            return repl;
        }

        #endregion
    }
}