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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;


@Singleton
class DbExecutor {
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }


    private final ExecutorService executor;

    @Inject
    DbExecutor() { executor = Executors.newSingleThreadExecutor(); }

    public void submit(@Nonnull ThrowingSupplier<Void> task) {
        submit(task, null, null);
    }

    public <T> void submit(@Nonnull ThrowingSupplier<T> task, @Nullable Consumer<T> onComplete) {
        submit(task, onComplete, null);
    }

    public <T> void submit(
        @Nonnull ThrowingSupplier<T> task,
        @Nullable Consumer<T> onComplete,
        @Nullable Consumer<Exception> onError) {
        executor.submit(() -> {
            try {
                final T v = task.get();
                if (onComplete != null) { SwingUtilities.invokeLater(() -> onComplete.accept(v)); }
            }
            catch (Exception e) {
                if (onError == null) { e.printStackTrace(); }
                else { SwingUtilities.invokeLater(() -> onError.accept(e)); }
            }
        });
    }
}
