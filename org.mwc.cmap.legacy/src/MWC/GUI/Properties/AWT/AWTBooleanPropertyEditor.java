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

package MWC.GUI.Properties.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTBooleanPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTBooleanPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:29:18  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:25  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:45+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:31+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:42+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:42+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:24  ianmayo
// initial version
//
// Revision 1.2  1999-11-23 11:12:49+00  ian_mayo
// made into instantiations of generic editors
//
// Revision 1.1  1999-11-16 16:02:10+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-10-12 15:36:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:05:48+01  administrator
// Initial revision
//

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Panel;
import java.beans.PropertyEditorSupport;

public class AWTBooleanPropertyEditor extends PropertyEditorSupport {
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	boolean _myVal;
	Checkbox _theBox;
	Panel _theHolder;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	@Override
	public java.awt.Component getCustomEditor() {
		_theHolder = new Panel();
		_theHolder.setLayout(new BorderLayout());
		_theBox = new Checkbox();
		_theHolder.add("West", _theBox);
		resetData();
		return _theHolder;
	}

	@Override
	public Object getValue() {
		final Boolean val = new Boolean(_theBox.getState());
		return val;
	}

	/**
	 * indicate that we can't just be painted, we've got to be edited
	 */
	@Override
	public boolean isPaintable() {
		return false;
	}

	public void resetData() {
		if (_theBox != null)
			_theBox.setState(_myVal);
	}

	@Override
	public void setValue(final Object p1) {
		if (p1 instanceof Boolean) {
			final Boolean val = (Boolean) p1;
			_myVal = val.booleanValue();
		} else
			return;
	}

	@Override
	public boolean supportsCustomEditor() {
		return true;
	}

}
