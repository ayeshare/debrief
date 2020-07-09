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

package ASSET.Scenario.Observers.Recording;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionList;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldSpeed;

public class CSVExportStatusObserver extends RecordStatusToFileObserverType {
	static public class DebriefReplayInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public DebriefReplayInfo(final CSVExportStatusObserver data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("Directory", "The directory to place Debrief data-files"),
						prop("Active", "Whether this observer is active"), };

				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	/**
	 * ************************************************************ member variables
	 * *************************************************************
	 */
	protected boolean _haveOutputPositions = false;

	private final ArrayList<Integer> _recordedDetections = new ArrayList<Integer>();

	/***************************************************************
	 * constructor
	 ***************************************************************/

	private final String _subjectName;

	/**
	 * create a new monitor
	 *
	 * @param directoryName    the directory to output the plots to
	 * @param recordDetections whether to record detections
	 */
	public CSVExportStatusObserver(final String directoryName, final String fileName, final TargetType subjectToTrack,
			final String observerName, final boolean isActive, final String subjectName) {
		super(directoryName, fileName, false, false, true, subjectToTrack, observerName, isActive);

		_subjectName = subjectName;
	}

	/**
	 * ok, create the property editor for this class
	 *
	 * @return the custom editor
	 */
	@Override
	protected Editable.EditorType createEditor() {
		return new CSVExportStatusObserver.DebriefReplayInfo(this);
	}

	/**
	 * determine the normal suffix for this file type
	 */
	@Override
	protected String getMySuffix() {
		return "csv";
	}

	@Override
	protected String newName(final String name) {
		return name + "_" + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System.currentTimeMillis())
				+ ".csv";
	}

	@Override
	public void restart(final ScenarioType scenario) {
		super.restart(scenario);

		// and clear the stored detections
		_recordedDetections.clear();
	}

	/**
	 * output the build details to file
	 */
	@Override
	protected void writeBuildDate(final String theBuildDate) throws IOException {
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	public String writeDetailsToBuffer(final MWC.GenericData.WorldLocation loc, final ASSET.Participants.Status stat,
			final NetworkParticipant pt, final long newTime) {

		final StringBuffer buff = new StringBuffer();

		long theTime = stat.getTime();

		if (theTime == TimePeriod.INVALID_TIME)
			theTime = newTime;

		final String dateStr = MWC.Utilities.TextFormatting.FullFormatDateTime.toISOString(theTime);

		// _os.write("Date, Lat, Long, Course (Degs), Speed (m/s)");
		final DecimalFormat threeDP = new DecimalFormat("0.###");
		final DecimalFormat sixDP = new DecimalFormat("0.######");

		buff.append(dateStr);
		buff.append(", ");
		buff.append(sixDP.format(loc.getLat()));
		buff.append(", ");
		buff.append(sixDP.format(loc.getLong()));
		buff.append(", ");
		buff.append(threeDP.format(stat.getCourse()));
		buff.append(", ");
		buff.append(threeDP.format(stat.getSpeed().getValueIn(WorldSpeed.M_sec)));
		buff.append(System.getProperty("line.separator"));

		return buff.toString();
	}

	/**
	 * write out the file header details for this scenario
	 *
	 * @param title the scenario we're describing
	 * @throws IOException
	 */

	@Override
	protected void writeFileHeaderDetails(final String title, final long currentDTG) throws IOException {
		_os.write("Time, Lat, Long, Course (Degs), Speed (M/Sec)");

		// end the line
		_os.write(System.getProperty("line.separator"));
	}

	@Override
	protected void writeTheseDetectionDetails(final ParticipantType pt, final DetectionList detections,
			final long dtg) {
		// TODO Auto-generated method stub

	}

	// ////////////////////////////////////////////////////////////////////
	// editable properties
	// ////////////////////////////////////////////////////////////////////

	@Override
	public void writeThesePositionDetails(final MWC.GenericData.WorldLocation loc, final ASSET.Participants.Status stat,
			final ASSET.ParticipantType pt, final long newTime) {
		// just double check this is us
		if (_subjectName == null || (_subjectName != null && _subjectName.equals(pt.getName()))) {
			// make a note that we've output some track positions now
			// (and are now happy to output sensor data)
			_haveOutputPositions = true;

			final String res = writeDetailsToBuffer(loc, stat, pt, newTime);
			writeToFile(res);
		}
	}

	@Override
	protected void writeThisDecisionDetail(final NetworkParticipant pt, final String activity, final long dtg) {
		// TODO Auto-generated method stub

	}

	/**
	 * write this text to our stream
	 *
	 * @param msg the string to write
	 */
	private void writeToFile(final String msg) {
		if (msg != null) {
			try {
				if (_os == null)
					super.createOutputFile();

				_os.write(msg);
				_os.flush();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
