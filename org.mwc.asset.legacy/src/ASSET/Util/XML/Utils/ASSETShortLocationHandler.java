
package ASSET.Util.XML.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class ASSETShortLocationHandler extends MWCXMLReader {
	private final static String type = "shortLocation";
	private final static String LAT = "Lat";
	private final static String LONG = "Long";
	private final static String HEIGHT = "Height";

	public static void exportLocation(final MWC.GenericData.WorldLocation loc, final Element parent,
			final Document doc) {
		final Element eLoc = doc.createElement(type);
		eLoc.setAttribute(LAT, writeThisLong(loc.getLat()));
		eLoc.setAttribute(LONG, writeThisLong(loc.getLong()));

		// now output the depth
		final WorldDistance depth = new WorldDistance(-loc.getDepth(), WorldDistance.METRES);
		WorldDistanceHandler.exportDistance(HEIGHT, depth, eLoc, doc);

		parent.appendChild(eLoc);
	}

	double _lat;
	double _long;

	WorldDistance _height;

	public ASSETShortLocationHandler() {
		// inform our parent what type of class we are
		super(type);

		addAttributeHandler(new HandleDoubleAttribute(LAT) {
			@Override
			public void setValue(final String name, final double val) {
				_lat = val;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(LONG) {
			@Override
			public void setValue(final String name, final double val) {
				_long = val;
			}
		});

		addHandler(new WorldDistanceHandler(HEIGHT) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_height = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		MWC.GenericData.WorldLocation res;

		if (_height != null)
			res = new WorldLocation.LocalLocation(_lat, _long, _height);
		else
			res = new WorldLocation(_lat, _long, 0);

		setLocation(res);

		_height = null;
		_lat = Double.NaN;
		_long = Double.NaN;
	}

	abstract public void setLocation(MWC.GenericData.WorldLocation res);

}