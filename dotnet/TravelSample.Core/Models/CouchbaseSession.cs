// 
// CouchbaseSession.cs
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
using System.Linq;
using System.Net.Http;
using System.Threading;
using Couchbase.Lite;
using Couchbase.Lite.Query;
using Couchbase.Lite.Sync;
using static TravelSample.Core.Util.Constants;

namespace TravelSample.Core.Models
{
    /// <summary>
    /// A class for holding session state that is shared between views
    /// </summary>
    public sealed class CouchbaseSession : IDisposable
    {
        #region Constants

        private static readonly ISelectResult DocIdResult = SelectResult.Expression(Expression.Meta().ID);

        #endregion

        #region Variables

        private readonly Lazy<string> _userDocID;

        /// <summary>
        /// An event fired when this session is ended
        /// </summary>
        public event EventHandler Ended;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the user's <see cref="Database"/>
        /// </summary>
        public Database Database { get; }

        /// <summary>
        /// Gets the session <see cref="HttpClient"/> for sending
        /// HTTP requests
        /// </summary>
        public HttpClient HttpClient { get; } = new HttpClient();

        /// <summary>
        /// Gets whether or not this is a guest session
        /// </summary>
        public bool IsGuest => Username == "guest";

        /// <summary>
        /// Gets the <see cref="Replicator"/> being used in this session
        /// </summary>
        public Replicator Replicator { get; }

        /// <summary>
        /// Gets the document ID for the user's user document (created when
        /// the user is created via the web app)
        /// </summary>
        public string UserDocID => _userDocID.Value;

        /// <summary>
        /// Gets the current user's Username
        /// </summary>
        public string Username { get; }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="db">The user's database</param>
        /// <param name="replicator">The user's replicator (already started)</param>
        /// <param name="username">The user's username</param>
        public CouchbaseSession(Database db, Replicator replicator, string username)
        {
            Database = db;
            Replicator = replicator;
            Username = username;
            _userDocID = new Lazy<string>(GetUserDocID, LazyThreadSafetyMode.None); 
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Gets the guest bookmark document (only applicable to the guest user)
        /// </summary>
        /// <returns>The guest bookmark document</returns>
        public Document FetchGuestBookmarkDocument()
        {
            if (!IsGuest) {
                throw new InvalidOperationException("This method is only for the guest user");
            }

            using (var searchQuery = Query
                .Select(DocIdResult)
                .From(DataSource.Database(Database))
                .Where(TypeProperty.EqualTo("bookmarkedhotels"))) {
                using (var results = searchQuery.Run()) {
                    var docID = results.FirstOrDefault()?.GetString("id");
                    return docID != null ? Database.GetDocument(docID) : null;
                }
            }
        }

        #endregion

        #region Private Methods

        private string GetUserDocID()
        {
            using (var userQuery = Query
                .Select(DocIdResult)
                .From(DataSource.Database(Database))
                .Where(UsernameProperty.EqualTo(Username))) {
                using (var results = userQuery.Run()) {
                    return results.FirstOrDefault()?.GetString("id");
                }
            }
        }

        #endregion

        #region IDisposable

        /// <inheritdoc />
        public void Dispose()
        {
            Ended?.Invoke(this, null);
            Database?.Dispose();
            Replicator?.Stop();
            // Uncomment after DB019 (https://github.com/couchbase/couchbase-lite-net/issues/908)
            //_replicator?.Dispose();
            HttpClient.Dispose();
        }

        #endregion
    }
}