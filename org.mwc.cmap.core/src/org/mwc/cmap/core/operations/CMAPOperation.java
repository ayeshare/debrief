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

package org.mwc.cmap.core.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.core.CorePlugin;

/**
 * convenience method for setting up a Debrief operation
 *
 * @author ian.mayo
 *
 */
abstract public class CMAPOperation extends AbstractOperation {

	/**
	 * constructor - that also sorts out the context
	 *
	 * @param title
	 */
	public CMAPOperation(final String title) {
		super(title);

		if (CorePlugin.getUndoContext() != null) {
			super.addContext(CorePlugin.getUndoContext());
		}
	}

	/**
	 * instead of having to implement REDO, just call execute
	 *
	 * @param monitor
	 * @param info
	 * @return
	 * @throws ExecutionException
	 */
	@Override
	public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

}
