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

package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Table;
import org.mwc.cmap.grideditor.table.GridEditorTable;

public class TablePopupMenuBuilder {

	private final MenuManager myMenuManager;

	private final GridEditorActionGroup myActionGroup;

	public TablePopupMenuBuilder(final GridEditorTable tableUI, final GridEditorActionGroup actionGroup) {
		myActionGroup = actionGroup;
		myMenuManager = new MenuManager();
		myMenuManager.setRemoveAllWhenShown(true);
		myMenuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				myActionGroup.fillContextMenu(manager);
			}
		});
		final Table table = tableUI.getTableViewer().getTable();
		table.setMenu(myMenuManager.createContextMenu(table));
	}
}
