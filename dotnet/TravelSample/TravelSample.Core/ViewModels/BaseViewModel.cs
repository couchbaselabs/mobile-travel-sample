// 
// BaseViewModel.cs
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
using TravelSample.Core.Util;

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// An abstract base model that can request navigation changes
    /// </summary>
    public abstract class BaseViewModel : NotifyPropertyChanged
    {
        #region Variables

        /// <summary>
        /// An event fired when the view model has requested a backwards navigation
        /// </summary>
        public event EventHandler BackRequested;

        /// <summary>
        /// An event fired when the view model has requested navigation to a new
        /// view model
        /// </summary>
        public event EventHandler<BaseViewModel> NavigationRequested;

        #endregion

        #region Protected Methods

        protected void RequestBack()
        {
            BackRequested?.Invoke(this, EventArgs.Empty);
        }

        protected void RequestNavigation(BaseViewModel nextViewModel)
        {
            NavigationRequested?.Invoke(this, nextViewModel);
        }

        #endregion
    }

    /// <summary>
    /// An abstract base class for a view model that understands what kind
    /// of model it has
    /// </summary>
    /// <typeparam name="TModel">The type of model that this view model consumes</typeparam>
    public abstract class BaseViewModel<TModel> : BaseViewModel
    {
        #region Properties

        protected TModel Model { get; }

        #endregion

        #region Constructors

        protected BaseViewModel()
        {
            Model = Activator.CreateInstance<TModel>();
        }

        protected BaseViewModel(TModel model)
        {
            Model = model;
        }

        #endregion
    }

    /// <summary>
    /// An abstract base class for a view model that understands both what kind of
    /// model and what kind of parameter it has (the model must accept the same kind
    /// of parameter)
    /// </summary>
    /// <typeparam name="TModel">The type of model that this view model consumes</typeparam>
    /// <typeparam name="TParameter">The type of parameter that both this view model and its model consume</typeparam>
    public abstract class BaseViewModel<TModel, TParameter> : BaseViewModel<TModel>
    {
        #region Constructors

        protected BaseViewModel(TParameter param)
            : base((TModel) Activator.CreateInstance(typeof(TModel), param))
        {
        }

        #endregion
    }
}