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
// $RCSfile: AWTDatePropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: AWTDatePropertyEditor.java,v $
// Revision 1.3  2004/11/26 11:32:46  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.2  2004/05/25 15:29:21  Ian.Mayo
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
// Revision 1.1  2002-04-11 14:01:32+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:43+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 09:41:37+00  novatech
// factor generic processing to parent class, and provide support for NULL values
//
// Revision 1.1  2001-01-03 13:42:43+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:24  ianmayo
// initial version
//
// Revision 1.4  2000-10-09 13:35:51+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.3  2000-02-02 14:25:06+00  ian_mayo
// correct package naming
//
// Revision 1.2  1999-11-23 11:12:48+00  ian_mayo
// made into instantiations of generic editors
//
// Revision 1.1  1999-11-16 16:02:29+00  ian_mayo
// Initial revision
//
// Revision 1.2  1999-11-11 18:16:09+00  ian_mayo
// new class, now working
//
// Revision 1.1  1999-10-12 15:36:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:05:48+01  administrator
// Initial revision
//

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import MWC.GUI.Properties.DatePropertyEditor;

public class AWTDatePropertyEditor extends DatePropertyEditor {
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/**
	 * field to edit the date
	 */
	TextField _theDate;

	/**
	 * field to edit the time
	 */
	TextField _theTime;

	/**
	 * panel to hold everything
	 */
	Panel _theHolder;

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
	public java.awt.Component getCustomEditor() {
		_theHolder = new Panel();
		_theHolder.setLayout(new GridLayout(1, 0));
		_theDate = new TextField();
		_theTime = new TextField();
		_theHolder.add(new Label("Date:"));
		_theHolder.add(_theDate);
		_theHolder.add(new Label("Time:"));
		_theHolder.add(_theTime);

		resetData();
		return _theHolder;
	}

	/**
	 * get the date text as a string
	 */
	@Override
	protected String getDateText() {
		return _theDate.getText();
	}

	/**
	 * get the date text as a string
	 */
	@Override
	protected String getTimeText() {
		return _theTime.getText();
	}

	/**
	 * set the date text in string form
	 */
	@Override
	protected void setDateText(final String val) {
		_theDate.setText(val);
	}

	/**
	 * show the user how many microseconds there are
	 *
	 * @param val
	 */
	@Override
	protected void setMicroText(final long val) {
		throw new RuntimeException("Not implemented!!!");
	}

	/**
	 * set the time text in string form
	 */
	@Override
	protected void setTimeText(final String val) {
		_theTime.setText(val);
	}
}
