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
package Debrief.Wrappers.Extensions.Measurements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * time series that stores double measurements
 *
 * @author ian
 *
 */
@Deprecated
public class TimeSeriesTmpDouble extends TimeSeriesTmpCore {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final List<Double> _values1 = new ArrayList<Double>();

	public TimeSeriesTmpDouble(final String name, final String units) {
		super(name, units);
	}

	public void add(final Long index, final Double value1) {
		_indices.add(index);
		_values1.add(value1);
	}

	public Iterator<Double> getValues() {
		return _values1.iterator();
	}

	@Override
	public void printAll() {
		System.out.println(":" + getName());
		final int len = _indices.size();
		for (int i = 0; i < len; i++) {
			System.out.println("i:" + _indices.get(i) + " v1:" + _values1.get(i));
		}
	}

	/**
	 * convenience function, to describe this plottable as a string
	 */
	@Override
	public String toString() {
		return getName() + " (" + size() + " items)";
	}
}
