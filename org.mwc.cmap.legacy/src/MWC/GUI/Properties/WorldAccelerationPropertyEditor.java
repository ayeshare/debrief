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
// $RCSfile: WorldAccelerationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.1 $
// $Log: WorldAccelerationPropertyEditor.java,v $
// Revision 1.1  2004/08/26 16:47:35  Ian.Mayo
// Implement more editable properties, add Acceleration property editor
//
// Revision 1.1  2004/08/26 09:46:38  Ian.Mayo
// Add world speed property editors, and setter for Area corners
//
//

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.text.DecimalFormat;

import MWC.GenericData.WorldAcceleration;

/**
 * abstract class providing core functionality necessary for editing a distance
 * value where units are provided (the return value is in minutes)
 */
abstract public class WorldAccelerationPropertyEditor extends PropertyEditorSupport {
	/**
	 * the formatting object used to write to screen
	 */
	static protected DecimalFormat _formatter = new DecimalFormat("0.######");

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/**
	 * the value we are editing (in minutes)
	 */
	private WorldAcceleration _myVal;
	/**
	 * the amount of columns the users wants us to create
	 */
	protected int _numColumns = 3;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	/**
	 * get the duration text as a string
	 */
	abstract protected double getAcceleration() throws java.text.ParseException;

	/**
	 * build the editor
	 */
	@Override
	abstract public Component getCustomEditor();

	/**
	 * get the units text as a string
	 */
	abstract protected int getUnits();

	/**
	 * extract the values currently stored in the text boxes (distance in minutes)
	 */
	@Override
	public Object getValue() {
		WorldAcceleration val = null;
		try {
			// get the distance
			final double duration = getAcceleration();

			// get the units scale factor
			final int units = getUnits();

			// scale the distance to our output units (minutes)
			val = new WorldAcceleration(duration, units);

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
			setAcceleration(0);
			setUnits(WorldAcceleration.M_sec_sec);
		} else {
			// get the best units
			setUnits(_myVal.getUnits());
			setAcceleration(_myVal.getValue());
		}
	}

	/**
	 * set the duration text in string form
	 */
	abstract protected void setAcceleration(double val);

	/**
	 * the the number of columns to use in the editor
	 *
	 * @param num the number of columns to show in the text field
	 */
	public void setColumns(final int num) {
		_numColumns = num;
	}

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
			if (p1 instanceof WorldAcceleration) {
				// store the distance
				_myVal = (WorldAcceleration) p1;

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
	 */
	abstract protected void updateGUI();

}
