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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public abstract class AbstractGridEditorOperation extends AbstractOperation {

	private final OperationEnvironment myEnvironment;

	private EnvironmentState myStateBeforeFirstRun;

	private EnvironmentState myStateAfterFirstRun;

	public AbstractGridEditorOperation(final String label, final OperationEnvironment environment) {
		super(label);
		addContext(environment.getUndoContext());
		myEnvironment = environment;
	}

	@Override
	public final boolean canRedo() {
		if (myStateBeforeFirstRun == null) {
			return false;
		}
		return myStateBeforeFirstRun.isCompatible(getOperationEnvironment());
	}

	@Override
	public final boolean canUndo() {
		if (myStateAfterFirstRun == null) {
			return false;
		}
		return myStateAfterFirstRun.isCompatible(getOperationEnvironment());
	}

	protected abstract EnvironmentState computeBeforeExecutionState() throws ExecutionException;

	protected abstract EnvironmentState doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

	protected abstract void doUndo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

	@Override
	public final IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		if (myStateBeforeFirstRun == null) {
			myStateBeforeFirstRun = computeBeforeExecutionState();
		}
		final EnvironmentState afterExecute = doExecute(monitor, info);
		if (myStateAfterFirstRun == null) {
			myStateAfterFirstRun = afterExecute;
		}
		return Status.OK_STATUS;
	}

	protected OperationEnvironment getOperationEnvironment() {
		return myEnvironment;
	}

	protected EnvironmentState getStateAfterFirstRun() {
		return myStateAfterFirstRun;
	}

	protected EnvironmentState getStateBeforeFirstRun() {
		return myStateBeforeFirstRun;
	}

	@Override
	public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		if (myStateAfterFirstRun == null || myStateAfterFirstRun.isCompatible(getOperationEnvironment())) {
			throw new IllegalStateException("I told you I can't redo");
		}
		execute(monitor, info);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		if (myStateBeforeFirstRun == null) {
			throw new IllegalStateException("I haven't been executed yet or already failed");
		}
		doUndo(monitor, info);
		if (!myStateBeforeFirstRun.isCompatible(getOperationEnvironment())) {
			// can't roll-back state, subsequent undo's/redo's don't make sense
			myStateAfterFirstRun = null;
			myStateBeforeFirstRun = null;
			throw new ExecutionException("Attempt to undo have been made, but state haven't been restored");
		}
		return Status.OK_STATUS;
	}

}
