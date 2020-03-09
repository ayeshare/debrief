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

package org.mwc.cmap.NarrativeViewer.actions;

import org.mwc.cmap.NarrativeViewer.Column;

public class SwitchColumnVisibilityAction extends AbstractDynamicAction {
	private final Column myColumn;

	public SwitchColumnVisibilityAction(final Column column, final String name) {
		myColumn = column;
		setText(name);
	}

	@Override
	public void refresh() {
		setChecked(myColumn.isVisible());
	}

	@Override
	public void run() {
		final boolean wasVisible = myColumn.isVisible();
		myColumn.setVisible(!wasVisible);
	}

}
