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

package org.mwc.cmap.xyplot.views;

import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public class LocationCalculator implements ILocationCalculator {

	static public final class LocationTest extends junit.framework.TestCase {
		WorldLocation start, end, watch;
		LocationCalculator calc = new LocationCalculator(WorldDistance.MINUTES);

		@Override
		public final void setUp() {
			// set the earth model we are expecting
			MWC.GenericData.WorldLocation.setModel(new CompletelyFlatEarth());
		}

		public void testDistanceJump() {
			calc = new LocationCalculator(WorldDistance.KM);
			start = new WorldLocation(25.0000000, 51.6889495, 0);
			end = new WorldLocation(26.3595475, 51.6882503, 0);

			watch = new WorldLocation(26.2152611, 51.862166, 40); // 0240
			System.out.println(calc.getDistance(start, end, watch));

			watch = new WorldLocation(26.2144472, 51.8585917, 40);
			System.out.println(calc.getDistance(start, end, watch));

			watch = new WorldLocation(26.2140611, 51.8572444, 15);
			System.out.println(calc.getDistance(start, end, watch));

			watch = new WorldLocation(26.2137222, 51.8560667, 15); // 0243
			System.out.println(calc.getDistance(start, end, watch));

			watch = new WorldLocation(26.2133806, 51.8548861, 15); // 0244
			System.out.println(calc.getDistance(start, end, watch));

			// Till this moment the distance is increased.
			// There is a "jump" here (the distance is decreased)
			watch = new WorldLocation(26.2283833, 51.5456056, 15); // 0259
			System.out.println(calc.getDistance(start, end, watch));
		}

		public void testGetDistanceMiddle() {
			start = new WorldLocation(0, 0, 0);
			end = new WorldLocation(0, 1, 0);
			watch = new WorldLocation(1, 0.5, 0);

			final double lineLen = new WorldDistance(end.subtract(start)).getValueIn(WorldDistance.MINUTES);
			assertEquals(60.0, lineLen);

			final double perpendicular = watch.rangeFrom(start, end).getValueIn(WorldDistance.MINUTES);
			assertEquals(60.0, perpendicular);

			final double d = calc.getDistance(start, end, watch);
			assertEquals(30.0, d, 0.00001 /* epsilon */);
		}

		public void testGetDistanceQuarter() {
			start = new WorldLocation(0, 0, 0);
			end = new WorldLocation(0, 2, 0);
			watch = new WorldLocation(1, 1.5, 0);

			final double lineLen = new WorldDistance(end.subtract(start)).getValueIn(WorldDistance.MINUTES);
			assertEquals(60.0 * 2, lineLen);

			final double d = Math.abs(calc.getDistance(start, end, watch));
			assertEquals(90.0, d, 0.00001 /* epsilon */);
		}

		public void testGetSampleDistance() {
			// NOTE: this scenario is as described here: http://i.imgur.com/26orkaR.png
			start = new WorldLocation(0, 6, 0);
			end = new WorldLocation(8, 8, 0);
			watch = new WorldLocation(2, 4, 0);

			final double lineLen = new WorldDistance(end.subtract(start)).getValueIn(WorldDistance.DEGS);
			assertEquals(8.24621, lineLen, 0.001);

			final double perpendicular = watch.rangeFrom(start, end).getValueIn(WorldDistance.DEGS);
			assertEquals(2.4253, perpendicular, 0.001);

			final LocationCalculator calc2 = new LocationCalculator(WorldDistance.DEGS);
			final double d = calc2.getDistance(start, end, watch);
			assertEquals(1.4552, d, 0.0001);
		}

	}

	private int _units;

	public LocationCalculator() {
		this(WorldDistance.DEGS);
	}

	public LocationCalculator(final int units) {
		setUnits(units);
	}

	@Override
	public double getDistance(final LineShape line, final Watchable watchable) {
		return getDistance(line.getLine_Start(), line.getLineEnd(), watchable.getLocation());
	}

	/**
	 * find the distance from start point to the perpendicular line meeting the
	 * watch point
	 *
	 * @param start
	 * @param end
	 * @param watchableLocation
	 * @return
	 */
	private double getDistance(final WorldLocation start, final WorldLocation end,
			final WorldLocation watchableLocation) {
		final double rangeDegs = watchableLocation.rangeFrom(start);
		final double hyp = new WorldDistance(rangeDegs, WorldDistance.DEGS).getValueIn(_units);

		// angle from start to end
		final double lineBrg = end.bearingFrom(start);
		final double pointBrg = watchableLocation.bearingFrom(start);
		final double delta = pointBrg - lineBrg;

		// how far along x-section?
		final double along = hyp * Math.cos(delta);

		return along;
	}

	public void setUnits(final int units) {
		this._units = units;
	}
}
