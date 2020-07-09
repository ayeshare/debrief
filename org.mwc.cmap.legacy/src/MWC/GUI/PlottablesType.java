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

package MWC.GUI;

public interface PlottablesType extends CanEnumerate {

	/**
	 * what area do we cover?
	 *
	 * @return the area, or null
	 */
	MWC.GenericData.WorldArea getBounds();

	/**
	 * the name of this set of plottables
	 *
	 * @return my name
	 */
	String getName();

	/**
	 * paint this list of plottables
	 *
	 * @param dest the graphics destination
	 */
	void paint(CanvasType dest);

	/**
	 * how big is the list?
	 *
	 * @return the length of the list
	 */
	int size();
}
