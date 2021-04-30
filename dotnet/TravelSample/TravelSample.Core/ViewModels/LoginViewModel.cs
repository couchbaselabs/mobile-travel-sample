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
using TravelSample.Core.Models;
using Xamarin.Forms;

namespace TravelSample.Core.ViewModels
{
    /// <summary>
    /// The view model for the login view
    /// </summary>
    public sealed class LoginViewModel : BaseViewModel<LoginModel>
    {
        #region Variables
        
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
        public ICommand GuestLoginCommand => new Command(async () => await GuestLoginAsync());

        /// <summary>
        /// Gets the command for logging in as a user
        /// </summary>
        public ICommand LoginCommand => new Command(async () => await LoginAsync());

        /// <summary>
        /// Gets whether or not the login button is enabled
        /// </summary>
        public bool LoginEnabled => !String.IsNullOrWhiteSpace(_username) && !String.IsNullOrWhiteSpace(_password);

        public ImageSource Logo
        {
            get {
                switch (Device.RuntimePlatform) {
                    case Device.iOS:
                        return ImageSource.FromFile("CBTravel.LOGO");
                    case Device.Android:
                        return ImageSource.FromFile("cbtravel_logo.png");
                    case Device.UWP:
                        return ImageSource.FromFile("Assets/CBTravel.LOGO.png");
                }

                return null;
            }
        }

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

        #region Private Methods

        private async Task GuestLoginAsync()
        {
            await LoginAsync("guest", null);
        }

        private async Task LoginAsync()
        {
            if (String.IsNullOrWhiteSpace(_username) || String.IsNullOrWhiteSpace(_password)) {
                await Application.Current.MainPage.DisplayAlert("Error", "Please enter both a username and password to continue", "OK");
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
                await Application.Current.MainPage.DisplayAlert("Error", $"Error Creating Database, {e.Message}", "OK");
                return;
            }

            Password = null;
            SessionStarted?.Invoke(this, session);
            if (session.IsGuest) {
                RequestNavigation(new BookmarkedHotelViewModel(session));
            } else {
                RequestNavigation(new FlightBookingViewModel(session));
            }
        }

        #endregion
    }
}