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

package org.mwc.cmap.gridharness.data.base60;

public class Sexagesimal {

	private final double myDegrees;

	private final double myMinutes;

	private final double mySeconds;

	private final int myHemi;

	public Sexagesimal(final double degrees, final double minutes, final double seconds, final int hemi) {
		myDegrees = degrees;
		myMinutes = minutes;
		mySeconds = seconds;
		myHemi = hemi;
	}

	public String format(final SexagesimalFormat format, final boolean forLongitudeNotLatitude) {
		return format.format(this, forLongitudeNotLatitude);
	}

	public double getCombinedDegrees() {
		return SexagesimalSupport.combineToDegrees(getDegrees(), getMinutes(), getSeconds(), getHemi());
	}

	public double getDegrees() {
		return myDegrees;
	}

	public int getHemi() {
		return myHemi;
	}

	public double getMinutes() {
		return myMinutes;
	}

	public double getSeconds() {
		return mySeconds;
	}
}