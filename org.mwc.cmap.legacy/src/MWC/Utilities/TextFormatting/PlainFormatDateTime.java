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

// @Author : Ian Mayo
// @Project: Debrief 3
// @File   : PlainFormatDateTime.java

package MWC.Utilities.TextFormatting;

/**
 * class defining how to format a date-time field
 */
abstract public class PlainFormatDateTime {
	public abstract String getExample();

	public abstract String toString(java.util.Date theTime);
}
