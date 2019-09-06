//
// Copyright (c) 2019 Couchbase, Inc All rights reserved.
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
package com.couchbase.travelsample;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("com.couchbase.travelsample")
public class Config {
    public static final String LOG_DIR = "logs";
    public static final String DATABASE_DIR = "database";
    public static final String GUEST_DATABASE_DIR = DATABASE_DIR + "/guest";
    public static final String DATABASE_NAME = "guest";
    public static final String WEB_APP_ENDPOINT = "http://54.185.31.148:8080/api/";
}
