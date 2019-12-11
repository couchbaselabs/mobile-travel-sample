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
package com.couchbase.travelsample.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.naming.AuthenticationException;

import com.couchbase.lite.CouchbaseLiteException;


public class LoginDao {
    private final DbManager db;
    private final DbExecutor exec;

    @Inject
    public LoginDao(@Nonnull DbManager db, @Nonnull DbExecutor exec) {
        this.db = db;
        this.exec = exec;
    }

    public void openAsGuest(@Nonnull Consumer<Void> listener) {
        exec.submit(
            this::openAsGuestAsync,
            (ok) -> listener.accept(null));
    }

    public void openWithValidation(
        @Nonnull String username,
        @Nonnull char[] password,
        @Nonnull Runnable onSuccess,
        @Nonnull Consumer<Exception> onFailure) {
        exec.submit(
            () -> openWithValidationAsync(username, password),
            (ign) -> onSuccess.run(),
            onFailure);
    }

    private Void openAsGuestAsync() throws CouchbaseLiteException, IOException {
        db.openGuestDb();
        return null;
    }

    private Void openWithValidationAsync(@Nonnull String username, @Nonnull char[] password)
        throws IOException, CouchbaseLiteException, URISyntaxException, AuthenticationException {
        try { db.openUserDb(username, password); }
        finally { Arrays.fill(password, (char) 0); }

        return null;
    }
}
