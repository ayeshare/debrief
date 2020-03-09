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

package org.mwc.debrief.core.ContextOperations;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.TimePeriod;

/**
 * @author ian.mayo
 */
public class TrimTrack implements RightClickContextItemGenerator {

	private static class TrimTrackOperation extends CMAPOperation {

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;

		/**
		 * the track we're interpolating
		 */
		private final ArrayList<TrackWrapper> _tracks;

		public TrimTrackOperation(final String title, final Layers layers, final ArrayList<TrackWrapper> tracks) {
			super("Trim track(s)");
			_layers = layers;
			_tracks = tracks;
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// check the time controller is open
			final IViewPart part = CorePlugin.findView(CorePlugin.TIME_CONTROLLER);
			if (part == null) {
				CorePlugin.errorDialog("Trim Tracks",
						"The Time Controller must be used to specify the trimmed time period.\n"
								+ "Please open the Time Controller View.");
			} else {
				// ok, try to get the time
				final TimePeriod period = part.getAdapter(TimePeriod.class);

				if (period != null)

				{
					final Iterator<TrackWrapper> iter = _tracks.iterator();
					while (iter.hasNext()) {
						final TrackWrapper thisT = iter.next();
						thisT.trimTo(period);
					}

					CorePlugin.errorDialog("Trim Tracks",
							"This has been a destructive change, data has been deleted.\nPlease use Save-As "
									+ "if you don't wish to overwrite the orginal (full-extent) datafile");
				}
			}

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			return null;
		}

	}

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	@Override
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects) {
		ArrayList<TrackWrapper> tracks = null;

		// we're only going to work with one item
		for (int i = 0; i < subjects.length; i++) {
			final Editable editable = subjects[i];
			if (editable instanceof TrackWrapper) {
				if (tracks == null)
					tracks = new ArrayList<TrackWrapper>();

				tracks.add((TrackWrapper) editable);
			}
		}

		// ok, is it worth going for?
		if (tracks != null) {
			final String title;
			if (tracks.size() == 1)
				title = "Trim track to Time Period (from Time Controller)";
			else
				title = "Trim tracks to Time Period (from Time Controller)";

			// get a final version
			final ArrayList<TrackWrapper> finalTracks = tracks;

			// right,stick in a separator
			parent.add(new Separator());

			final Action trimAction = new Action(title) {

				@Override
				public void run() {
					// ok, go for it.
					// sort it out as an operation
					final IUndoableOperation doTrim = new TrimTrackOperation(title, theLayers, finalTracks);

					// ok, stick it on the buffer
					CorePlugin.run(doTrim);
				}
			};
			trimAction.setImageDescriptor(CorePlugin.getImageDescriptor("icons/clock.png"));
			parent.add(trimAction);

		}

	}
}
