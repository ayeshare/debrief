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

package ASSET.Scenario.Observers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import ASSET.ScenarioType;
import MWC.Utilities.TextFormatting.GeneralFormat;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 12-Aug-2004 Time: 09:13:45 To
 * change this template use File | Settings | File Templates.
 */
public abstract class EndOfRunRecordToFileObserver extends RecordToFileObserverType {

	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////

	public static String getHeaderInfo() {
		String res = "ASSET Version:" + ASSET.GUI.VersionInfo.getVersion() + GeneralFormat.LINE_SEPARATOR;
		res += "File saved:" + MWC.Utilities.TextFormatting.FullFormatDateTime.toString(new Date().getTime())
				+ GeneralFormat.LINE_SEPARATOR;
		return res;
	}

	/**
	 * output the ASSET header information
	 *
	 * @param writer destination
	 */
	protected static void writeHeaderInfo(final FileWriter writer) {
		try {
			writer.write(getHeaderInfo());
		} catch (final IOException e) {
			e.printStackTrace(); // To change body of catch statement use File | Settings | File
									// Templates.
		}
	}

	/**
	 * declare the new object
	 *
	 * @param directoryName the directory to write to
	 * @param fileName      the filename to use
	 * @param observerName  the name of this observer
	 * @param isActive      whether we're active
	 */
	public EndOfRunRecordToFileObserver(final String directoryName, final String fileName, final String observerName,
			final boolean isActive) {
		super(directoryName, fileName, observerName, isActive);
	}

	/**
	 * whether to actually write the end-of-run results to file
	 */
	protected boolean doEndOfRunWrite() {
		return true;
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 *
	 * @param scenario the scenario we're closing from
	 */
	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {

		// just check if we're doing the end of run processing
		if (doEndOfRunWrite()) {
			try {
				// ok. get ready to output our status information
				final FileWriter _myWriter = createOutputFileWriter(scenario);

				// write the asset header info
				writeHeaderInfo(_myWriter);

				// and output our results
				writeMyResults(_myWriter);

				// and close the writer
				_myWriter.close();

				// and reset my data
				resetData();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * run is complete, data has been exported, reset our data structures
	 */
	protected abstract void resetData();

	/**
	 * ok, run is complete. output our results data to the supplied writer
	 *
	 * @param myWriter
	 * @throws IOException
	 */
	protected abstract void writeMyResults(FileWriter myWriter) throws IOException;
}
