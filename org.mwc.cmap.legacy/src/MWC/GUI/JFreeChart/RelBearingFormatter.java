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

package MWC.GUI.JFreeChart;

import java.io.Serializable;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;

public class RelBearingFormatter implements formattingOperation, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void format(final XYPlot thePlot) {
		final NumberAxis theAxis = (NumberAxis) thePlot.getRangeAxis();
		theAxis.setRange(-180, +180);
		theAxis.setLabel("(Red)    " + theAxis.getLabel() + "     (Green)");

		// create some tick units suitable for degrees
		theAxis.setStandardTickUnits(CourseFormatter.getDegreeTickUnits());
		theAxis.setAutoTickUnitSelection(true);
	}
}