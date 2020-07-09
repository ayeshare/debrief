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

package interfaces;

import MWC.Algorithms.PlainProjection;
import MWC.GenericData.WorldArea;

/**
 * interface for objects who have a 2d geographic view that can be externally
 * controlled
 *
 * @author ian.mayo
 *
 */
public interface IControllableViewport {

	/**
	 * find out the full projection details
	 *
	 */
	public PlainProjection getProjection();

	/**
	 * find out the current coverage of the view
	 *
	 * @return
	 */
	public WorldArea getViewport();

	/**
	 * control the complete projection details
	 *
	 * @param proj the new projection to use
	 */
	public void setProjection(PlainProjection proj);

	/**
	 * control the current coverage of the view
	 *
	 * @param target
	 */
	public void setViewport(WorldArea target);
}
