// 
// BaseModel.cs
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
namespace TravelSample.Core.Models
{
    /// <summary>
    /// An abstract base class for models that contain a specific type
    /// of parameter.  This eliminates a lot of boiler plate code involving
    /// passing data between view models during navigation.
    /// </summary>
    /// <typeparam name="TParameter">The type of parameter that this model holds</typeparam>
    public abstract class BaseModel<TParameter>
    {
        #region Variables

        protected readonly TParameter _param;

        #endregion

        #region Constructors

        protected BaseModel(TParameter param)
        {
            _param = param;
        }

        #endregion
    }
}