// 
// App.xaml.cs
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
using MvvmCross.Core.ViewModels;
using MvvmCross.Platform;
using TravelSample.Core.Models;
using TravelSample.Core.ViewModels;
using Windows.ApplicationModel;
using Windows.ApplicationModel.Activation;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace TravelSample.UWP
{
    /// <summary>
    /// Provides application-specific behavior to supplement the default Application class.
    /// </summary>
    sealed partial class App : Application
    {
        #region Variables

        private CouchbaseSession _currentSession;

        #endregion

        #region Constructors

        static App()
        {
            // Best place for this because this will run it before anything else,
            // and only run it once
            Couchbase.Lite.Support.UWP.Activate();
        }

        /// <summary>
        /// Initializes the singleton application object.  This is the first line of authored code
        /// executed, and as such is the logical equivalent of main() or WinMain().
        /// </summary>
        public App()
        {
            InitializeComponent();
            Suspending += OnSuspending;
            Resuming += OnResuming;
        }

        #endregion

        #region Private Methods

        private void CouchbaseSessionStarted(object sender, CouchbaseSession session)
        {
            _currentSession = session;
            session.Ended += (s, args) =>
            {
                _currentSession = null;
            };
        }

        private void OnResuming(object sender, object e)
        {
            _currentSession?.Replicator?.Start();
        }

        private void OnSuspending(object sender, SuspendingEventArgs e)
        {
            var deferral = e.SuspendingOperation.GetDeferral();
            _currentSession?.Replicator?.Stop();
            deferral.Complete();
        }

        #endregion

        #region Overrides

        /// <summary>
        /// Invoked when the application is launched normally by the end user.  Other entry points
        /// will be used such as when the application is launched to open a specific file.
        /// </summary>
        /// <param name="e">Details about the launch request and process.</param>
        protected override void OnLaunched(LaunchActivatedEventArgs e)
        {
            var rootFrame = Window.Current.Content as Frame;

            // Do not repeat app initialization when the Window already has content,
            // just ensure that the window is active
            if (rootFrame == null)
            {
                // Create a Frame to act as the navigation context and navigate to the first page
                rootFrame = new Frame();

                // Place the frame in the current Window
                Window.Current.Content = rootFrame;
            }

            if (e.PrelaunchActivated) {
                return;
            }

            if (rootFrame.Content == null) {
                LoginViewModel.SessionStarted += CouchbaseSessionStarted;
                var setup = new Setup(rootFrame);
                setup.Initialize();

                var start = Mvx.Resolve<IMvxAppStart>();
                start.Start();
            }

            // Ensure the current window is active
            Window.Current.Activate();
        }

        #endregion
    }
}
