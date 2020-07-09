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

// $RCSfile: AWTMenuItem.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTMenuItem.java,v $
// Revision 1.2  2004/05/25 15:41:08  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:58+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:46+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:59+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:54+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:09  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:24+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:31+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:02+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:55+01  sm11td
// Initial revision
//
// Revision 1.1  1999-02-01 16:08:55+00  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:33:13+00  sm11td
// Initial revision
//
package MWC.GUI.Tools.AWT;

import java.awt.MenuItem;

import MWC.GUI.Tool;

/**
 * extension of normal AWT menu to create a menu item containing a Debrief tool
 * item
 */
public class AWTMenuItem extends MenuItem implements java.awt.event.ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/**
	 * the Debrief tool we are calling
	 */
	protected Tool _theTool;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	public AWTMenuItem(final String theLabel, final Tool theTool) {
		super(theLabel);
		_theTool = theTool;
		this.addActionListener(this);
	}

	public AWTMenuItem(final Tool theTool) {
		_theTool = theTool;
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	/**
	 * callback function for when menu item if selected
	 */
	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		/**
		 * check that we have a tool declared
		 */
		if (_theTool != null)
			_theTool.execute();
	}

	protected Tool getTool() {
		return _theTool;
	}
}
