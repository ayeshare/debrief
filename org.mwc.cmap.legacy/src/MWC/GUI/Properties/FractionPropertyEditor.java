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

// $RCSfile: FractionPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: FractionPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:28:57  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.1  2003-05-16 08:38:00+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-28 09:25:41+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:46+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-29 07:56:49+00  administrator
// Use MWC.Trace instead of System.out
//
// Revision 1.0  2001-07-17 08:43:53+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-19 15:23:36+00  novatech
// added 30 second step size
//
// Revision 1.1  2001-01-03 13:42:50+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:22  ianmayo
// initial version
//
// Revision 1.2  2000-12-01 10:14:02+00  ian_mayo
// include smaller steps
//
// Revision 1.1  2000-09-26 10:53:10+01  ian_mayo
// Initial revision
//

package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

public class FractionPropertyEditor extends PropertyEditorSupport {

	protected Double _myFrac;

	private final String stringTags[] = { "0 - reset", "1/100", "1/50", "1/10", "1/5", "1", "5", "10", "50", "100" };

	private final double vals[] = { 0, 0.01, 0.02, 0.1, 0.2, 1, 5, 10, 50, 100 };

	@Override
	public String getAsText() {
		String res = null;
		final double current = _myFrac.doubleValue();
		for (int i = 0; i < vals.length; i++) {
			final double v = vals[i];
			if (v == current) {
				res = stringTags[i];
			}

		}

		return res;
	}

	@Override
	public String[] getTags() {
		return stringTags;
	}

	@Override
	public Object getValue() {
		return _myFrac;
	}

	@Override
	public void setAsText(final String val) {
		for (int i = 0; i < stringTags.length; i++) {
			final String thisS = stringTags[i];
			if (thisS.equals(val)) {
				_myFrac = new Double(vals[i]);
			}
		}

	}

	@Override
	public void setValue(final Object p1) {
		if (p1 instanceof Double) {
			_myFrac = (Double) p1;
		} else if (p1 instanceof String) {
			final String val = (String) p1;
			setAsText(val);
		}
	}
}
