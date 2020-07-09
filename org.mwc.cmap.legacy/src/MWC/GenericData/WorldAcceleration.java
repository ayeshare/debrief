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

package MWC.GenericData;

/**
 * class which represents a speed as a value plus a set of units
 */
final public class WorldAcceleration {
	/////////////////////////////////////////////////////////////////
	// member variables
	/////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class AccelTest extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public AccelTest(final String val) {
			super(val);
		}

		public final void testStrings() {
			WorldAcceleration da = new WorldAcceleration(12, Kts_sec);
			String res = da.toString();
			assertEquals("correct output format", res, "12.0 kts/s");

			da = new WorldAcceleration(1.75, M_sec_sec);
			res = da.toString();
			assertEquals("correct output format", res, "1.75 m/s/s");
		}

		public final void testWorldDistanceUnits() {
			final WorldAcceleration w1 = new WorldAcceleration(1, WorldAcceleration.M_sec_sec);
			assertEquals("m sec correct", w1.getValueIn(WorldAcceleration.M_sec_sec), 1d, 0.000001);

			final WorldAcceleration w2 = new WorldAcceleration(1, WorldAcceleration.Kts_sec);
			assertEquals("correct value stored", 1, w2._myAccel, 0.001);
			assertEquals("correct units stored", WorldAcceleration.Kts_sec, w2._myUnits, 0.001);
			assertEquals("m/sec correct", w2.getValueIn(WorldAcceleration.M_sec_sec), 0.5144444, 0.000001);
			assertEquals("kts correct", w2.getValueIn(WorldAcceleration.Kts_sec), 1, 0.000001);

			assertEquals("Back to kts correct", w2.getValueIn(WorldAcceleration.Kts_sec), 1, 0.000001);

			assertEquals("retrieve m/s label", WorldAcceleration.getLabelFor(WorldAcceleration.M_sec_sec), "m/s/s");
			assertEquals("retrieve kts label", WorldAcceleration.getLabelFor(WorldAcceleration.Kts_sec), "kts/s");

		}
	}

	/**
	 * the types of units we can handle
	 */
	static public final int M_sec_sec = 0;

	static public final int Kts_sec = 1;
	/**
	 * the scale factors for the units compared to minutes
	 */
	static private double _scaleVals[] = { 1, MWC.Algorithms.Conversions.Mps2Kts(1), };

	/**
	 * the units labels
	 */
	static public final String[] UnitLabels = { "m/s/s", "kts/s", };

	/**
	 * perform a units conversion
	 */
	static public double convert(final int from, final int to, final double val) {
		// get this scale value
		double scaleVal = _scaleVals[from];

		// convert to mins
		final double tmpVal = val / scaleVal;

		// get the new scale val
		scaleVal = _scaleVals[to];

		// convert to new value
		return tmpVal * scaleVal;
	}

	/////////////////////////////////////////////////////////////////
	// constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * get the string representing this set of units
	 */
	static public String getLabelFor(final int units) {
		return UnitLabels[units];
	}

	/**
	 * get the SI units for this type
	 */
	public static int getSIUnits() {
		return M_sec_sec;
	}

	/////////////////////////////////////////////////////////////////
	// member methods
	/////////////////////////////////////////////////////////////////

	/**
	 * get the index for this type of unit
	 */
	static public int getUnitIndexFor(final String units) {
		int res = 0;
		for (int i = 0; i < UnitLabels.length; i++) {
			final String unitLabel = UnitLabels[i];
			if (units.equals(unitLabel)) {
				res = i;
				break;
			}
		}
		return res;
	}

	/**
	 * the actual speed (in user selected units)
	 */
	double _myAccel;

	/**
	 * the user's selection of units
	 *
	 */
	int _myUnits;

	/**
	 * normal constructor
	 *
	 * @param value the distance in the supplied units
	 * @param units the units used for this distance
	 */
	public WorldAcceleration(final double value, final int units) {
		_myAccel = value;
		_myUnits = units;
	}

	/**
	 * copy constructor
	 */
	public WorldAcceleration(final WorldAcceleration other) {
		_myAccel = other._myAccel;
		_myUnits = other._myUnits;
	}

	public int getUnits() {
		return _myUnits;
	}

	public String getUnitsLabel() {
		return getLabelFor(_myUnits);
	}

	public double getValue() {
		return _myAccel;
	}

	/**
	 * get this actual distance, expressed in minutes
	 */
	public double getValueIn(final int units) {
		return convert(_myUnits, units, _myAccel);
	}

	/**
	 * produce as a string
	 */
	@Override
	public String toString() {
		final String res = _myAccel + " " + getUnitsLabel();
		return res;
	}

}
