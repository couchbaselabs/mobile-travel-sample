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

import java.awt.*;
import javax.annotation.Nonnull;
import javax.swing.JPanel;


public abstract class Page {
    public static final Color COLOR_ACCENT = new Color(204, 42, 47);
    public static final Color COLOR_TEXT = Color.BLACK;
    public static final Color COLOR_UNFOCUSED = Color.GRAY;
    public static final Color COLOR_SELECTED = new Color(204, 42, 47, 100);
    public static final Color COLOR_BACKGROUND = Color.WHITE;


    private final String name;

    public Page(@Nonnull String name) { this.name = name; }

    public abstract JPanel getView();

    public abstract void open(Object args);

    public abstract void close();

    public final String getName() { return name; }
}
