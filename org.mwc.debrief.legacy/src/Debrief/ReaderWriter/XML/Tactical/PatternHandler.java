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

package Debrief.ReaderWriter.XML.Tactical;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

// Copyright MWC 2001, Debrief 3 Project
// $RCSfile: PatternHandler.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: PatternHandler.java,v $
// Revision 1.4  2004/11/25 10:24:24  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/22 13:40:58  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.2  2004/09/09 10:23:02  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:48:16  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-06-04 10:18:42+01  ian_mayo
// Convert labels to & from XML to allow multi-line labels
//
// Revision 1.3  2003-03-19 15:37:25+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:19+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:01+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:14+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-01-22 12:43:33+00  administrator
// Reflect new way of reading in doubles
//
// Revision 1.3  2002-01-15 09:14:51+00  administrator
// correct mis-usage of Debrief Layer handler, switch to Debrief class which inherits LayerHandler
//
// Revision 1.2  2001-11-20 11:31:08+00  administrator
// Switch boolean attribute handler
//
// Revision 1.1  2001-11-14 19:38:32+00  administrator
// switch to new XML(SAX2) reader structure
//
// Revision 1.0  2001-07-17 08:41:24+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-26 11:21:17+00  novatech
// store and remember the values of BuoySymbolType and BuoySymbolSize
//
// Revision 1.2  2001-01-18 13:18:28+00  novatech
// remember the buoy colour
//
// Revision 1.1  2001-01-17 13:23:05+00  novatech
// Initial revision
//

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.TimeRangeHandler;

public final class PatternHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LocationPropertyEditor lp = new MWC.GUI.Properties.LocationPropertyEditor();

	public static void exportTrack(final Debrief.Wrappers.BuoyPatternWrapper pattern, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		/*
		 * <!ELEMENT pattern (colour, timeRange?, font?,((textlabel)*))> <!ATTLIST
		 * pattern Name CDATA #REQUIRED Visible (TRUE|FALSE) "TRUE" NameVisible
		 * (TRUE|FALSE) "TRUE" NameLocation (Top|Left|Bottom|Centre|Right) "Right" >
		 */

		final Element fld = doc.createElement("pattern");
		fld.setAttribute("Name", pattern.getName());
		fld.setAttribute("Visible", writeThis(pattern.getVisible()));
		fld.setAttribute("NameVisible", writeThis(pattern.getNameVisible()));
		lp.setValue(pattern.getNameLocation());
		fld.setAttribute("NameLocation", lp.getAsText());
		fld.setAttribute("BuoySymbolType", pattern.getBuoySymbolType());
		fld.setAttribute("BuoySymbolSize", writeThis(pattern.getBuoySymbolSize()));
		ColourHandler.exportColour(pattern.getColor(), fld, doc);

		// sort out the time range, if it looks like we have any time data
		if (pattern.getStartDTG() != null) {
			TimeRangeHandler.exportThis(pattern.getStartDTG(), pattern.getEndDTG(), fld, doc);
		}

		// and the font
		final java.awt.Font theFont = pattern.getFont();
		if (theFont != null) {
			FontHandler.exportFont(theFont, fld, doc);
		}

		// now the points
		final java.util.Enumeration<Editable> iter = pattern.elements();
		while (iter.hasMoreElements()) {
			final MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
			// make use of the static method in the DebriefLayerHandler which handles all
			// sorts of plottables
			Debrief.ReaderWriter.XML.DebriefLayerHandler.exportThisDebriefItem(pl, fld, doc);
		}

		parent.appendChild(fld);

	}

	private final MWC.GUI.Layers _theLayers;

	// our "working" track
	Debrief.Wrappers.BuoyPatternWrapper _myPattern;

	public PatternHandler(final MWC.GUI.Layers theLayers) {
		// inform our parent what type of class we are
		super("pattern");

		// store the layers object, so that we can add ourselves to it
		_theLayers = theLayers;

		addHandler(new ColourHandler() {
			@Override
			public void setColour(final java.awt.Color res) {
				_myPattern.setColor(res);
				_myPattern.setBuoyColor(res);
			}
		});
		addHandler(new Debrief.ReaderWriter.XML.Shapes.LabelHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});
		addHandler(new FontHandler() {
			@Override
			public void setFont(final java.awt.Font font) {
				_myPattern.setFont(font);
			}
		});
		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_myPattern.setName(fromXML(val));
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible") {
			@Override
			public void setValue(final String name, final boolean val) {
				_myPattern.setVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("NameVisible") {
			@Override
			public void setValue(final String name, final boolean val) {
				_myPattern.setNameVisible(val);
			}
		});
		addAttributeHandler(new HandleAttribute("NameLocation") {
			@Override
			public void setValue(final String name, final String val) {
				lp.setAsText(val);
				_myPattern.setNameLocation((Integer) lp.getValue());
			}
		});
		addHandler(new TimeRangeHandler() {
			@Override
			public void setTimeRange(final HiResDate start, final HiResDate end) {
				_myPattern.setStartDTG(start);
				_myPattern.setEndDTG(end);
			}
		});
		addAttributeHandler(new HandleAttribute("BuoySymbolType") {
			@Override
			public void setValue(final String name, final String val) {
				_myPattern.setBuoySymbolType(val);
			}
		});
		addAttributeHandler(new HandleAttribute("BuoySymbolSize") {
			@Override
			public void setValue(final String name, final String val) {
				try {
					_myPattern.setBuoySymbolSize(readThisDouble(val));
				} catch (final java.text.ParseException pe) {
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:" + val);
				}

			}
		});

	}

	void addThis(final MWC.GUI.Plottable val) {
		_myPattern.add(val);
		// if this is a LabelWrapper, we need to set it's parent
		if (val instanceof LabelWrapper) {
			final LabelWrapper lw = (LabelWrapper) val;
			lw.setParent(_myPattern);
		}
	}

	@Override
	public final void elementClosed() {
		// our layer is complete, add it to the parent!
		_theLayers.addThisLayerAllowDuplication(_myPattern);

		_myPattern = null;
	}

	// this is one of ours, so get on with it!
	@Override
	protected final void handleOurselves(final String name, final Attributes attributes) {
		_myPattern = new Debrief.Wrappers.BuoyPatternWrapper();

		super.handleOurselves(name, attributes);

	}

}