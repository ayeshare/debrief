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

package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.DynamicLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.TimeDisplayPainter;

/**
 * @author snpe
 *
 */
public class InsertTimeDisplayAbsolute extends CoreInsertChartFeature {

	public InsertTimeDisplayAbsolute() {
		super();
		setMultiple(true);
	}

	@Override
	public Layer getLayer() {
		return new DynamicLayer();
	}

	@Override
	protected String getLayerName() {
		return Layers.DYNAMIC_FEATURES;
	}

	/**
	 * @return
	 */
	@Override
	protected Plottable getPlottable(final PlainChart theChart) {
		return new TimeDisplayPainter();
	}
}
