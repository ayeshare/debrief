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
package Debrief.ReaderWriter.FlatFile;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportSATC {

	/**
	 * where we write our data
	 *
	 */
	private final Layers _layers;

	public ImportSATC(final Layers target) {
		super();
		_layers = target;
	}

	public void importThis(final String fName, final InputStream is) throws IOException, ParseException

	{
		// extract the file part of the name (up to the last ".")
		final Path p = Paths.get(fName);
		String trackName = p.getFileName().toString();

		// trim the suffix
		if (trackName.lastIndexOf(".") != -1) {
			trackName = trackName.substring(0, trackName.lastIndexOf("."));
		}

		// ok, loop through the lines
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		TrackWrapper myTrack = null;
		SensorWrapper sensor = null;

		String satc_sentence;

		// ok, ditch the first line.
		br.readLine();

		WorldLocation thisLocation = new WorldLocation(0, -12, 0);
		final SimpleDateFormat sds = new GMTDateFormat("yyyy/MMM/dd HH:mm:ss");

		boolean positionsComplete = false;

		Date lastDate = null;

		// loop through the remaining lines
		while ((satc_sentence = br.readLine()) != null) {

			// ok, are we now on the bearings?
			if (satc_sentence.startsWith("//")) {
				positionsComplete = true;

				// reset the date
				lastDate = null;

				// create the sensor wrapper
				sensor = new SensorWrapper("Sensor");
				sensor.setVisible(true);
				myTrack.add(sensor);

				// carry on with the next line
				continue;
			}

			// ok, are we working with a position, or a sensor cut?
			if (!positionsComplete) {
				// ok, parse the position
				final String[] elements = satc_sentence.split(",");
				final String date = elements[2];
				final double courseDegs = Double.valueOf(elements[3]);
				final double speedKts = Double.valueOf(elements[4]);

				final double courseRads = Math.toRadians(courseDegs);
				final double speedYps = new WorldSpeed(speedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3;

				final Date theTime = sds.parse(date);

				WorldVector vector = null;

				// ok, have we passed the first time?
				if (lastDate != null) {
					// ok, sort out the vector
					final long elapsedMillis = theTime.getTime() - lastDate.getTime();
					final double elapsedSecs = elapsedMillis / 1000d;
					final double speedMs = new WorldSpeed(speedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);
					final WorldDistance dist = new WorldDistance(speedMs * elapsedSecs, WorldSpeed.M_sec);
					vector = new WorldVector(courseRads, dist, null);
					thisLocation = thisLocation.add(vector);
				} else {
					// first location = do some start up
					myTrack = new TrackWrapper();
					myTrack.setName(trackName);
					myTrack.setColor(Color.blue);
					final TrackSegment initialLayer = new TrackSegment(TrackSegment.ABSOLUTE);

					// create a DR track segment
					myTrack.add(initialLayer);

					// and store the new track
					this._layers.addThisLayer(myTrack);
				}

				// ok, create the entry
				final Fix newFix = new Fix(new HiResDate(theTime.getTime()), thisLocation, courseRads, speedYps);
				final FixWrapper fw = new FixWrapper(newFix);
				fw.resetName();
				myTrack.add(fw);

				// and move the time along
				lastDate = theTime;
			} else {
				// ok, parse the sensor data
				// ok, parse the position
				final String[] elements = satc_sentence.split(",");
				final String date = elements[0];
				final double bearingDegs = Double.valueOf(elements[1]);
				final Date theTime = sds.parse(date);

				final SensorContactWrapper scw = new SensorContactWrapper(myTrack.getName(),
						new HiResDate(theTime.getTime()), null, bearingDegs, null, null, null, Color.red,
						theTime.toString(), MWC.GUI.CanvasType.SOLID, "Sensor");
				sensor.add(scw);
			}

		}

		// lastly, apply rainbow shading
		final HiResDate startDTG = sensor.getStartDTG();
		final long delta = (sensor.getEndDTG().getMicros() - startDTG.getMicros()) / 1000000;

		final Enumeration<Editable> iter = sensor.elements();
		while (iter.hasMoreElements()) {
			final SensorContactWrapper swc = (SensorContactWrapper) iter.nextElement();
			final long time = (swc.getDTG().getMicros() - startDTG.getMicros()) / 1000000;
			// produce value from 0..1 for how far through the rainbow we
			// require
			final float hue = (float) ((double) time / (double) delta);
			swc.setColor(new Color(Color.HSBtoRGB(hue, 0.8f, 0.7f)));
		}

	}
}
