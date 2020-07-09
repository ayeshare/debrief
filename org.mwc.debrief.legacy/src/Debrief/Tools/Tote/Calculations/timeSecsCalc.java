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

package Debrief.Tools.Tote.Calculations;

import java.text.DecimalFormat;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: timeSecsCalc.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: timeSecsCalc.java,v $
// Revision 1.5  2006/03/16 16:01:09  Ian.Mayo
// Override not-applicable statement
//
// Revision 1.4  2004/11/29 11:34:45  Ian.Mayo
// Show correct units for hi-res data
//
// Revision 1.3  2004/11/29 10:56:03  Ian.Mayo
// Only output time if the object has a valid time
//
// Revision 1.2  2004/11/25 10:24:41  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:18  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.6  2003-03-19 15:36:58+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.5  2003-02-10 16:27:46+00  ian_mayo
// Reflect name change of get wrappable data
//
// Revision 1.4  2003-02-07 15:36:08+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.3  2003-01-15 15:29:31+00  ian_mayo
// Show "n/a" if not valid time
//
// Revision 1.2  2002-07-23 08:48:51+01  ian_mayo
// Put into the correct time zone
//
// Revision 1.1  2002-06-17 11:01:44+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-28 09:25:12+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:42+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:36+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:12+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:26+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:05  ianmayo
// initial import of files
//
// Revision 1.1  2000-09-14 10:25:04+01  ian_mayo
// Initial revision
//
// Revision 1.3  2000-08-14 11:00:55+01  ian_mayo
// reflect correct use of units
//
// Revision 1.2  2000-05-19 11:23:43+01  ian_mayo
// provided n/a result string when secondary watchable not present
//
// Revision 1.1  1999-10-12 15:34:21+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:03:04+01  administrator
// Initial revision
//

import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public final class timeSecsCalc extends plainCalc {
	private java.text.SimpleDateFormat _myDateFormat = null;
	private java.text.SimpleDateFormat _milliSecsFormat = null;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	public timeSecsCalc() {
		super(new DecimalFormat("00.00"), "Time", "HHmm.ss");

		// create the format
		_myDateFormat = new GMTDateFormat("HHmm.ss");
		_milliSecsFormat = new GMTDateFormat("s");

		// just over-ride the units when we're in hi-res mode
		if (HiResDate.inHiResProcessingMode()) {
			super.setUnits("secs");
		}

	}
	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	@Override
	public final double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime) {
		double res = 0.0;
		if (primary != null) {
			res = primary.getTime().getMicros();
		} else
			res = secondary.getTime().getMicros();

		return res;
	}

	/**
	 * does this calculation require special bearing handling (prevent wrapping
	 * through 360 degs)
	 */
	@Override
	public final boolean isWrappableData() {
		return false;
	}

	@Override
	public synchronized final String update(final Watchable primary, final Watchable secondary, final HiResDate time) {
		String res = NOT_APPLICABLE;
		long val = 0;
		if (primary != null) {
			// HI-RES NOT DONE - should be able to plot times in micros
			final HiResDate theDTG = primary.getTime();

			// is it a valid time?
			if (theDTG != null) {
				val = theDTG.getDate().getTime();

				// hmm, are we in hi-res mode?
				if (HiResDate.inHiResProcessingMode()) {
					res = _milliSecsFormat.format(theDTG.getDate()) + "." + DebriefFormatDateTime.formatMicros(theDTG);
				} else {
					res = _myDateFormat.format(new java.util.Date(val));
				}

			} else
				res = NOT_APPLICABLE;
		}

		return res;
	}

}
