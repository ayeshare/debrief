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

package org.mwc.cmap.grideditor;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.mwc.cmap.gridharness.data.GriddableSeries;

public class GridEditorUndoSupport {

	private ObjectUndoContext myUndoContext;

	private final IOperationHistory myOperationHistory;

	public GridEditorUndoSupport(final IOperationHistory operationHistory) {
		myOperationHistory = operationHistory;
		myUndoContext = createNullContext();
	}

	private ObjectUndoContext createNullContext() {
		return new ObjectUndoContext(new Object());
	}

	public IOperationHistory getOperationHistory() {
		return myOperationHistory;
	}

	public IUndoContext getUndoContext() {
		return myUndoContext;
	}

	/**
	 * @return <code>true</code> if undo context has been changed
	 */
	public boolean setTableInput(final GriddableSeries mainInput) {
		if (myUndoContext != null && myUndoContext.getObject() == mainInput) {
			return false;
		}

		myOperationHistory.dispose(myUndoContext, true, true, true);
		myUndoContext = mainInput == null ? createNullContext() : new ObjectUndoContext(mainInput);
		return true;
	}

}
