
package MWC.Utilities.ReaderWriter.XML.Util;

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

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class ShortLocationHandler extends MWCXMLReader {

	public static void exportLocation(final MWC.GenericData.WorldLocation loc, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final Element eLoc = doc.createElement("shortLocation");
		eLoc.setAttribute("Lat", writeThisLong(loc.getLat()));
		eLoc.setAttribute("Long", writeThisLong(loc.getLong()));
		eLoc.setAttribute("Depth", writeThis(loc.getDepth()));
		parent.appendChild(eLoc);
	}

	private double _lat;
	private double _long;

	private double _depth;

	public ShortLocationHandler() {
		// inform our parent what type of class we are
		super("shortLocation");

	}

	@Override
	public void elementClosed() {
		final MWC.GenericData.WorldLocation res = new MWC.GenericData.WorldLocation(_lat, _long, _depth);
		setLocation(res);
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(final String name, final Attributes attributes) {
		// initialise data
		_lat = _long = _depth = 0.0;

		final int len = attributes.getLength();
		for (int i = 0; i < len; i++) {

			final String nm = attributes.getQName(i);// getLocalName(i);
			final String val = attributes.getValue(i);
			try {
				if (nm.equals("Lat"))
					_lat = readThisDouble(val);
				else if (nm.equals("Long"))
					_long = readThisDouble(val);
				else if (nm.equals("Depth"))
					_depth = readThisDouble(val);
			} catch (final java.text.ParseException e) {
				MWC.Utilities.Errors.Trace.trace(e, "Failed reading in:" + nm + " value is:" + val);
			}
		}
	}

	abstract public void setLocation(MWC.GenericData.WorldLocation res);

}