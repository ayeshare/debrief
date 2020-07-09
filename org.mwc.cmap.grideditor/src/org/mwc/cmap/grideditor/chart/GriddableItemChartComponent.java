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

package org.mwc.cmap.grideditor.chart;

import MWC.GUI.TimeStampedDataItem;

/**
 * Encapsulates a different ways to extract a chartable value from a given
 * {@link TimeStampedDataItem}.
 */
public interface GriddableItemChartComponent {

	public double getDoubleValue(TimeStampedDataItem dataItem);
}
