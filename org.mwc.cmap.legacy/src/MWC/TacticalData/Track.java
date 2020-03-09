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

// $RCSfile: Track.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.9 $
// $Log: Track.java,v $
// Revision 1.9  2006/07/28 09:57:30  Ian.Mayo
// Minor optimisation, plus derivation of bounds when loading existing track
//
// Revision 1.8  2005/12/13 09:03:26  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.7  2004/12/02 16:33:46  Ian.Mayo
// Speed up fix comparisons
//
// Revision 1.6  2004/11/25 11:32:33  Ian.Mayo
// re-instate deleted method
//
// Revision 1.5  2004/11/25 10:54:18  Ian.Mayo
// Remove unused method (get final fix), and add hasFixes accessor
//
// Revision 1.4  2004/11/24 16:05:34  Ian.Mayo
// Switch to hi-res timers
//
// Revision 1.3  2004/11/01 15:56:13  Ian.Mayo
// Provide getter which returns the final vessel location
//
// Revision 1.2  2004/05/24 16:26:49  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:49  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-07-12 15:46:47+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:26:05+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:58+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:29+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-29 19:34:03+01  administrator
// Provide CloserMethod
//
// Revision 1.0  2001-07-17 08:42:49+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:37+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:08  ianmayo
// initial version
//
// Revision 1.3  1999-12-03 14:33:45+00  ian_mayo
// filled in code to keep track of data area
//
// Revision 1.2  1999-11-26 15:45:07+00  ian_mayo
// adding toString method
//
// Revision 1.1  1999-10-12 15:36:16+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:45:33+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:50:31+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-12 08:09:18+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:02+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:55+01  sm11td
// Initial revision
//
// Revision 1.2  1999-06-01 16:49:18+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.1  1999-01-31 13:32:59+00  sm11td
// Initial revision
//

package MWC.TacticalData;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;

public class Track implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////
	/**
	 * the list of fixes we are storing
	 */
	private final java.util.Vector<Fix> _theFixes;

	/**
	 * the vessel name
	 */
	private String VesselName;

	/**
	 * the name of the track
	 */
	private String _trackName;

	/**
	 * the area covered by the track
	 */
	private WorldArea _theArea;

	protected HiResDate _startDTG = null;
	protected HiResDate _endDTG = null;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * default constructor
	 */
	public Track() {
		_theFixes = new Vector<Fix>(0, 1);
	}

	/**
	 * constructor receives reference to list of fixes which make up the track
	 */
	public Track(final java.util.Vector<Fix> theFixesVal) {
		this();

		// hey, check we've received some fixes
		if (theFixesVal != null) {

			// ok - add the fixes incrementally, so we can sort out the time
			// limits
			for (final Iterator<Fix> iter = theFixesVal.iterator(); iter.hasNext();) {
				final Fix thisFix = iter.next();
				addFix(thisFix);
			}
		}
	}

	/**
	 * add the fix to our list, also extend our area
	 */
	public void addFix(final Fix theFix) {
		// add to vector
		_theFixes.addElement(theFix);

		// extend the area to include this
		if (_theArea == null) {
			_theArea = new WorldArea(theFix.getLocation(), theFix.getLocation());
		} else {
			_theArea.extend(theFix.getLocation());
		}

		// extend the time period
		final HiResDate thisTime = theFix.getTime();

		final long myTime = thisTime.getMicros();

		if (_startDTG == TimePeriod.INVALID_DATE) {
			_startDTG = _endDTG = thisTime;
		} else {
			if (myTime < _startDTG.getMicros()) {
				_startDTG = thisTime;
			}

			if (myTime > _endDTG.getMicros()) {
				_endDTG = thisTime;
			}
		}
	}

	// ////////////////////////////////////////////////
	// member functions
	// ////////////////////////////////////////////////

	public void closeMe() {
		// empty out the fixes
		_theFixes.removeAllElements();
	}

	/**
	 * Retrieve the area of the track
	 */
	public WorldArea getDataArea() {
		return _theArea;
	}

	public HiResDate getEndDTG() {
		return _endDTG;
	}

	/**
	 * get the last fix - convenience function called from ASSET
	 *
	 * @return the last fix
	 */
	public Fix getFinalFix() {
		return _theFixes.lastElement();
	}

	public Enumeration<Fix> getFixes() {
		return _theFixes.elements();
	}

	public String getName() {
		return _trackName;
	}

	public HiResDate getStartDTG() {
		return _startDTG;
	}

	/**
	 * Get
	 */
	public String getVesselName() {
		return VesselName;
	}

	/**
	 * do we have any data?
	 *
	 * @return yes/no
	 */
	public boolean hasFixes() {
		return !_theFixes.isEmpty();
	}

	public void setName(final String theName) {
		_trackName = theName;
	}

	/**
	 * Set
	 */
	public void setVesselName(final String VesselName) {
		this.VesselName = VesselName;
		setName(VesselName);
	}

	/**
	 * return this item as a string
	 */
	@Override
	public String toString() {
		return getName();
	}

}
