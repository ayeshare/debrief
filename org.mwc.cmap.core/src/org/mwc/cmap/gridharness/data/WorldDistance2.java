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

package org.mwc.cmap.gridharness.data;

/**
 * Proposed replacement for {@link WorldDistance} that specifies units metadata
 * via API and allows charting.
 * <p>
 * While we assume that {@link WorldDistance} substitutes the public API and
 * can't be changed, we will provide static migration methods that allows to
 * convert instances between {@link WorldDistance} and {@link WorldDistance2}
 *
 * @see WorldSpeed2
 */
public class WorldDistance2 extends AbstractValueInUnits {

	public static final UnitsSet DISTANCE_UNITS;

	public static final UnitsSet.Unit METRES;

	public static final UnitsSet.Unit YARDS;

	public static final UnitsSet.Unit KYDS;

	public static final UnitsSet.Unit KM;

	public static final UnitsSet.Unit NM;

	public static final UnitsSet.Unit DEGS;

	public static final UnitsSet.Unit FT;

	static {
		DISTANCE_UNITS = new UnitsSet("nm", false);
		METRES = DISTANCE_UNITS.addUnit("m", 1852, true);
		YARDS = DISTANCE_UNITS.addUnit("yds", 2025.371828);
		KYDS = DISTANCE_UNITS.addUnit("kyds", 2.025371828);
		KM = DISTANCE_UNITS.addUnit("km", 1.852);
		NM = DISTANCE_UNITS.getMainUnit();
		DEGS = DISTANCE_UNITS.addUnit("degs", 0.016666666666666666666667);
		FT = DISTANCE_UNITS.addUnit("ft", 6076.11548);
		DISTANCE_UNITS.freeze();
	}

	/**
	 * perform a units conversion
	 */
	static public double convert(final UnitsSet.Unit from, final UnitsSet.Unit to, final double val) {
		return DISTANCE_UNITS.convert(from, to, val);
	}

	/**
	 * get the string representing this set of units
	 */
	static public String getLabelFor(final UnitsSet.Unit units) {
		return units.getLabel();
	}

	/**
	 * get the SI units for this type
	 */
	public static UnitsSet.Unit getSIUnits() {
		return DISTANCE_UNITS.getSIUnit();
	}

	/**
	 * get the index for this type of unit
	 * <p>
	 * NOTE: there are no "indices" anymore, we assume that this method may be used
	 * in client code to obtain the units from label first and the then get the
	 * value in this units. This calling sequence will work without syntactical
	 * changes.
	 */
	static public UnitsSet.Unit getUnitIndexFor(final String units) {
		return DISTANCE_UNITS.findUnit(units);
	}

	/**
	 * method to find the smallest set of units which will show the indicated value
	 * as a whole or 1/2 value
	 */
	static public UnitsSet.Unit selectUnitsFor(final double millis) {
		return DISTANCE_UNITS.selectUnitsFor(millis);
	}

	public static WorldDistance unwrap(final WorldDistance2 distance) {
		final WorldDistance result = new WorldDistance(distance.getValueIn(METRES), WorldDistance.METRES);
		return result;
	}

	// Well, thats basically it, the whole code below is provided as a migration
	// helper only
	// The only reason it is here is to allow simple migration,
	// say by just global search/replace WorldDistance to WorldDistance2

	public static WorldDistance2 wrap(final WorldDistance legacyDistance) {
		final WorldDistance2 result = new WorldDistance2();
		result.setValues(legacyDistance.getValueIn(WorldDistance.METRES), METRES);
		return result;
	}

	public WorldDistance2() {
		super(DISTANCE_UNITS);
	}

	public WorldDistance2(final double value, final UnitsSet.Unit units) {
		this();
		setValues(value, units);
	}

	public WorldDistance2(final WorldDistance2 copy) {
		this();
		setValues(copy.getValueIn(METRES), METRES);
	}

	@Override
	public WorldDistance2 makeCopy() {
		return new WorldDistance2(this);
	}

}
