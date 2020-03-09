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

package Debrief.Wrappers;

import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GenericData.WatchableList;

public interface ISecondaryTrack extends WatchableList, HasEditables {

	/**
	 * is this track configured to interpolate points?
	 *
	 * @return yes/no
	 */
	public abstract boolean getInterpolatePoints();

	/**
	 * get the legs
	 *
	 * @return
	 */
	Enumeration<Editable> segments();

	/**
	 * specify if this track should interpolate points when receiving a
	 * "getNEarestTo" call
	 *
	 * @param val yes/no
	 */
	public abstract void setInterpolatePoints(final boolean val);

}
