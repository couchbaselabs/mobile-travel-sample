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
package com.couchbase.travelsample.ui.view;

import javax.annotation.Nonnull;
import javax.swing.JPanel;


public abstract class Page {
    private final String name;

    public Page(@Nonnull String name) { this.name = name; }

    public abstract JPanel getView();

    public abstract void open();

    public abstract void close();

    public final String getName() { return name; }
}
