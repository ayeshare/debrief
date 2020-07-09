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

package MWC.GUI.Properties;

/**
 * class to represent a bounded integer value
 *
 */
public class BoundedInteger {

	///////////////////////////////////
	// member variables
	//////////////////////////////////
	/**
	 * the current value
	 *
	 */
	protected int _current;

	/**
	 * the minimum value which may be reached
	 *
	 */
	protected int _min;

	/**
	 * the maximum value which may be reached
	 *
	 */
	protected int _max;

	///////////////////////////////////
	// constructor
	//////////////////////////////////
	public BoundedInteger(final int current, final int min, final int max) {
		_current = current;
		_min = min;
		_max = max;
	}

	@Override
	public boolean equals(final Object o) {
		boolean res = false;
		if (o instanceof BoundedInteger) {
			final BoundedInteger other = (BoundedInteger) o;
			res = (other.getCurrent() == this.getCurrent());
		}

		return res;
	}

	///////////////////////////////////
	// nested classes
	//////////////////////////////////
	///////////////////////////////////
	// member functions
	//////////////////////////////////
	/**
	 * get the current value
	 *
	 */
	public int getCurrent() {
		return _current;
	}

	public int getMax() {
		return _max;
	}

	/**
	 * get the minimum allowable value
	 *
	 */
	public int getMin() {
		return _min;
	}

	public void setCurrent(final int val) {
		_current = val;
	}
}
