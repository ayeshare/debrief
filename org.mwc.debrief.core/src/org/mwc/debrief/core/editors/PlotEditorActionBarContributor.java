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

package org.mwc.debrief.core.editors;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * Plot editor action bar contributor.
 */
public class PlotEditorActionBarContributor extends EditorActionBarContributor {
	private static final String EDITOR_VIEW_MENU_ID = "/org.mwc.debrief.core.EditorView";

	// current editor
	protected PlotEditor _myEditor;

	public PlotEditorActionBarContributor() {
		super();
	}

	@SuppressWarnings("unused")
	@Override
	public void contributeToMenu(final IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
		final IMenuManager editorViewMenu = menuManager.findMenuUsingPath(EDITOR_VIEW_MENU_ID);
		// TODO: verify what is happening with the above item. Do we need it?
	}

	@Override
	public void contributeToToolBar(final IToolBarManager toolBarManager) {
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void init(final IActionBars bars) {
		super.init(bars);
	}

	/**
	 * Sets the active editor for the contributor.
	 * <p>
	 * The <code>EditorActionBarContributor</code> implementation of this method
	 * does nothing. Subclasses may reimplement. This generally entails
	 * disconnecting from the old editor, connecting to the new editor, and updating
	 * the actions to reflect the new editor.
	 * </p>
	 *
	 * @param targetEditor the new target editor
	 */
	@Override
	public void setActiveEditor(final IEditorPart targetEditor) {
		if (targetEditor instanceof PlotEditor) {
			_myEditor = (PlotEditor) targetEditor;
		} else {
			_myEditor = null;
		}
		final IActionBars bars = getActionBars();
		if (bars == null) {
			return;
		}
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), _myEditor.getUndoAction());
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(), _myEditor.getRedoAction());
		bars.updateActionBars();
	}
}