
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
package MWC.GUI.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTStatusBar.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTStatusBar.java,v $
// Revision 1.2  2004/05/24 16:29:22  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:05  Ian.Mayo
// Initial import
//
// Revision 1.5  2002-12-16 15:39:05+00  ian_mayo
// Reflect fact that location of units labels has changed
//
// Revision 1.4  2002-10-30 16:27:02+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-10-28 09:24:29+00  ian_mayo
// minor tidying (from IntelliJ Idea)
//
// Revision 1.2  2002-05-28 09:25:37+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:22+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:33+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-14 11:58:52+01  novatech
// include comments, support class to format range data, editable type and property editor for range units
//
// Revision 1.1  2001-01-03 13:43:04+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:03  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:37:04+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:49+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-23 14:04:01+01  administrator
// Initial revision
//

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import MWC.GUI.Editable;
import MWC.GUI.StatusBar;
import MWC.GUI.Properties.UnitsPropertyEditor;

public class AWTStatusBar extends Panel implements StatusBar {
	static public class StatusBarSupport implements MWC.GUI.Editable {
		//////////////////////////////////////////////////////
		// bean info for this class
		/**
		 * ///////////////////////////////////////////////////// the set of editable
		 * properties for a status bar
		 */
		public class StatusInfo extends Editable.EditorType {

			/**
			 * constructor, takes the status bar we are editing
			 *
			 */
			public StatusInfo(final StatusBarSupport data) {
				super(data, data.getName(), "");
			}

			@Override
			public PropertyDescriptor[] getPropertyDescriptors() {
				try {
					final PropertyDescriptor[] res = {
							longProp("Units", "the units for display", UnitsPropertyEditor.class) };

					return res;

				} catch (final IntrospectionException e) {
					return super.getPropertyDescriptors();
				}
			}

		}

		/**
		 * the parent which provides us with our properties
		 */
		protected MWC.GUI.ToolParent _theParent;

		/**
		 * our editor
		 */
		transient private Editable.EditorType _myEditor;

		/**
		 * <init>
		 *
		 */
		public StatusBarSupport() {
			// load the data
		}

		/**
		 * format this bearing using the customer preference
		 *
		 * @param brg the bearing in radians
		 */
		public String formatBearing(final double brg) {
			// prepare the bearing, since there's little doubt about this
			double bearing = MWC.Algorithms.Conversions.Rads2Degs(brg);

			// clip the data
			if (bearing < 0) {
				bearing += 360.0;
			}

			// format it using the standard style
			return MWC.Utilities.TextFormatting.GeneralFormat.formatBearing(bearing);
		}

		/**
		 * format this range using the customer preference
		 *
		 * @param range range in degrees
		 */
		public String formatRange(final double range) {

			String theUnits = "";
			double theRng = 0;
			String rngStr;

			// find out which type it is
			if (_theParent != null)
				theUnits = _theParent.getProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);

			if (theUnits == null)
				theUnits = MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS;

			if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS)
					|| theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.OLD_YDS_UNITS)) {
				theRng = MWC.Algorithms.Conversions.Degs2Yds(range);
			} else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.KYD_UNITS)) {
				theRng = MWC.Algorithms.Conversions.Degs2Yds(range) / 1000;
			} else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.METRES_UNITS)) {
				theRng = MWC.Algorithms.Conversions.Degs2m(range);
			} else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.KM_UNITS)) {
				theRng = MWC.Algorithms.Conversions.Degs2Km(range);
			} else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.NM_UNITS)) {
				theRng = MWC.Algorithms.Conversions.Degs2Nm(range);
			} else {
				MWC.Utilities.Errors.Trace.trace("Range/Bearing units in properties file may be corrupt");
			}

			rngStr = MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(theRng);

			return rngStr + " " + theUnits;
		}

		@Override
		public Editable.EditorType getInfo() {
			if (_myEditor == null)
				_myEditor = new StatusInfo(this);

			return _myEditor;
		}

		@Override
		public String getName() {
			return "Status bar properties";
		}

		/////////////////////////
		// editable stuff
		/////////////////////////

		/////////////////////
		// support for the editor class
		/////////////////////
		public String getUnits() {
			String theUnits = "";

			// find out which type it is
			if (_theParent != null)
				theUnits = _theParent.getProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);

			return theUnits;
		}

		@Override
		public boolean hasEditor() {
			return true;
		}

		/**
		 * set the application properties for the status bar. it's this properties file
		 * which provides the status bar with the details of the units it is to use
		 *
		 */
		public void setParent(final MWC.GUI.ToolParent parent) {
			_theParent = parent;
		}

		public void setUnits(final String val) {
			if (_theParent != null)
				_theParent.setProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY, val);
		}

		@Override
		public String toString() {
			return getName();
		}

		////////////////////////////////////
		// property editor class to let us set range units

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * MWC.GUI.AWT.AWTStatusBar.StatusBarSupport
	 */
	/////////////////////////////////////////////////////////////
	// member variables
	/**
	 * //////////////////////////////////////////////////////////// the text label
	 * we are handling
	 */
	protected Label theText;

	/**
	 * support class to help us format the text & set the correct unites
	 */
	protected MWC.GUI.AWT.AWTStatusBar.StatusBarSupport _support;

	/////////////////////////////////////////////////////////////
	// constructor
	/**
	 * ////////////////////////////////////////////////////////////
	 *
	 */
	public AWTStatusBar(final MWC.GUI.Properties.PropertiesPanel panel, final MWC.GUI.ToolParent parent) {
		theText = new Label("  ");
		theText.setAlignment(Label.CENTER);
		final BorderLayout lm = new BorderLayout();
		setLayout(lm);
		add("Center", theText);

		_support = new MWC.GUI.AWT.AWTStatusBar.StatusBarSupport();
		_support.setParent(parent);

		// we should probably do something about the properties panel, another day..
	}

	/**
	 * getInsets
	 *
	 * @return the returned Insets
	 */
	@Override
	public Insets getInsets() {
		return new Insets(6, 6, 6, 6);
	}

	/**
	 * paint
	 *
	 * @param p1 parameter for paint
	 */
	@Override
	public void paint(final Graphics p1) {
		super.paint(p1);

		final Rectangle rt = super.getBounds();
		p1.setColor(Color.lightGray);
		p1.draw3DRect(1, 1, rt.width - 3, rt.height - 3, false);
	}

	/**
	 * set range and bearing data in this text panel
	 *
	 * @param range   the range in degrees
	 * @param bearing the bearing in radians
	 */
	@Override
	public void setRngBearing(final double range, final double bearing) {
		final String rngStr = _support.formatRange(range);
		final String brgStr = _support.formatBearing(bearing);

		setText(rngStr + " " + brgStr);
	}

	////////////////////////////////////////////////////
	// support class which provides formatting support for range/bearing
	////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// member functions
	/**
	 * //////////////////////////////////////////////////////////// set the text in
	 * the label
	 *
	 * @param theVal the text to display
	 */

	@Override
	public void setText(final String theVal) {
		theText.setText(theVal);
	}

}