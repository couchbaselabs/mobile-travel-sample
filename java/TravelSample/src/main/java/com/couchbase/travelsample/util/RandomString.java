//
// Copyright (c) 2020 Couchbase, Inc All rights reserved.
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
package com.couchbase.travelsample.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nonnull;


public class RandomString {
    public static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHANUMERIC = "0123456789" + ALPHA + ALPHA.toLowerCase(Locale.ROOT);

    private final Random random;
    private final char[] chars;

    public RandomString() { this(new SecureRandom()); }

    public RandomString(@Nonnull Random random) { this(random, ALPHANUMERIC); }

    public RandomString(@Nonnull Random random, @Nonnull String chars) {
        if (chars.length() < 2) { throw new IllegalArgumentException(); }
        this.chars = chars.toCharArray();
        this.random = Objects.requireNonNull(random);
    }

    public String nextString(int len) {
        final char[] buf = new char[len];
        for (int idx = 0; idx < buf.length; ++idx) { buf[idx] = chars[random.nextInt(chars.length)]; }
        return new String(buf);
    }
}
