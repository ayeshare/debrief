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

package org.mwc.debrief.core.editors.painters.snail;

// Copyright MWC 1999, Debrief 3 Project

// $RCSfile: SnailDrawSWTSensorContact.java,v $
// @author $Author$
// @version $Revision$
// $Log: SnailDrawSWTSensorContact.java,v $
// Revision 1.2  2005/07/11 11:51:33  Ian.Mayo
// Do tidying as recommended by Eclipse
//
// Revision 1.1  2005/07/04 07:45:52  Ian.Mayo
// Initial snail implementation
//

import MWC.GenericData.Watchable;

/**
 * Class to perform custom plotting of Sensor data, when in a Snail-mode. (this
 * may include Snail-mode or relative-mode).
 */
public final class SnailDrawSWTSensorContact extends SnailDrawSWTTacticalContact {

	////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////
	public SnailDrawSWTSensorContact(final SnailDrawSWTFix plotter) {
		_fixPlotter = plotter;
	}

	@Override
	public final boolean canPlot(final Watchable wt) {
		boolean res = false;

		if (wt instanceof Debrief.Wrappers.SensorContactWrapper) {
			res = true;
		}
		return res;
	}

}
