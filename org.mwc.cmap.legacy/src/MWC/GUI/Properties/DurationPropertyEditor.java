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

package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DurationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: DurationPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:28:54  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.5  2003-02-16 11:42:53+00  ian_mayo
// Reflect movement of Duration utility method
//
// Revision 1.4  2002-10-28 09:25:03+00  ian_mayo
// minor tidying (from IntelliJ Idea)
//
// Revision 1.3  2002-10-11 08:34:48+01  ian_mayo
// IntelliJ optimisations
//
// Revision 1.2  2002-05-28 09:25:43+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:40+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:57+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 14:01:39+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-01-22 12:42:27+00  administrator
// Reflect new way of reading in double values
//
// Revision 1.1  2002-01-17 20:42:51+00  administrator
// General tidying up
//
// Revision 1.0  2002-01-17 16:30:38+00  administrator
// Initial revision
//
// Revision 1.0  2002-01-17 14:45:10+00  administrator
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:51+01  administrator
// Initial revision
//
import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.text.DecimalFormat;

import MWC.GenericData.Duration;

/**
 * abstract class providing core functionality necessary for editing a distance
 * value where units are provided (the return value is in minutes)
 */
abstract public class DurationPropertyEditor extends PropertyEditorSupport {
	/**
	 * the formatting object used to write to screen
	 *
	 */
	static protected DecimalFormat _formatter = new DecimalFormat("0.######");

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/**
	 * the value we are editing (in minutes)
	 */
	private Duration _myVal;
	/**
	 * the amount of columns the users wants us to create
	 *
	 */
	protected int _numColumns = 3;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	/**
	 * build the editor
	 */
	@Override
	abstract public Component getCustomEditor();

	/**
	 * get the duration text as a string
	 */
	abstract protected double getDuration() throws java.text.ParseException;

	/**
	 * get the units text as a string
	 */
	abstract protected int getUnits();

	/**
	 * extract the values currently stored in the text boxes (distance in minutes)
	 */
	@Override
	public Object getValue() {
		Duration val = null;
		try {
			// get the distance
			final double duration = getDuration();

			// get the units scale factor
			final int units = getUnits();

			// scale the distance to our output units (minutes)
			val = new Duration(duration, units);

		} catch (final NumberFormatException e) {
			MWC.Utilities.Errors.Trace.trace(e);
		} catch (final java.text.ParseException pe) {
			MWC.Utilities.Errors.Trace.trace(pe);
		}

		return val;
	}

	/**
	 * indicate that we can't just be painted, we've got to be edited
	 */
	@Override
	public boolean isPaintable() {
		return false;
	}

	/**
	 * put the data into the text fields, if they have been created yet
	 */
	public void resetData() {
		if (_myVal == null) {
			setDuration(0);
			setUnits(Duration.SECONDS);
		} else {
			// get the best units
			final int units = Duration.selectUnitsFor(_myVal.getValueIn(Duration.MILLISECONDS));
			setUnits(units);
			setDuration(_myVal.getValueIn(units));
		}
	}

	/**
	 * the the number of columns to use in the editor
	 *
	 * @param num the number of columns to show in the text field
	 */
	public void setColumns(final int num) {
		_numColumns = num;
	}

	/**
	 * set the duration text in string form
	 */
	abstract protected void setDuration(double val);

	/**
	 * set the units text in string form
	 */
	abstract protected void setUnits(int val);

	/**
	 * store the new value (in minutes)
	 */
	@Override
	public void setValue(final Object p1) {
// reset value
		_myVal = null;

		// try to catch if we are receiving a null (uninitialised) value
		if (p1 != null) {
			// check it's a Double
			if (p1 instanceof Duration) {
				// store the distance
				_myVal = (Duration) p1;

				// and update our data
				resetData();
			}
		}
	}

	/**
	 * return flag to say that we'd rather use our own (custom) editor
	 */
	@Override
	public boolean supportsCustomEditor() {
		return true;
	}

	/**
	 * update the GUI, following a new value assignment
	 *
	 */
	abstract protected void updateGUI();

}
