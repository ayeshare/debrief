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

import java.awt.Color;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 */
public class InsertLabel extends CoreInsertShape {

	/**
	 * get a plottable object
	 *
	 * @param centre
	 * @param theChart
	 * @return
	 */
	@Override
	protected Plottable getPlottable(final PlainChart theChart) {

		// right, what's the area we're looking at
		final WorldArea wa = theChart.getDataArea();

		// get centre of area (at zero depth)
		final WorldLocation centre = wa.getCentreAtSurface();

		// and now wrap the shape
		final LabelWrapper theWrapper = new LabelWrapper("Blank label", centre, Color.red);

		return theWrapper;

	}

	@Override
	protected PlainShape getShape(final WorldLocation centre) {
		// don't bother, we're not generating shapes this way...
		return null;
	}

	@Override
	protected String getShapeName() {
		// don't bother, we're not generating shapes this way...
		return null;
	}
}