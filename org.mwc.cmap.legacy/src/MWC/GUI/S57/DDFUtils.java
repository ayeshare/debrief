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

package MWC.GUI.S57;

import com.bbn.openmap.layer.vpf.MutableInt;

public class DDFUtils {
	/** ********************************************************************* */
	/* DDFFetchVariable() */
	/*                                                                      */
	/* Fetch a variable length string from a record, and allocate */
	/* it as a new string (with CPLStrdup()). */
	/** ********************************************************************* */

	public static String fetchVariable(final byte[] pszRecord, final int nMaxChars, final char nDelimChar1,
			final char nDelimChar2, final MutableInt pnConsumedChars) {
		int i;

		for (i = 0; i < nMaxChars - 1 && pszRecord[i] != nDelimChar1 && pszRecord[i] != nDelimChar2; i++) {
		}

		pnConsumedChars.value = i;
		if (i < nMaxChars && (pszRecord[i] == nDelimChar1 || pszRecord[i] == nDelimChar2)) {
			pnConsumedChars.value++;
		}

		final byte[] pszReturnBytes = new byte[i];
		System.arraycopy(pszRecord, 0, pszReturnBytes, 0, i);

		return new String(pszReturnBytes);
	}

	/** ********************************************************************* */
	/* DDFScanVariable() */
	/*                                                                      */
	/* Establish the length of a variable length string in a */
	/* record. */
	/** ********************************************************************* */

	public static int scanVariable(final byte[] pszRecord, final int nMaxChars, final char nDelimChar) {
		int i;
		for (i = 0; i < nMaxChars - 1 && pszRecord[i] != nDelimChar; i++) {
		}
		return i;
	}
}