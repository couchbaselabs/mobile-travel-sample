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
package com.couchbase.travelsample.db;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.AuthenticationException;

import com.couchbase.lite.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.couchbase.travelsample.model.Hotel;


public class ReplicatorManager {
    private static final Logger LOGGER = Logger.getLogger(DbManager.class.getName());

    public static final String SGW_ENDPOINT = "ws://127.0.0.1:4984/travel-sample";
    public static final int LOGIN_TIMEOUT_SEC = 30;

    private abstract static class ReplicatorListener implements ReplicatorChangeListener {
        @Nonnull
        private final CountDownLatch latch = new CountDownLatch(1);
        @Nonnull
        private final Replicator replicator;

        @Nullable
        protected CouchbaseLiteException err;

        protected ReplicatorListener(@Nonnull Replicator replicator) { this.replicator = replicator; }

        protected abstract boolean checkState(
            @Nonnull ReplicatorActivityLevel state,
            @Nonnull CouchbaseLiteException err);

        @Nonnull
        public CountDownLatch getLatch() { return latch; }

        @Nullable
        public CouchbaseLiteException getError() { return err; }

        @Override
        public void changed(@Nonnull ReplicatorChange change) {
            if (!replicator.equals(change.getReplicator())) { return; }

            final ReplicatorStatus status = replicator.getStatus();
            final ReplicatorActivityLevel state = status.getActivityLevel();
            LOGGER.log(Level.INFO, "Replicator state: " + state.name());
            if (checkState(state, status.getError())) { latch.countDown(); }
        }
    }

    private static final class ReplStartListener extends ReplicatorListener {
        ReplStartListener(@Nonnull Replicator replicator) { super(replicator); }

        @SuppressWarnings("FallThrough")
        @SuppressFBWarnings("SF_SWITCH_NO_DEFAULT")
        @Override
        protected boolean checkState(
            @Nonnull ReplicatorActivityLevel state,
            @Nonnull CouchbaseLiteException err) {
            switch (state) {
                case CONNECTING:
                    return false;
                case STOPPED:
                case OFFLINE:
                    this.err = err;
                default:
                    return true;
            }
        }
    }

    private static final class ReplStopListener extends ReplicatorListener {
        ReplStopListener(@Nonnull Replicator replicator) { super(replicator); }

        @SuppressWarnings("FallThrough")
        @SuppressFBWarnings("SF_SWITCH_NO_DEFAULT")
        @Override
        protected boolean checkState(
            @Nonnull ReplicatorActivityLevel state,
            @Nonnull CouchbaseLiteException err) {
            switch (state) {
                case CONNECTING:
                case IDLE:
                case BUSY:
                    return false;
                case OFFLINE:
                    this.err = err;
                default:
                    return true;
            }
        }
    }

    @Nonnull
    private final Database database;
    @Nullable
    private Replicator replicator;

    ReplicatorManager(@Nonnull Database database) { this.database = database; }

    @Nonnull
    void start(@Nonnull String username, @Nonnull char[] password)
        throws CouchbaseLiteException, IOException, AuthenticationException, URISyntaxException {
        if (replicator != null) { throw new IllegalStateException("Replicator already running"); }

        final ReplicatorConfiguration config
            = new ReplicatorConfiguration(database, new URLEndpoint(new URI(SGW_ENDPOINT)));

        // !!! copying the password into the string is unsecure.
        config.setAuthenticator(new BasicAuthenticator(username, password));

        config.setType(ReplicatorType.PUSH_AND_PULL);
        config.setContinuous(true);

        config.setPushFilter((document, flags) ->
            !(Hotel.DOC_TYPE.equals(document.getString(DbManager.PROP_DOC_TYPE))
                || DbManager.DOC_TYPE_AIRLINE.equals(document.getString(DbManager.PROP_DOC_TYPE))
                || DbManager.DOC_TYPE_AIRPORT.equals(document.getString(DbManager.PROP_DOC_TYPE))
                || DbManager.DOC_TYPE_ROUTE.equals(document.getString(DbManager.PROP_DOC_TYPE))
                || DbManager.DOC_TYPE_LANDMARK.equals(document.getString(DbManager.PROP_DOC_TYPE))));

        final Replicator repl = new Replicator(config);
        final ReplStartListener listener = new ReplStartListener(repl);
        final ListenerToken token = repl.addChangeListener(listener);
        repl.start();

        final boolean ok;
        try { ok = listener.getLatch().await(LOGIN_TIMEOUT_SEC, TimeUnit.SECONDS); }
        catch (InterruptedException ignore) { throw new IOException("Login interrupted"); }
        finally { repl.removeChangeListener(token); }

        if (!ok) { throw new IOException("Login timeout"); }

        final CouchbaseLiteException err = listener.getError();
        if (err != null) {
            if (err.getCode() == CBLError.Code.HTTP_AUTH_REQUIRED) { throw err; }
            throw new AuthenticationException("Authentication error");
        }

        LOGGER.log(Level.INFO, "replicator started: " + repl);
        replicator = repl;
    }

    void stop() {
        final Replicator repl = replicator;
        replicator = null;
        if (repl == null) { return; }

        CouchbaseLiteException err = null;
        for (int i = 0; i < 3; i++) {
            final ReplicatorStatus status = repl.getStatus();
            if (status.getActivityLevel() == ReplicatorActivityLevel.STOPPED) {
                LOGGER.log(Level.INFO, "replicator stopped: " + repl);
                return;
            }
            if (err == null) { err = status.getError(); }

            final ReplStopListener listener = new ReplStopListener(repl);
            final ListenerToken token = repl.addChangeListener(listener);
            repl.stop();

            final boolean ok;
            try { ok = listener.getLatch().await(LOGIN_TIMEOUT_SEC, TimeUnit.SECONDS); }
            catch (InterruptedException ignore) { }
            finally { repl.removeChangeListener(token); }
            if (err == null) { err = listener.getError(); }
        }

        LOGGER.log(Level.WARNING, "failed to stop replicator: " + repl, err);
    }
}
