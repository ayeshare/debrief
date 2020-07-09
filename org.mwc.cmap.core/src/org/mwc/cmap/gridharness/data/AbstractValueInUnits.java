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

public abstract class AbstractValueInUnits implements ValueInUnits {

	private final UnitsSet myUnitsSet;

	private final UnitsSet.Unit myStorageUnits;

	private double myStorageValue;

	public AbstractValueInUnits(final UnitsSet unitsSet) {
		myUnitsSet = unitsSet;
		myStorageUnits = unitsSet.getMainUnit();
	}

	@Override
	public UnitsSet getUnitsSet() {
		return myUnitsSet;
	}

	public double getValue() {
		return myStorageValue;
	}

	@Override
	public double getValueIn(final UnitsSet.Unit units) {
		return myUnitsSet.convert(myStorageUnits, units, myStorageValue);
	}

	@Override
	public void setValues(final double value, final UnitsSet.Unit units) {
		myStorageValue = myUnitsSet.convert(units, myStorageUnits, value);
	}

	/**
	 * produce as a string
	 */
	@Override
	public String toString() {
		// so, what are the preferred units?
		final UnitsSet.Unit theUnits = myUnitsSet.selectUnitsFor(myStorageValue);
		final double theValue = getValueIn(theUnits);
		final String res = theValue + " " + theUnits.getLabel();
		return res;
	}

}
