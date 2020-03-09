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

package org.mwc.debrief.multipath2.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.mwc.debrief.multipath2.model.MultiPathModel.CalculationException;
import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import flanagan.interpolation.LinearInterpolation;

/**
 * class that loads & store an SVP, providing a mean average calculator
 *
 * @author ian
 *
 */
public class SVP {

	// /////////////////////////////////////////////////
	// and the testing goes here
	// /////////////////////////////////////////////////
	public static class SVP_Test extends junit.framework.TestCase {
		public static final String SVP_FILE = "src/org/mwc/debrief/multipath2/model/test_svp.csv";
		public static final String SVP_FILE2 = "src/org/mwc/debrief/multipath2/model/test_svp2.csv";
		public static final String SVP_FILE_NO_ZERO = "src/org/mwc/debrief/multipath2/model/test_svp_noZero.csv";

		// TODO FIX-TEST
		public void NtestExtrapolate() {
			double res = SVP.extrapolateZero(3, 4, 6, 5);
			assertEquals("wrong extrapolated value", 3d, res);

			res = SVP.extrapolateZero(3, 4, 6, 6);
			assertEquals("wrong extrapolated value", 2d, res);

			res = SVP.extrapolateZero(3, 6, 6, 4);
			assertEquals("wrong extrapolated value", 8d, res);

			// now try loading it
			final SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try {
				svp.load(SVP_FILE_NO_ZERO);
			} catch (final ParseException e) {
				fail("wrong number format");
			} catch (final IOException e) {
				fail("unable to read lines");
			} catch (final DataFormatException e) {
				fail("bad data");
			}

			assertEquals("not created extra point", 4, svp._depths.length);
			assertEquals("not created extra point", 4, svp._speeds.length);

			assertEquals("not got zero depth", 0d, svp._depths[0]);
			assertEquals("not got surface speed", 1503d, svp._speeds[0]);

		}

		// TODO FIX-TEST
		public void NtestIndex() {
			final SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try {
				svp.load(SVP_FILE);
			} catch (final ParseException e) {
				fail("wrong number format");
			} catch (final IOException e) {
				fail("unable to read lines");
			} catch (final DataFormatException e) {
				fail("bad data");
			}

			assertEquals("got data", 4, svp._depths.length);

			int t1 = svp.pointBefore(0);
			assertEquals("correct depth", 0d, svp._depths[t1]);

			t1 = svp.pointBefore(5);
			assertEquals("correct depth", 0d, svp._depths[t1]);

			t1 = svp.pointBefore(15);
			assertEquals("correct depth", 00d, svp._depths[t1]);

			t1 = svp.pointBefore(35);
			assertEquals("correct depth", 30d, svp._depths[t1]);

			t1 = svp.pointBefore(50);
			assertEquals("correct depth", 40d, svp._depths[t1]);

			t1 = svp.pointAfter(0);
			assertEquals("correct depth", 0d, svp._depths[t1]);

			t1 = svp.pointAfter(5);
			assertEquals("correct depth", 30d, svp._depths[t1]);

			t1 = svp.pointAfter(15);
			assertEquals("correct depth", 30d, svp._depths[t1]);

			t1 = svp.pointAfter(35);
			assertEquals("correct depth", 40d, svp._depths[t1]);

			t1 = svp.pointAfter(50);
			assertEquals("correct depth", 60d, svp._depths[t1]);

			t1 = svp.pointAfter(150);
			assertEquals("correct depth", -1, t1);

		}

		// TODO FIX-TEST
		public void NtestMean() {
			final SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try {
				svp.load(SVP_FILE);
			} catch (final ParseException e) {
				fail("wrong number format");
			} catch (final IOException e) {
				fail("unable to read lines");
			} catch (final DataFormatException e) {
				fail("bad data");
			}

			double mean = svp.getMeanSpeedBetween(30, 55);
			assertEquals("correct mean", 1510.125, mean);

			// now for a calc that spans a SVP step
			mean = svp.getMeanSpeedBetween(20, 55);
			assertEquals("correct mean", 1508.42, mean, 0.01);

			// now for a calc that goes from surface
			mean = svp.getMeanSpeedBetween(0, 35);
			assertEquals("correct mean", 1503.03, mean, 0.01);

			// now for a calc with depths reversed
			mean = svp.getMeanSpeedBetween(55, 20);
			assertEquals("correct mean", 1508.42, mean, 0.01);
		}

		// TODO FIX-TEST
		public void NtestMean2() {
			final SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try {
				svp.load(SVP_FILE2);
			} catch (final ParseException e) {
				fail("wrong number format");
			} catch (final IOException e) {
				fail("unable to read lines");
			} catch (final DataFormatException e) {
				fail("bad data");
			}

			double mean;

			// now for a calc that spans a SVP step
			final int surface = 0;
			int transmitter = 50;
			int receiver = 30;
			mean = svp.getMeanSpeedBetween(surface, transmitter);
			assertEquals("correct mean", 1501.628, mean, 0.01);

			// now for a calc that goes from surface
			mean = svp.getMeanSpeedBetween(surface, receiver);
			assertEquals("correct mean", 1502.207, mean, 0.01);

			// and the direct path
			mean = svp.getMeanSpeedBetween(receiver, transmitter);
			assertEquals("correct mean", 1500.760, mean, 0.01);

			// try some other depths
			transmitter = 20;
			receiver = 75;

			mean = svp.getMeanSpeedBetween(surface, transmitter);
			assertEquals("correct mean", 1502.298, mean, 0.01);

			// now for a calc that goes from surface
			mean = svp.getMeanSpeedBetween(surface, receiver);
			assertEquals("correct mean", 1500.305, mean, 0.01);

			// and the direct path
			mean = svp.getMeanSpeedBetween(receiver, transmitter);
			assertEquals("correct mean", 1499.581, mean, 0.01);
		}

		// TODO FIX-TEST
		public void NtestMissingData() {
			final SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try {
				svp.load(SVP_FILE);
			} catch (final ParseException e) {
				fail("wrong number format");
			} catch (final IOException e) {
				fail("unable to read lines");
			} catch (final DataFormatException e) {
				fail("bad data");
			}

			// change the first point so we don't have data at zero
			svp._depths[0] = 4;

			try {
				svp.getMeanSpeedBetween(0, 22);
				fail("should not have found index");
			} catch (final CalculationException e) {
				assertTrue("wrong message provided", e.getMessage().startsWith(SHALLOW_FAIL));
			}

			try {
				svp.getMeanSpeedBetween(33, 222);
				fail("should not have found index");
			} catch (final RuntimeException e) {
				assertTrue("wrong message provided", e.getMessage().startsWith(DEEP_FAIL));
			}

		}

		public void testDummy() {

		}
	}

	public static final String DEEP_FAIL = "SVP doesn't go deep enough";
	public static final String SHALLOW_FAIL = "SVP doesn't go shallow enough";

	private static double extrapolateZero(final double depthOne, final double speedOne, final double depthTwo,
			final double speedTwo) {
		final double spdDelta = speedTwo - speedOne;
		final double depthDelta = depthTwo - depthOne;
		final double gradient = spdDelta / depthDelta;
		final double zeroSpeed = speedOne - (depthOne * gradient);

		return zeroSpeed;
	}

	double _depths[];

	double _speeds[];

	private HashMap<Double, Double> _cache;

	/**
	 * find the maximum depth
	 *
	 * @return
	 */
	public double getMaxDepth() {
		return _depths[_depths.length - 1];
	}

	/**
	 * calculate the weighted average speed between the two depths
	 *
	 * @param depthOne first depth
	 * @param depthTwo second depth
	 * @return
	 */
	public double getMeanSpeedBetween(final double depthOne, final double depthTwo) {

		Double res = null;

		final Double thisKey = depthOne * 1000 + depthTwo;

//		String thisKey = "" + depthOne + "-" + depthTwo;

		res = _cache.get(thisKey);

		// did we find it?
		if (res != null) {
			// done - we have our answer
		} else {
			// get the data ready
			final LinearInterpolation interp = new LinearInterpolation(_depths, _speeds);

			// special case - equal depths
			if (depthOne == depthTwo)
				res = interp.interpolate(depthOne);
			else {

				// don't have our sound speed, better calculate it.
				final double shallowDepth = Math.min(depthOne, depthTwo);
				final double deepDepth = Math.max(depthOne, depthTwo);

				// ok, first find the point before the shallow depth
				final int before = pointBefore(shallowDepth);

				// did we find one?
				if (before == -1)
					throw new MultiPathModel.CalculationException(SHALLOW_FAIL + " (" + deepDepth + "m)");

				// now find the point after the deep depth
				final int after = pointAfter(deepDepth);

				if (after == -1)
					throw new MultiPathModel.CalculationException(DEEP_FAIL + " (" + deepDepth + "m)");

				double runningMean = -1;
				double lastDepth = -1;
				double lastSpeed = -1;

				// sort out the gaps
				for (int i = before; i <= after; i++) {
					final double thisDepth = _depths[i];
					final double thisSpeed = _speeds[i];

					// have we passed our first loop?
					if (lastDepth != -1) {
						// is this the first segment
						if (runningMean == -1) {
							// just check our whole depth isn't in the first segment.
							double travelInThisSeg;
							if (deepDepth < thisDepth) {
								// yes, we just sep the two depths
								travelInThisSeg = deepDepth - shallowDepth;
							} else {
								travelInThisSeg = thisDepth - shallowDepth;
							}
							final double lowerSpeed = interp.interpolate(shallowDepth);
							final double upperSpeed = thisSpeed;
							final double meanSpeed = (lowerSpeed + upperSpeed) / 2;
							runningMean = meanSpeed * travelInThisSeg;
						} else {
							// ok, are we in a mid-section?
							if (thisDepth < deepDepth) {
								// yes, consume the whole of this section
								final double travelInThisSeg = thisDepth - lastDepth;
								final double meanSpeed = (thisSpeed + lastSpeed) / 2;
								runningMean += meanSpeed * travelInThisSeg;
							} else {
								// we must be in the last leg, just consume the portion we need
								final double travelInThisSeg = deepDepth - lastDepth;
								final double lowerSpeed = lastSpeed;
								final double upperSpeed = interp.interpolate(deepDepth);
								final double meanSpeed = (lowerSpeed + upperSpeed) / 2;
								runningMean += meanSpeed * travelInThisSeg;
							}
						}
					}
					lastDepth = thisDepth;
					lastSpeed = thisSpeed;
				}

				// ok, now just divide by the total depth travelled
				runningMean = runningMean / (deepDepth - shallowDepth);

				res = runningMean;

			}
			_cache.put(thisKey, res);
		}

		// done = we've got our answer
		return res;
	}

	/**
	 * load an SVP from teh specific path
	 *
	 * @param path where to get it from
	 * @throws NumberFormatException if the numbers aren't legible
	 * @throws IOException           if the file can't be found
	 */
	public void load(final String path) throws ParseException, IOException, DataFormatException {
		// ok, clear the cache - we're getting a new profile
		_cache = new HashMap<Double, Double>();

		final Vector<Double> values = new Vector<Double>();

		final BufferedReader bufRdr = new BufferedReader(new FileReader(path));
		String line = null;

		// read each line of text file
		while ((line = bufRdr.readLine()) != null) {
			final StringTokenizer st = new StringTokenizer(line, ",");
			while (st.hasMoreTokens()) {
				final String thisE = st.nextToken();
				if (thisE.length() > 0)
					values.add(MWCXMLReader.readThisDouble(thisE));
			}
		}

		bufRdr.close();

		if (!values.isEmpty()) {
			// just check the values are of the correct order
			final double sampleVal = values.elementAt(1);
			if (sampleVal < 500)
				throw new MultiPathModel.DataFormatException("Doesn't look like sound speed data");

			// ok, now move the values into our two arrays
			final int numEntries = values.size();
			_depths = new double[numEntries / 2];
			_speeds = new double[numEntries / 2];
			for (int i = 0; i < numEntries; i += 2) {
				_depths[i / 2] = values.elementAt(i);
				_speeds[i / 2] = values.elementAt(i + 1);
			}

			// SPECIAL CASE: if the first depth is under 5 metres, extrapolate a
			// point at zero metres
			final double minDepth = _depths[0];
			if (minDepth > 0) {
				// aah, none-zero. do we have one nearby
				if (minDepth < 5) {
					// do we have enough to extrapolate?
					if (_depths.length >= 2) {
						final double zeroSpd = extrapolateZero(_depths[0], _speeds[0], _depths[1], _speeds[1]);

						final double[] tmpDepths = new double[_depths.length + 1];
						final double[] tmpSpeeds = new double[_depths.length + 1];

						// now shove the arrays along
						System.arraycopy(_depths, 0, tmpDepths, 1, _depths.length);
						System.arraycopy(_speeds, 0, tmpSpeeds, 1, _speeds.length);

						_depths = tmpDepths;
						_speeds = tmpSpeeds;

						_depths[0] = 0;
						_speeds[0] = zeroSpd;
					}

				}
			}

		}
	}

	/**
	 * determine the index of the data point after the supplied depth
	 *
	 * @param depth
	 * @return
	 */
	private int pointAfter(final double depth) {
		int res = -1;
		final int len = _depths.length;
		for (int i = 0; i < len; i++) {
			final double thisD = _depths[i];
			if (thisD >= depth) {
				res = i;
				break;
			}
		}

		return res;
	}

	/**
	 * determine the index of the observation before the specified depth
	 *
	 * @param depth
	 * @return
	 */
	private int pointBefore(final double depth) {
		int res = -1;
		final int len = _depths.length;
		for (int i = 0; i < len; i++) {
			final double thisD = _depths[i];
			if (thisD > depth) {
				// ok, passed our data value
				break;
			}
			res = i;
		}

		return res;
	}
}
