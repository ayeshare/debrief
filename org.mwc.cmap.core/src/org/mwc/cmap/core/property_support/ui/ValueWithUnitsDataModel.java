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

package org.mwc.cmap.core.property_support.ui;

public interface ValueWithUnitsDataModel {

	/**
	 * marshall the current specified values into a results object
	 *
	 * @param dist  the value typed in
	 * @param units the units for the value
	 * @return an object representing the new data value
	 */
	public Object createResultsObject(double dist, int units);

	/**
	 * get the current value
	 *
	 * @return
	 */
	public double getDoubleValue();

	/**
	 * get the list of units to supply
	 *
	 * @return
	 */
	public String[] getTagsList();

	/**
	 * get the current selection
	 *
	 * @return
	 */
	public int getUnitsValue();

	/**
	 * unmarshall the supplied object into it's child attributes
	 *
	 * @param value
	 */
	public void storeMe(Object value);
}
