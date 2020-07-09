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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UnitsSet {

	public static final class Unit {

		private final UnitsSet myUnitSet;

		private final String myLabel;

		private final double myScaleFactor;

		private Unit(final UnitsSet unitSet, final String label, final double scaleFactor) {
			myUnitSet = unitSet;
			myLabel = label;
			myScaleFactor = scaleFactor;
		}

		public String getLabel() {
			return myLabel;
		}

		public double getScaleFactor() {
			return myScaleFactor;
		}

		public UnitsSet getUnitSet() {
			return myUnitSet;
		}

		public boolean isSIUnit() {
			return myScaleFactor == 1.0d;
		}
	}

	private final LinkedHashMap<String, Unit> myUnits = new LinkedHashMap<String, Unit>();

	private final Unit myMainUnit;

	private Unit mySIUnit;

	private boolean myIsFrosen;

	/**
	 * Creates the Units set for given "main" unit. All other units in set will
	 * provide scale factors to this "main" unit.
	 *
	 * @param mainUnitLabel label for main unit
	 * @param isSIUnit      <code>true</code> if the main unit belongs to SI units
	 *                      system
	 */
	public UnitsSet(final String mainUnitLabel, final boolean isSIUnit) {
		myMainUnit = new Unit(this, mainUnitLabel, 1.0d);
		myUnits.put(mainUnitLabel, myMainUnit);
		if (isSIUnit) {
			mySIUnit = myMainUnit;
		}
	}

	public Unit addUnit(final String label, final double scaleFactor) {
		return addUnit(label, scaleFactor, false);
	}

	public Unit addUnit(final String label, final double scaleFactor, final boolean isSIUnit) {
		ensureNotFrozen();
		if (scaleFactor == 0) {
			throw new IllegalArgumentException("ScaleFactor can't be 0");
		}
		if (myMainUnit.getLabel().equals(label)) {
			throw new IllegalArgumentException("You can't change main unit: " + label);
		}
		// allow unit replacement for now
		final Unit unit = new Unit(this, label, scaleFactor);
		myUnits.put(label, unit);

		if (isSIUnit) {
			// allow replacement?
			mySIUnit = unit;
		}
		return unit;
	}

	public double convert(final Unit from, final Unit to, final double val) {
		ensureUnitKnown(from);
		ensureUnitKnown(to);

		if (from == to) {
			return val;
		}

		// get this scale value
		double scaleVal = from.getScaleFactor();

		// convert to main unit
		final double tmpVal = val / scaleVal;

		// get the new scale val
		scaleVal = to.getScaleFactor();

		// convert to new value
		return tmpVal * scaleVal;
	}

	private void ensureNotFrozen() {
		if (myIsFrosen) {
			throw new IllegalStateException("I am frozen: " + this);
		}
	}

	private void ensureUnitKnown(final Unit unit) {
		if (unit == null) {
			throw new NullPointerException();
		}
		if (unit.getUnitSet() != this) {
			throw new IllegalArgumentException(
					"" + this + " is not compatible with unit " + unit.getLabel() + ", from " + unit.getUnitSet());
		}
	}

	public Unit findUnit(final String label) {
		return myUnits.get(label);
	}

	/**
	 * Makes this {@link UnitsSet} immutable. Any future attempts to modify this
	 * unit set will fail with {@link IllegalStateException}
	 */
	public void freeze() {
		myIsFrosen = true;
	}

	public String[] getAllUnitLabels() {
		final List<String> result = new ArrayList<String>(myUnits.keySet());
		return result.toArray(new String[result.size()]);
	}

	public Unit[] getAllUnits() {
		final List<Unit> result = new ArrayList<Unit>(myUnits.values());
		return result.toArray(new Unit[result.size()]);
	}

	public Unit getMainUnit() {
		return myMainUnit;
	}

	public Unit getSIUnit() {
		return mySIUnit;
	}

	/**
	 * method to find the smallest set of units which will show the indicated value
	 * as a whole or 1/2 value
	 */
	public Unit selectUnitsFor(final double value) {
		for (final Unit nextUnit : myUnits.values()) {
			double newVal = convert(myMainUnit, nextUnit, value);

			// double the value, so that 1/2 values are valid
			newVal *= 2;

			// is this a whole number?
			if (Math.abs(newVal - (int) newVal) < 0.0000000001) {
				return nextUnit;
			}
		}
		// no, just use the main unit
		return myMainUnit;
	}

	@Override
	public String toString() {
		return "UnitsSet for " + myMainUnit.getLabel();
	}
}
