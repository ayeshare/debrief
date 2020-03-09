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

package org.mwc.cmap.plotViewer.actions;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.PlotViewerPlugin;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Action;

/**
 * @author ian.mayo
 */
abstract public class CoreDragAction extends CoreEditorAction {
	/**
	 * embed switching drag mode into an action, so we can reverse it
	 *
	 * @author ian.mayo
	 *
	 */
	public static class SwitchModeAction implements Action {
		/**
		 * the editor we're controlling
		 *
		 */
		private final SWTChart _editor;

		/**
		 * the mode we're switching to
		 *
		 */
		private final SWTChart.PlotMouseDragger _newMode;

		public SwitchModeAction(final SWTChart.PlotMouseDragger newMode, final SWTChart editor) {
			_editor = editor;
			_newMode = newMode;
		}

		@Override
		public void execute() {
			_editor.setDragMode(_newMode);

			final IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getEditorReferences();
			for (final IEditorReference editorReference : editorReferences) {
				final IEditorPart editorPart = editorReference.getEditor(false);
				if (editorPart instanceof IChartBasedEditor) {
					final IChartBasedEditor editor = (IChartBasedEditor) editorPart;
					final SWTChart chart = editor.getChart();
					if (chart != null) {
						chart.setDragMode(_newMode);
					}
				}
			}

			// ok - store the mode in the core editor
			PlotViewerPlugin.setCurrentMode(_newMode);
		}

		@Override
		public boolean isRedoable() {
			return false;
		}

		@Override
		public boolean isUndoable() {
			return false;
		}

		@Override
		public void undo() {
			// don't bother = we're not undo-able
		}

	}

	@Override
	protected void execute() {
		// find out what the current dragger is
		final PlainChart chrs = getChart();
		final SWTChart myChart = (SWTChart) chrs;

		final SWTChart.PlotMouseDragger oldMode = myChart.getDragMode();

		// get rid of the old model
		oldMode.close();

		// create an instance of the new mode
		final SWTChart.PlotMouseDragger newMode = getDragMode();

		// create the action
		final CoreDragAction.SwitchModeAction theAction = new CoreDragAction.SwitchModeAction(newMode, myChart);

		// initialise the cursor
		final Cursor normalCursor = newMode.getNormalCursor();
		myChart.getCanvasControl().setCursor(normalCursor);

		// and wrap it
		final DebriefActionWrapper daw = new DebriefActionWrapper(theAction);

		// and run it
		CorePlugin.run(daw);
	}

	/**
	 * retrieve an instance of our dragger
	 *
	 * @return
	 */
	abstract public SWTChart.PlotMouseDragger getDragMode();
}