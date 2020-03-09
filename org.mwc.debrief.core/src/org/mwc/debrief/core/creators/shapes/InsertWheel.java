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

package org.mwc.debrief.core.creators.shapes;

import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.WheelShape;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 *
 */
public class InsertWheel extends CoreInsertShape {

	/**
	 * produce the shape for the user
	 *
	 * @param centre the current centre of the screen
	 * @return a shape, based on the centre
	 */
	@Override
	protected PlainShape getShape(final WorldLocation centre) {
		final WorldDistance outerRadius = new WorldDistance(12000, WorldDistance.YARDS);
		// generate the shape
		final PlainShape res = new WheelShape(centre, new WorldDistance(4000, WorldDistance.YARDS), outerRadius);
		return res;
	}

	/**
	 * return the name of this shape, used give the shape an initial name
	 *
	 * @return the name of this type of shape, eg: rectangle
	 */
	@Override
	protected String getShapeName() {
		return "wheel";
	}

}