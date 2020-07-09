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

package MWC.GUI.Tools.Operations;

// Copyright MWC 1999

// $RCSfile: ShowVideo.java,v $
// $Author: Ian.Mayo $
// $Log: ShowVideo.java,v $
// Revision 1.2  2004/05/25 15:44:11  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:45  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:01+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:06+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:58+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 13:03:38+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-07-30 15:38:00+01  administrator
// update, to pass in the address of the properties window, and to trigger creation of some JMF-related data just to check that we have JMF loaded (so the GUI can elect to not display this data - if it wishes)
//
// Revision 1.1  2001-07-27 17:08:45+01  administrator
// add as floating toolbar if possible
//
// Revision 1.0  2001-07-27 14:08:05+01  administrator
// Initial revision
//

import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;

public class ShowVideo extends PlainTool {
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	MWC.GUI.Properties.PropertiesPanel _theProperties;

	java.awt.Component _theSubject;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	/**
	 * ShowVideo an existing data file
	 *
	 * @param theParent  parent application, where we can show the busy cursor
	 * @param theLabel   the label to put on the button
	 * @param theSubject the GUI component we are going to watch
	 */
	public ShowVideo(final ToolParent theParent, final MWC.GUI.Properties.PropertiesPanel thePanel,
			final java.awt.Component theSubject) {

		super(theParent, "Record video", "images/camera.gif");

		// store the properties window, it's the destination we have to use
		_theProperties = thePanel;

		// and remember the object we're listing to
		_theSubject = theSubject;
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	/**
	 * collate the data ready to perform the operations
	 */
	@Override
	public Action getData() {

		final Action res = null;

		final java.awt.Component comp = new MWC.GUI.Video.SwingGrabControl(_theSubject, getParent(), _theProperties);

		// see if this is a swing editor - if so we will add our panel as a floatable
		// toolbar
		if (_theProperties instanceof MWC.GUI.Properties.Swing.SwingPropertiesPanel) {
			final MWC.GUI.Properties.Swing.SwingPropertiesPanel swPanel = (MWC.GUI.Properties.Swing.SwingPropertiesPanel) _theProperties;
			swPanel.addThisPanel(comp);
		} else
			_theProperties.add(comp);

		// return the product
		return res;
	}

}
