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

/**
 * Created by IntelliJ IDEA. User: ian.mayo Date: 26-Nov-2004 Time: 13:30:46 To
 * change this template use File | Settings | File Templates.
 */
public interface CanBeRelativeToTimeStepper {
	boolean isRelativeTimes();

	void setRelativeTimes(boolean relativeTimes);
}
