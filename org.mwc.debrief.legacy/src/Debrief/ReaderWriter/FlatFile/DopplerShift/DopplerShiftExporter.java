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

package Debrief.ReaderWriter.FlatFile.DopplerShift;

import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.ArrayOffsetHelper.LegacyArrayOffsetModes;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * exporter class to replicate old Strand export format
 *
 * @author ianmayo
 *
 */
public class DopplerShiftExporter {

	/**
	 * type of exception for when we fail to export
	 *
	 * @author ian
	 *
	 */
	public static class ExportException extends Exception {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public ExportException(final String message) {
			super(message);
		}
	}

	/**
	 * format this date in the prescribed format
	 *
	 * @param val the date to format
	 * @return the formatted date
	 */
	static protected String formatThis(final Date val) {
		final DateFormat df = new GMTDateFormat("HH:mm:ss	dd/MM/yyyy");
		return df.format(val);
	}

	/**
	 * export the dataset to a string
	 *
	 * @param primaryTrack    the ownship track
	 * @param secondaryTracks sec tracks = presumed to be just one
	 * @param period          the time period to export
	 * @param baseFreq
	 * @param sensorType      what sensor type was specified
	 * @return
	 */
	public String export(final WatchableList primaryTrack, final WatchableList[] secondaryTracks,
			final TimePeriod period, final double baseFreq) throws ExportException {

		String res2 = null;

		final boolean onlyVis = true;
		final TrackWrapper sensorHost = (TrackWrapper) primaryTrack;
		final TrackWrapper targetTrack = (TrackWrapper) secondaryTracks[0];

		// store the frequency doublets
		final TreeSet<Doublet> res = new TreeSet<Doublet>();

		// friendly fix-wrapper to save us repeatedly creating it
		final FixWrapper index = new FixWrapper(new Fix(null, new WorldLocation(0, 0, 0), 0.0, 0.0));

		// loop through our sensor data
		final Enumeration<Editable> sensors = sensorHost.getSensors().elements();
		if (sensors != null) {
			System.out.println("-------------------");
			while (sensors.hasMoreElements()) {
				final SensorWrapper wrapper = (SensorWrapper) sensors.nextElement();
				if (!onlyVis || (onlyVis && wrapper.getVisible())) {
					final Enumeration<Editable> cuts = wrapper.elements();
					while (cuts.hasMoreElements()) {
						final SensorContactWrapper scw = (SensorContactWrapper) cuts.nextElement();
						if (!onlyVis || (onlyVis && scw.getVisible())) {
							FixWrapper targetFix = null;
							TrackSegment targetParent = null;

							if (targetTrack != null) {
								// right, get the track segment and fix nearest to
								// this
								// DTG
								final Enumeration<Editable> trkData = targetTrack.elements();
								final Vector<TrackSegment> _theSegments = new Vector<TrackSegment>();

								while (trkData.hasMoreElements()) {

									final Editable thisI = trkData.nextElement();
									if (thisI instanceof SegmentList) {
										final SegmentList thisList = (SegmentList) thisI;
										final Enumeration<Editable> theElements = thisList.elements();
										while (theElements.hasMoreElements()) {

											final TrackSegment ts = (TrackSegment) theElements.nextElement();

											// check it's in our period
											final BaseTimePeriod bp = new BaseTimePeriod(ts.startDTG(), ts.endDTG());
											if (bp.overlaps(period))
												_theSegments.add(ts);
										}

									}
									if (thisI instanceof TrackSegment) {
										final TrackSegment ts = (TrackSegment) thisI;

										// check it's in our period
										final BaseTimePeriod bp = new BaseTimePeriod(ts.startDTG(), ts.endDTG());
										if (bp.overlaps(period))
											_theSegments.add(ts);
									}
								}

								if (_theSegments.size() == 0) {
									throw new ExportException("Target track is not present in specified time period");
								} else {
									final Iterator<TrackSegment> iter = _theSegments.iterator();
									while (iter.hasNext()) {
										final TrackSegment ts = iter.next();

										final TimePeriod validPeriod = new TimePeriod.BaseTimePeriod(ts.startDTG(),
												ts.endDTG());
										if (validPeriod.contains(scw.getDTG())) {
											// sorted. here we go
											targetParent = ts;

											// create an object with the right time
											index.getFix().setTime(scw.getDTG());

											// and find any matching items
											final SortedSet<Editable> items = ts.tailSet(index);
											targetFix = (FixWrapper) items.first();
										}
									}
								}
							}

							final Watchable[] matches = sensorHost.getNearestTo(scw.getDTG(), false);
							if ((matches != null) && (matches.length > 0)) {
								// put the sensor location into the
								final FixWrapper hostFix = sensorHost.getBacktraceTo(scw.getDTG(),
										wrapper.getSensorOffset(),
										wrapper.getArrayCentreMode().equals(LegacyArrayOffsetModes.WORM));

								final Doublet thisDub = new Doublet(scw, targetFix, targetParent, hostFix);

								// if we've no target track add all the points
								if (targetTrack == null) {
									// store our data
									res.add(thisDub);
								} else {
									// if we've got a target track we only add points
									// for which we
									// have
									// a target location
									if (targetFix != null) {
										// store our data
										res.add(thisDub);
									}
								} // if we know the track
							} // if there are any matching items
								// if we find a match
						} // if cut is visible
					} // loop through cuts
				} // if sensor is visible
			} // loop through sensors
		} // if there are sensors

		// ok, ready to dump data - do some checks
		if (baseFreq == -1)
			throw new ExportException("Target track does not have base frequency assigned");

		// have we found some cust
		if (res.size() == 0)
			throw new ExportException("No sensor cuts found");

		// start off with the recorded base frequency
		res2 = "base freq," + baseFreq;
		res2 += "\n";

		// and the header
		res2 += "time, measured frequency, predicted frequency";
		res2 += "\n";

		// sort out the speed of sound
		final String spdStr = Application.getThisProperty(FrequencyCalcs.SPEED_OF_SOUND_KTS_PROPERTY);
		final double speedOfSound;
		if (spdStr != null && spdStr.length() > 0) {
			speedOfSound = Double.parseDouble(spdStr);
		} else {
			speedOfSound = FrequencyCalcs.SpeedOfSoundKts;
		}

		// now the cuts
		final Iterator<Doublet> iter = res.iterator();
		while (iter.hasNext()) {
			res2 += exportThis(iter.next(), speedOfSound);
			res2 += "\n";
		}

		return res2;
	}

	private String exportThis(final Doublet doublet, final double speedOfSound) {
		String res = formatThis(doublet.getDTG().getDate());
		res += ",";
		res += doublet.getMeasuredFrequency();
		res += ",";
		res += doublet.getPredictedFrequency(speedOfSound);
		return res;
	}

}
