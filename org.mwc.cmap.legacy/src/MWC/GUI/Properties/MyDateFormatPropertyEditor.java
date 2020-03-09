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

public class MyDateFormatPropertyEditor extends MWC.GUI.Properties.DateFormatPropertyEditor {
	public static final String DTG_SPEED = "HHmm x.xkt";

	static public final String NULL_VALUE = "N/A";

	static private final String[] stringTags = { NULL_VALUE, "mm:ss.SSS", "HHmm.ss", "HHmm", DTG_SPEED, "ddHHmm",
			"ddHHmm.ss", "yy/MM/dd HH:mm", };

	private int getMyIndexOf(final String val) {
		int res = INVALID_INDEX;

		// cycle through the tags until we get a matching one
		for (int i = 0; i < getTags().length; i++) {
			final String thisTag = getTags()[i];
			if (thisTag.equals(val)) {
				res = i;
				break;
			}

		}
		return res;
	}

	@Override
	public final String[] getTags() {
		return stringTags;
	}

	@Override
	public void setAsText(final String val) {
		_myFormat = getMyIndexOf(val);
	}

}
