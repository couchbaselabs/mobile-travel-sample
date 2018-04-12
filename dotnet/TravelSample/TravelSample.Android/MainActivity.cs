// 
// MainActivity.cs
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
using Acr.UserDialogs;
using Android.App;
using Android.Content.PM;
using Android.OS;
using TravelSample.Core.Services;
using TravelSample.Droid.Services;

namespace TravelSample.Droid
{
    [Activity(Label = "TravelSample", Icon = "@drawable/icon", Theme = "@style/MainTheme", ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation)]
    public class MainActivity : global::Xamarin.Forms.Platform.Android.FormsAppCompatActivity
    {
        #region Overrides

        protected override void OnCreate(Bundle bundle)
        {
            TabLayoutResource = Resource.Layout.Tabbar;
            ToolbarResource = Resource.Layout.Toolbar;

            base.OnCreate(bundle);

            global::Xamarin.Forms.Forms.Init(this, bundle);
            Couchbase.Lite.Support.Droid.Activate(ApplicationContext);

            Couchbase.Lite.DI.Service.Register<IDatabaseSeedService>( new DatabaseSeedService(ApplicationContext)); 
            UserDialogs.Init(() => (Activity)ApplicationContext);
            LoadApplication(new App());
        }

        #endregion
    }
}

