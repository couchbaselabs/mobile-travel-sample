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
package com.couchbase.travelsample.ui.view.widgets;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class SuggestedTextField<T> extends JTextField
    implements KeyListener, Consumer<List<T>>, ListSelectionListener {
    private final static Logger LOGGER = Logger.getLogger(SuggestedTextField.class.getName());

    @FunctionalInterface
    public interface SuggestionSupplier<S> {
        void match(String target, Consumer<List<S>> consumer);
    }

    @Nonnull
    private final DefaultListModel<T> listModel = new DefaultListModel<>();

    private final SuggestionSupplier<T> supplier;
    private final PopupFactory factory;
    private final JList<T> list;
    private Popup menu;

    public SuggestedTextField(SuggestionSupplier<T> supplier) {
        this.supplier = supplier;

        addKeyListener(this);

        list = new JList<>();
        list.setModel(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);

        factory = new PopupFactory();
    }

    @Override
    public void accept(@Nonnull List<T> items) {
        if (menu == null) { menu = factory.getPopup(new JFrame(), this.list, getX(), getY() + (2 * getHeight())); }

        listModel.clear();
        for (T item : items) { listModel.addElement(item); }

        menu.show();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getSource().equals(list)) { return; }

        ListSelectionModel selectionModel = list.getSelectionModel();
        if (selectionModel.isSelectionEmpty()) { return; }

        menu.hide();

        setText(listModel.elementAt(selectionModel.getMinSelectionIndex()).toString());
    }

    @Override
    public void keyReleased(KeyEvent e) { supplier.match(getText(), this); }

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }
}
