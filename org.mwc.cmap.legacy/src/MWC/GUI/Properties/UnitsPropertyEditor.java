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

import java.beans.PropertyEditorSupport;

/**
 * //////////////////////////////////// class providing a drop-down list of
 * units types
 */

public class UnitsPropertyEditor extends PropertyEditorSupport {

	public static final String UNITS_PROPERTY = "RNG_UNITS";
	public static final String YDS_UNITS = "yd";
	public static final String KYD_UNITS = "kyd";
	public static final String METRES_UNITS = "m";
	public static final String KM_UNITS = "km";
	public static final String NM_UNITS = "nm";

	public static final String OLD_YDS_UNITS = "yds";
	public static final String OLD_KYD_UNITS = "kyds";

	/**
	 * the user's current selection
	 */
	protected String _myUnits;

	@Override
	public String getAsText() {
		return _myUnits;
	}

	/**
	 * get the list of String we provide editing for
	 *
	 * @return list of units types
	 */
	@Override
	public String[] getTags() {
		final String tags[] = { YDS_UNITS, KYD_UNITS, NM_UNITS, KM_UNITS, METRES_UNITS };
		return tags;
	}

	@Override
	public Object getValue() {
		return _myUnits;
	}

	@Override
	public void setAsText(final String val) {
		_myUnits = val;
	}

	@Override
	public void setValue(final Object p1) {
		if (p1 instanceof String) {
			final String val = (String) p1;
			setAsText(val);
		}
	}
}
