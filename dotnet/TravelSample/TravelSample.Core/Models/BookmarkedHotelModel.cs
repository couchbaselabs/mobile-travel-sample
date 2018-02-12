// 
// BookmarkedHotelModel.cs
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
using Couchbase.Lite.Query;
using TravelSample.Core.ViewModels;
using Hotels = System.Collections.Generic.List<System.Collections.Generic.IReadOnlyDictionary<string, object>>;

namespace TravelSample.Core.Models
{
    /// <summary>
    /// The model class for the bookmarked hotels view.
    /// </summary>
    public sealed class BookmarkedHotelModel : BaseModel<CouchbaseSession>, IDisposable
    {
        #region Constants

        private static readonly IExpression HotelsProperty = Expression.Property("hotels").From(BookmarkDbName);
        private static readonly IExpression HotelIdProperty = Meta.ID.From(HotelsDbName);
        private static readonly IExpression JoinExpression = ArrayFunction.Contains(HotelsProperty, HotelIdProperty);
        private static readonly IExpression TypeProperty = Expression.Property("type").From(BookmarkDbName);
        private static readonly ISelectResult AllBookmarks = SelectResult.All().From(BookmarkDbName);
        private static readonly ISelectResult AllHotels = SelectResult.All().From(HotelsDbName);
        private const string BookmarkDbName = "bookmarkSource";

        private const string HotelsDbName = "hotelsSource";

        #endregion

        #region Properties

        /// <summary>
        /// Gets the current user session
        /// </summary>
        public CouchbaseSession UserSession => _param;

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="param">The current user session</param>
        public BookmarkedHotelModel(CouchbaseSession param) : base(param)
        {
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Fetches the currently bookmarked hotels from the local database.  This only applies
        /// to guest users.
        /// </summary>
        /// <returns>A list of bookmarked hotels</returns>
        public Hotels FetchBookmarkedHotels()
        {
            var bookmarkedHotels = new Hotels();
            var bookmarkSource = DataSource.Database(UserSession.Database).As(BookmarkDbName);
            var hotelsSource = DataSource.Database(UserSession.Database).As(HotelsDbName);
            var join = Join.InnerJoin(hotelsSource).On(JoinExpression);

            using (var query = QueryBuilder
                .Select(AllBookmarks, AllHotels)
                .From(bookmarkSource)
                .Join(join)
                   .Where(TypeProperty.EqualTo(Expression.String("bookmarkedhotels")))) {

                var results = query.Execute().ToList();

                foreach (var result in results ){
                     bookmarkedHotels.Add(result.GetDictionary(HotelsDbName).ToDictionary(x => x.Key, x => x.Value));
                }

            }

            return bookmarkedHotels;
        }

        /// <summary>
        /// Removes the given hotel from the list of bookmarks
        /// </summary>
        /// <param name="bookmark">The item from the bookmarked hotels list to remove</param>
        public void RemoveBookmark(HotelListCellModel bookmark)
        {
            using (var document = UserSession.FetchGuestBookmarkDocument()?.ToMutable()) {
                if (document == null) {
                    throw new InvalidOperationException("Bookmark document not found");
                }

                var currentIds = document.GetArray("hotels");
                if (currentIds == null) {
                    throw new InvalidOperationException("Bookmark document contains no hotels entry");
                }

                for (var i = 0; i < currentIds.Count; i++) {
                    if (bookmark.Source["id"] as string == currentIds[i].ToString()) {
                        currentIds.RemoveAt(i);
                        break;
                    }
                }

                document.SetArray("hotels", currentIds);
                UserSession.Database.Save(document);
                if (bookmark.Source["id"] is string idToRemove) {
                    var doc = UserSession.Database.GetDocument(idToRemove);
                    if (doc != null) {
                        UserSession.Database.Delete(doc);
                    }
                }
            }
        }

        #endregion

        #region IDisposable

        public void Dispose()
        {
            UserSession.Dispose();
        }

        #endregion
    }
}