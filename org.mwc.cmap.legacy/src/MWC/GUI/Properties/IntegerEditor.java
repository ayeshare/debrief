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

// $RCSfile: IntegerEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: IntegerEditor.java,v $
// Revision 1.3  2004/09/03 11:10:20  Ian.Mayo
// Handle trying to edit optional attributes
//
// Revision 1.2  2004/05/25 15:28:58  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:43+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:41+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:40+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:49+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:47+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:13  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:49+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:42+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-12 08:09:28+01  administrator
// Initial revision
//

package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

public class IntegerEditor extends PropertyEditorSupport {
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public IntegerEditor() {
		super();
	}

	@Override
	public String getAsText() {
		String res = null;
		final Integer val = (Integer) super.getValue();
		if (val != null)
			res = "" + val.intValue();

		return res;
	}

	@Override
	public Object getValue() {
		return super.getValue();
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////
	@Override
	public void setValue(final Object p1) {
		// check we are receiving a string
		if (p1 instanceof String) {
			// check we can produce an integer
			try {
				final Integer val = new Integer((String) p1);
				super.setValue(val);
			} catch (final java.lang.NumberFormatException e) {
				// don't really worry, let's not update
			}
		} else {
			if (p1 instanceof Integer) {
				super.setValue(p1);
			}
		}
	}
}
