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

package Debrief.ReaderWriter.PCArgos;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;

import MWC.GUI.Layers;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GenericData.WorldLocation;

abstract public class ImportRangeDataPanel {
	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	/**
	 * the last directory we read data from
	 */
	static String _lastDirectory;

	/**
	 * the frequency selected (millis)
	 */
	static long _theFreq;

	/**
	 * the location origin for this type of data (preset to Autec);
	 */
	static WorldLocation _theOrigin = new WorldLocation(24.443785555092, -77.63522916694, 0.0);

	/**
	 * the DTG to add the data points to (this gets initialised once the first time
	 * this constructor is called
	 */
	static long _theDTG = -1;

	/**
	 * the data object we are going to insert the layers into
	 */
	private Layers _theData;

	/**
	 * the filename selected
	 */
	String _theFilename;

	/**
	 * the properties window we are putting ourselves into
	 */
	PropertiesPanel _thePanel;

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////
	public ImportRangeDataPanel(final Layers theData, final String lastDirectory, final PropertiesPanel thePanel) {
		_thePanel = thePanel;
		_theData = theData;

		// only store the last directory parameter if it is non-null
		if (lastDirectory != null)
			_lastDirectory = lastDirectory;

		// see if we need to initialise our DTG value
		if (_theDTG == -1) {
			Date dt = new Date();
			Calendar thisCal = Calendar.getInstance();
			thisCal.setTime(dt);
			Calendar otherCal = Calendar.getInstance();
			otherCal.set(thisCal.get(Calendar.YEAR), thisCal.get(Calendar.MONTH), thisCal.get(Calendar.DAY_OF_MONTH), 0,
					0, 0);
			_theDTG = otherCal.getTime().getTime();

			dt = null;
			thisCal = null;
			otherCal = null;
		}

		// prepare the form
		initForm();

		refreshForm();
	}

	void doClose() {
		// reset any local storate?
		_thePanel = null;
		_theData = null;
	}

	void doImport() {
		FileInputStream is = null;
		try {

			// create the input streams
			is = new FileInputStream(_theFilename);
			final BufferedInputStream bs = new BufferedInputStream(is);

			// see which type of importer to use
			if (_theFilename.toUpperCase().endsWith("RAO")) {
				// call the importer, and pass it our parameters and layers
				final ImportPCArgos ic = new ImportPCArgos();

				// trigger the import
				ic.importThis(_theData, _theFilename, bs, _theOrigin, _theDTG, _theFreq);
			} else if (_theFilename.toUpperCase().endsWith("PRN")) {
				// call the importer, and pass it our parameters and layers
				final ImportPMRF ic = new ImportPMRF();

				// trigger the import
				ic.importThis(_theData, _theFilename, bs, _theOrigin, _theDTG, _theFreq);
			} else {
				MWC.GUI.Dialogs.DialogFactory.showMessage("Import Data",
						"Sorry, suffix not recognised, please use PRN (PMRF) or RAO (PCArgos)");
			}

		} catch (final Exception e) {
			MWC.Utilities.Errors.Trace.trace(e);
		} finally {
			// make sure that the file gets closed
			try {
				if (is != null)
					is.close();
			} catch (final java.io.IOException ex) {
				MWC.Utilities.Errors.Trace.trace(ex, "Closing REPLAY file");
			}
		}

	}

	// /////////////////////////////////
	// member functions
	// ////////////////////////////////
	abstract protected void initForm();

	abstract protected void refreshForm();

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////
}
