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

package Debrief.Tools.Operations;

import java.io.File;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;

public final class OpenPlotXML extends MWC.GUI.Tools.Operations.Open {
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////
	// store action information
	///////////////////////////////////////////////////////
	static public final class OpenPlotAction implements Action {
		/**
		 * the filename we originally read the data from (note that by this point the
		 * data has already been read, and stored in the Layers object)
		 */
		private final File _theFile;
		/**
		 * the application we are going to import the data into
		 */
		private final Application _theApplication;

		/**
		 * constructor - produced AFTER we have read in the data, but before we have
		 * added it to the Application
		 */
		public OpenPlotAction(final File theFile, final Application theApplication) {
			_theApplication = theApplication;
			_theFile = theFile;
		}

		/**
		 * ok, the importer has now finished, we can tidy up now
		 *
		 */
		public final void doFinished() {
			// also have a pass through to set the stepper in any narratives
			// try to set the pointer to the TimeStepper in the narratives, if there are any
			final Session newSession = _theApplication.getCurrentSession();
			if (newSession != null) {
				final Layers theData = newSession.getData();
				final Debrief.GUI.Views.PlainView pv = newSession.getCurrentView();
				if (pv instanceof Debrief.GUI.Views.AnalysisView) {
					final int len = theData.size();
					for (int i = 0; i < len; i++) {
						final Layer ly = theData.elementAt(i);
						if (ly instanceof MWC.TacticalData.NarrativeWrapper) {
							@SuppressWarnings("unused")
							final MWC.TacticalData.NarrativeWrapper nw = (MWC.TacticalData.NarrativeWrapper) ly;
						} // whether this is a narrative
					} // through the layers
				} // whether this is an analysis view
			} // if we managed to create a session

			// put the filename into the MRU
			Debrief.GUI.Frames.Application.addToMru(_theFile.getPath());

			// and restore the application cursor
			_theApplication.restoreCursor();

		}

		@Override
		public final void execute() {

			_theApplication.setCursor(java.awt.Cursor.WAIT_CURSOR);

			// collate the list of files to importe
			final java.io.File[] fList = new java.io.File[] { _theFile };

			final MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller caller = new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
					fList, null) {
				// handle all of the files getting finished
				@Override
				public void allFilesFinished(final java.io.File[] fNames, final Layers newData) {
					doFinished();
				}

				// handle a single file getting finished
				@Override
				public void fileFinished(final java.io.File fName, final Layers newData) {
				}
			};

			caller.start();

		}

		@Override
		public final boolean isRedoable() {
			return false;
		}

		@Override
		public final boolean isUndoable() {
			return false;
		}

		@Override
		public final String toString() {
			return "Open " + _theFile.getName();
		}

		@Override
		public final void undo() {
			// delete the plottables from the Application object
		}
	}

	/**
		 *
		 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/**
	 * copy of the parent application for this tool
	 */
	private Application _myApplication = null;

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	public OpenPlotXML(final ToolParent theParent, final java.awt.Image theImage, final Application theApplication) {
		super(theParent, "Open Plot", new String[] { "*.xml", "*.dpf" }, "Debrief Plot Files (*.xml/*.dpf)");

		// store local data

		// see if we have an old directory to retrieve
		if (_lastDirectory.equals("")) {
			final String val = getParent().getProperty("XML_Directory");
			if (val != null)
				_lastDirectory = val;
		}

		_myApplication = theApplication;

	}

	@Override
	public final Action doOpen(final String filename) {
		Action res = null;

		final java.io.File theFile = new java.io.File(filename);

		// data is collated, now create 'action' function
		res = new OpenPlotAction(theFile, _myApplication);

		// return the product
		return res;
	}

}
