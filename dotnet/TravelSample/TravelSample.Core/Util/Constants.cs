// 
// Constants.cs
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
using System.Globalization;
using Couchbase.Lite.Query;

namespace TravelSample.Core.Util
{
    public static class Constants
    {
        #region Constants

        /// <summary>
        /// The Url of the backend server (which has API for searching for hotels, flights,
        /// and creating users, etc)
        /// </summary>
        public static readonly Uri ServerBackendUrl = new Uri("http://192.168.1.3:8080/api/");

        /// <summary>
        /// An expression for getting the type keypath of a document
        /// </summary>
        public static readonly IExpression TypeProperty = Expression.Property("type");

        /// <summary>
        /// The US culture info (for formatting the price in dollars, regardless of UI locale)
        /// </summary>
        public static readonly CultureInfo UsCulture = new CultureInfo("en-US");

        /// <summary>
        /// An expression for getting the username keypath of a document
        /// </summary>
        public static readonly IExpression UsernameProperty = Expression.Property("username");

        #endregion
    }
}