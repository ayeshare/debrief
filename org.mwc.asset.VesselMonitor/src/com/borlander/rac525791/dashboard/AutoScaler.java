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

package com.borlander.rac525791.dashboard;

/**
 *
 * This class takes 2 integers 0 &lt; i, j &lt; RANGE = 1000 and autoscales them
 * as decsribed at the
 * http://pml.clientsection.com/projects/57954/msg/cat/397859/3726984/comments.
 *
 * It is intended to be the mediator between dasboard figure and its speed/depth
 * pointer layers.
 *
 */
public class AutoScaler {
	public static final int RANGE = 1000;

	private static int computeMultiplier(final int actual, final int demanded) {
		final int max = Math.max(actual, demanded);
		if (max <= 10) {
			return 1;
		}
		if (max <= 100) {
			return 10;
		}
		return 100;
	}

	private int myActual;

	private int myDemanded;

	private int myMultiplier = 1;

	private void checkRange(final int unscaledValue) {
		if (unscaledValue < 0 || unscaledValue > RANGE) {
			throw new IllegalArgumentException(//
					"Expected value from 0 to " + RANGE + ", actual: " + unscaledValue);
		}
	}

	/**
	 * @return the value that should be show at the "unit multipliers" control
	 */
	public int getMultiplier() {
		return myMultiplier;
	}

	public int getScaledActual() {
		return myActual * getScaleFactor();
	}

	public int getScaledDemanded() {
		return myDemanded * getScaleFactor();
	}

	/**
	 * @return internal scale factor that allows to map the max of demanded/actual
	 *         values into the range of [100-1000] (if possible).
	 */
	private int getScaleFactor() {
		// to get explanation of this, please check the table at the
		// http://pml.clientsection.com/projects/57954/msg/cat/397859/3726984/comments
		return 100 / myMultiplier;
	}

	/**
	 * @return true if scale should be changed in responce to this change.
	 */
	public boolean setActual(final int actual) {
		checkRange(actual);
		myActual = actual;
		return updateMultiplier();
	}

	public boolean setDemanded(final int demanded) {
		checkRange(demanded);
		myDemanded = demanded;
		return updateMultiplier();
	}

	private boolean updateMultiplier() {
		final int newMultiplier = computeMultiplier(myActual, myDemanded);
		final boolean changed = (newMultiplier != myMultiplier);
		myMultiplier = newMultiplier;
		return changed;
	}
}
