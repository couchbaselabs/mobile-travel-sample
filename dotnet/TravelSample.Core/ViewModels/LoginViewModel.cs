// 
// LoginViewModel.cs
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
using System.Threading.Tasks;
using System.Windows.Input;
using Acr.UserDialogs;
using MvvmCross.Core.Navigation;
using MvvmCross.Core.ViewModels;
using TravelSample.Core.Models;


namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// The view model for the login view
    /// </summary>
    public sealed class LoginViewModel : BaseViewModel<LoginModel>
    {
        #region Variables

        private readonly IMvxNavigationService _navigationService;
        private string _password;
        private string _username;

        /// <summary>
        /// An event that fires when a <see cref="CouchbaseSession"/> is started
        /// </summary>
        public static event EventHandler<CouchbaseSession> SessionStarted;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the command for logging in as a guest
        /// </summary>
        public ICommand GuestLoginCommand => new MvxAsyncCommand(GuestLoginAsync);

        /// <summary>
        /// Gets the command for logging in as a user
        /// </summary>
        public ICommand LoginCommand => new MvxAsyncCommand(LoginAsync);

        /// <summary>
        /// Gets whether or not the login button is enabled
        /// </summary>
        public bool LoginEnabled => !String.IsNullOrWhiteSpace(_username) && !String.IsNullOrWhiteSpace(_password);

        /// <summary>
        /// Gets or sets the password to login with
        /// </summary>
        public string Password
        {
            get => _password;
            set {
                SetProperty(ref _password, value);
                RaisePropertyChanged(nameof(LoginEnabled));
            }
        }

        /// <summary>
        /// Gets or sets the username to login with
        /// </summary>
        public string Username
        {
            get => _username;
            set {
                SetProperty(ref _username, value);
                RaisePropertyChanged(nameof(LoginEnabled));
            }
        }

        #endregion

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="navigationService">The injected navigation service</param>
        public LoginViewModel(IMvxNavigationService navigationService)
            : base(new LoginModel())
        {
            _navigationService = navigationService;
        }

        #endregion

        #region Private Methods

        private async Task GuestLoginAsync()
        {
            await LoginAsync("guest", null);
        }

        private async Task LoginAsync()
        {
            if (String.IsNullOrWhiteSpace(_username) || String.IsNullOrWhiteSpace(_password)) {
                await UserDialogs.Instance.AlertAsync("Please enter both a username and password to continue", "Error");
                return;
            }

            await LoginAsync(_username, _password);
        }

        private async Task LoginAsync(string username, string password)
        {
            CouchbaseSession session;
            try {
                session = await Model.StartSessionAsync(username, password);
            } catch (Exception e) {
                Debug.WriteLine($"Error creating database: {e}");
                await UserDialogs.Instance.AlertAsync(e.Message, "Error Creating Database");
                return;
            }

            Password = null;
            SessionStarted?.Invoke(this, session);
            if (session.IsGuest) {
                await _navigationService.Navigate<BookmarkedHotelViewModel, CouchbaseSession>(session);
            } else {
                await _navigationService.Navigate<FlightBookingViewModel, CouchbaseSession>(session);
            }
        }

        #endregion
    }
}