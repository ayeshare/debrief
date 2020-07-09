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

package Debrief.GUI.Tote.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawTMAContact.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: SnailDrawTMAContact.java,v $
// Revision 1.5  2005/12/13 09:04:26  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2005/02/22 09:31:57  Ian.Mayo
// Refactor snail plotting sensor & tma data - so that getting & managing valid data points are handled in generic fashion.  We did have two very similar implementations, tracking errors introduced after hi-res-date changes was proving expensive/unreliable.  All fine now though.
//
// Revision 1.3  2005/01/24 10:29:01  Ian.Mayo
// Pass the parent track to the item when painting in snail mode.  We were just passing null before for sensor data
//
// Revision 1.2  2004/11/25 10:24:04  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:22  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.2  2003-06-23 13:40:55+01  ian_mayo
// Minor tidying, now complete
//
// Revision 1.1  2003-06-23 11:02:38+01  ian_mayo
// Initial revision
//

import Debrief.Wrappers.TMAContactWrapper;
import MWC.GenericData.Watchable;

/**
 * Class to perform custom plotting of tma solution data, when in a Snail-mode.
 * (this may include Snail-mode or relative-mode).
 */
public final class SnailDrawTMAContact extends SnailDrawTacticalContact {

	////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////
	public SnailDrawTMAContact(final SnailDrawFix plotter) {
		_fixPlotter = plotter;
	}

	///////////////////////////////////
	// member functions
	//////////////////////////////////

	@Override
	public final boolean canPlot(final Watchable wt) {
		boolean res = false;

		if (wt instanceof TMAContactWrapper) {
			res = true;
		}
		return res;
	}
}
