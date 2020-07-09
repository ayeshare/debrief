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

package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.operations.IUndoContext;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;

public class OperationEnvironment {

	private final GriddableSeries mySeries;

	private final TimeStampedDataItem mySubject;

	private final GriddableItemDescriptor myOptionalDescriptor;

	private final IUndoContext myUndoContext;

//	public OperationEnvironment(IUndoContext undoContext, GriddableSeries series) {
//		this(undoContext, series, null, null);
//	}

	public OperationEnvironment(final IUndoContext undoContext, final GriddableSeries series,
			final TimeStampedDataItem optionalSubject) {
		this(undoContext, series, optionalSubject, null);
	}

	public OperationEnvironment(final IUndoContext undoContext, final GriddableSeries series,
			final TimeStampedDataItem optionalSubject, final GriddableItemDescriptor optionalDescriptor) {
		myUndoContext = undoContext;
		mySeries = series;
		mySubject = optionalSubject;
		myOptionalDescriptor = optionalDescriptor;
	}

	/**
	 * @return optional descriptor for this context or <code>null</code> if not
	 *         applicable
	 */
	public GriddableItemDescriptor getDescriptor() {
		return myOptionalDescriptor;
	}

	public GriddableSeries getSeries() {
		return mySeries;
	}

	/**
	 * @return optional subject item for this context or <code>null</code> if not
	 *         applicable
	 */
	public TimeStampedDataItem getSubject() {
		return mySubject;
	}

	public IUndoContext getUndoContext() {
		return myUndoContext;
	}
}
