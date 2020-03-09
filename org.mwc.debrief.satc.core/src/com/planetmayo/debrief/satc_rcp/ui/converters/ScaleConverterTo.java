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

package com.planetmayo.debrief.satc_rcp.ui.converters;

import com.planetmayo.debrief.satc_rcp.ui.converters.units.MeterToYds;

public class ScaleConverterTo extends ScaleConverterFrom {

	public ScaleConverterTo(final int[] increments, final int[] borders) {
		super(increments, borders);
	}

	@Override
	public Object convert(final Object value) {
		if (!(value instanceof Double)) {
			return null;
		}
		final Double d = new MeterToYds().safeConvert((Double) value);
		// int val = ((Double) value).intValue();
		final int val = d.intValue();
		int current = startValue;
		int result = 0;
		for (int i = 0; i < values.length; i++) {
			if (current + values[i] * increments[i] >= val) {
				result += (val - current) / increments[i];
				break;
			} else {
				result += values[i];
				current += values[i] * increments[i];
			}
		}
		return result;
	}

	@Override
	public Object getFromType() {
		return Double.class;
	}

	@Override
	public Object getToType() {
		return Integer.class;
	}

}
