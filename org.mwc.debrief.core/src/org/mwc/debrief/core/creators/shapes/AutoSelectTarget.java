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

package org.mwc.debrief.core.creators.shapes;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author ian.mayo
 *
 */
public class AutoSelectTarget extends AbstractHandler implements IWorkbenchWindowActionDelegate {

	private static boolean _ticked = true;

	public static boolean getAutoSelectTarget() {
		return _ticked;
	}

	public static void setAutoSelectTarget(final boolean yes) {
		_ticked = yes;
	}

	/**
	 *
	 */
	@Override
	public void dispose() {
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		final Command command = event.getCommand();
		@SuppressWarnings("unused")
		final boolean oldValue = HandlerUtil.toggleCommandState(command);
		_ticked = !_ticked;
		return null;
	}

	/**
	 * @param window
	 */
	@Override
	public void init(final IWorkbenchWindow window) {
	}

	/**
	 * @param action
	 */
	@Override
	public void run(final IAction action) {
		_ticked = !_ticked;
		System.out.println("state is now:" + _ticked);
	}

	/**
	 * @param action
	 * @param selection
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
	}
}
