/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.plotViewer.actions;

import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import MWC.GUI.Layer;
import MWC.GUI.Plottable;

/** interface for editors that contain a chart (so the Debrief buttons can apply to them)
 * 
 * @author Administrator
 *
 */
public interface IChartBasedEditor
{
	public SWTChart getChart();

	public void selectPlottable(Plottable shape, Layer layer);
}
