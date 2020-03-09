/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.lite.gui.custom;

import java.util.List;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

public class JPopupList<T> extends JPopupMenu {
	/**
	 *
	 */
	private static final long serialVersionUID = -431721677208908941L;

	private final JList<T> _items;

	public JPopupList(final ListCellRenderer<T> _cellRenderer, final JList<T> _items) {
		super();
		this._items = _items;

		initializeComponents();
	}

	public JPopupList(final ListCellRenderer<T> _cellRenderer, final List<AbstractSelection<T>> items) {
		super();
		final Vector<T> list = new Vector<T>();
		for (final AbstractSelection<T> abstractItem : items) {
			list.add(abstractItem.getItem());
		}
		this._items = new JList<T>(list);

		initializeComponents();
	}

	private void initializeComponents() {
		add(_items);
	}

}
