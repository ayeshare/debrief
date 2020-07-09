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
package info.limpet.stackedcharts.ui.editor.drop;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ScatterSetContainerEditPart;

/**
 * base for classes supporting the drop process, including establishing if the
 * target is valid
 *
 * @author ian
 *
 */
abstract public class ScatterSetDropTargetListener extends CoreDropTargetListener {

	protected static boolean datasetAlreadyExistsOnTheseAxes(final Iterator<DependentAxis> axes, final String name) {
		boolean exists = false;

		while (axes.hasNext()) {
			final DependentAxis dAxis = axes.next();
			final Iterator<Dataset> dIter = dAxis.getDatasets().iterator();
			while (dIter.hasNext()) {
				final Dataset thisD = dIter.next();
				if (name.equals(thisD.getName())) {
					// ok, we can't add it
					System.err.println("Not adding dataset - duplicate name");
					exists = true;
					break;
				}
			}
		}

		return exists;
	}

	protected AbstractGraphicalEditPart feedback;

	protected ScatterSetDropTargetListener(final GraphicalViewer viewer) {
		super(viewer);
	}

	/**
	 * wrap up the data change for the drop event
	 *
	 * @param chart
	 * @param scatterSets
	 * @return
	 */
	abstract protected Command createScatterCommand(Chart chart, List<ScatterSet> scatterSets);

	@Override
	public void drop(final DropTargetEvent event) {
		if (LocalSelectionTransfer.getTransfer().isSupportedType(event.currentDataType)) {
			final StructuredSelection sel = (StructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
			if (sel.isEmpty()) {
				event.detail = DND.DROP_NONE;
				return;
			}
			final List<ScatterSet> scatterSets = convertSelectionToScatterSet(sel);
			final EditPart part = findPart(event);

			final AbstractGraphicalEditPart target = (AbstractGraphicalEditPart) part;

			// ok, now build up the commands necessary to
			// make the changes

			Command scatterCommand;
			if (scatterSets.size() > 0) {
				Chart chart = null;

				// get the target - we need the chart
				if (target instanceof AxisEditPart) {
					final AxisEditPart axis = (AxisEditPart) target;
					chart = (Chart) axis.getParent().getModel();
				} else if (target instanceof ChartEditPart) {
					chart = (Chart) target.getModel();
				} else if (target instanceof ScatterSetContainerEditPart) {
					final ScatterSetContainerEditPart scatter = (ScatterSetContainerEditPart) target;
					chart = (Chart) scatter.getParent().getModel();
				}

				scatterCommand = createScatterCommand(chart, scatterSets);
			} else {
				scatterCommand = null;
			}

			if (scatterCommand != null) {
				getCommandStack().execute(scatterCommand);
			}
		}

		feedback = null;
	}

}
