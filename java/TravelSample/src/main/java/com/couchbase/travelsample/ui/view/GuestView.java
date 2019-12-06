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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.couchbase.travelsample.model.Hotel;
import com.couchbase.travelsample.ui.controller.GuestController;
import com.couchbase.travelsample.ui.view.widgets.HotelCellRenderer;


@Singleton
public final class GuestView extends Page<GuestController> {
    private final static Logger LOGGER = Logger.getLogger(GuestView.class.getName());

    public static final String PAGE_NAME = "GUEST";

    interface HotelSelector {
        Set<Hotel> getSelection();
    }

    private class SelectionListener implements ListSelectionListener {
        final Set<Hotel> selection = new HashSet<>();

        public SelectionListener() {}

        public void valueChanged(ListSelectionEvent e) {
            Object src = e.getSource();
            if (!(src instanceof JList)) { return; }
            JList<Hotel> hotels = ((JList<Hotel>) src);

            ListModel<Hotel> model = hotels.getModel();
            ListSelectionModel selectionModel = hotels.getSelectionModel();

            boolean selectionEmpty = selectionModel.isSelectionEmpty();

            setDeleteButtonEnabled(!selectionEmpty);

            selection.clear();
            if (selectionEmpty) { return; }

            int n = selectionModel.getMaxSelectionIndex();
            for (int i = selectionModel.getMinSelectionIndex(); i <= n; i++) {
                selection.add(model.getElementAt(i));
            }

            LOGGER.log(Level.INFO, "new selection: " + selection);
        }
    }

    private JPanel panel;
    private JList<Hotel> bookmarks;
    private JButton logoutButton;
    private JButton deleteBookmarkButton;
    private JButton addBookmarkButton;

    @Inject
    public GuestView(GuestController controller) {
        super(PAGE_NAME, controller);

        final SelectionListener selectionListener = new SelectionListener();

        logoutButton.addActionListener(e -> logout());
        addBookmarkButton.addActionListener(e -> selectHotel());

        deleteBookmarkButton.addActionListener(e -> controller.deleteBookmark(selectionListener.selection));
        setDeleteButtonEnabled(false);

        bookmarks.setModel(controller.getBookmarksModel());
        bookmarks.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        bookmarks.addListSelectionListener(selectionListener);
        bookmarks.setCellRenderer(new HotelCellRenderer());
    }

    @Override
    public JPanel getView() { return panel; }

    @Override
    public void open(@Nullable Page<?> prevPage) {
        if (prevPage instanceof HotelSearchView) {
            controller.addBookmarks(((HotelSearchView) prevPage).getSelection());
        }
        controller.fetchBookmarks();
    }

    void setDeleteButtonEnabled(boolean enabled) {
        deleteBookmarkButton.setEnabled(enabled);
        deleteBookmarkButton.setBackground(enabled ? COLOR_ACCENT : COLOR_SELECTED);
    }

    private void selectHotel() {
        bookmarks.clearSelection();
        controller.selectHotel();
    }
}
