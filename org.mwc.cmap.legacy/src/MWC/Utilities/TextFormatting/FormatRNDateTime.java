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

// @Author : Ian Mayo
// @Project: Debrief 3
// @File   : FormatRNDateTime.java

package MWC.Utilities.TextFormatting;

import java.text.SimpleDateFormat;

public class FormatRNDateTime {
	static private SimpleDateFormat _df = null;

	static public String getExample() {
		return "ddHHmm.ss";
	}

	static public String toMediumString(final long theVal) {
		return toStringLikeThis(theVal, "ddHHmm");
	}

	static public String toShortString(final long theVal) {
		return toStringLikeThis(theVal, "HHmm");
	}

	static public String toString(final long theVal) {
		return toStringLikeThis(theVal, "ddHHmm.ss");
	}

	static synchronized public String toStringLikeThis(final long theVal, final String thePattern) {
		final java.util.Date theTime = new java.util.Date(theVal);
		String res;

		if (_df == null) {
			_df = new GMTDateFormat(thePattern);
		}

// do we need to change the pattern?
		if (_df.toPattern().equals(thePattern)) {
			// hey, don't bother, we're ok
		} else {
			// and update the pattern
			_df.applyPattern(thePattern);
		}

		res = _df.format(theTime);

		return res;
	}
}
