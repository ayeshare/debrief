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

package org.mwc.cmap.media.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.media.Activator;

public class BasePlanetmayoAction extends AbstractHandler {

	protected final String viewId;
	protected int nextId;

	public BasePlanetmayoAction(final String viewId) {
		this.viewId = viewId;
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		findNextId();
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IViewPart view = null;
		try {
			view = window.getActivePage().showView(viewId, "" + (nextId++), IWorkbenchPage.VIEW_ACTIVATE);
		} catch (final PartInitException e) {
			Activator.log(e);
		}
		return view;
	}

	private void findNextId() {
		while (true) {
			boolean found = false;
			final String secondaryId = "" + nextId;
			final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			for (final IWorkbenchPage page : window.getPages()) {
				if (page.findViewReference(viewId, secondaryId) != null) {
					found = true;
					break;
				}
			}
			if (!found) {
				break;
			}
			nextId++;
		}
	}
}
