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

package org.mwc.cmap.grideditor.table;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

public class CellEditorTraverseHandler implements TraverseListener {

	private final EditableTarget myActivateOnTab;

	private final Object myActuallyEdited;

	public CellEditorTraverseHandler(final EditableTarget activateOnTab, final Object actuallyEdited) {
		myActivateOnTab = activateOnTab;
		myActuallyEdited = actuallyEdited;
	}

	@Override
	public void keyTraversed(final TraverseEvent e) {
		if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
			final ColumnViewer viewer = myActivateOnTab.getColumnViewer();
			final Object element = myActivateOnTab.getElementToEdit(myActuallyEdited);
			if (element != null) {
				final int columnIndex = myActivateOnTab.getColumnIndex();
				viewer.editElement(element, columnIndex);
			}
		}
	}
}
