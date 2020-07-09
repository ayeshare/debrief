
package ASSET.Util.XML.Vessels.Util;

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

import java.text.ParseException;

import ASSET.Participants.Status;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class StatusHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "Status";

	static public void exportThis(final ASSET.Participants.Status toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		// create the element
		final org.w3c.dom.Element stat = doc.createElement(type);

		// set the attributes
		stat.setAttribute("Id", writeThis(toExport.getId()));
		stat.setAttribute("Course", writeThis(toExport.getCourse()));
		stat.setAttribute("Fuel", writeThis(toExport.getFuelLevel()));
		stat.setAttribute("Time", writeThisInXML(new java.util.Date(toExport.getTime())));
		ASSETLocationHandler.exportLocation(toExport.getLocation(), "Location", stat, doc);
		WorldSpeedHandler.exportSpeed(toExport.getSpeed(), stat, doc);

		// add to parent
		parent.appendChild(stat);

	}

	double _myCourse;
	WorldSpeed _mySpeed;
	double _myFuel;
	long _myTime = -1;
	int _myId;

	MWC.GenericData.WorldLocation _myLoc;

	public StatusHandler() {
		super(type);

		super.addAttributeHandler(new HandleAttribute("Course") {
			@Override
			public void setValue(final String name, final String val) {
				try {
					_myCourse = MWCXMLReader.readThisDouble(val);
				} catch (final ParseException e) {
					MWC.Utilities.Errors.Trace.trace(e);
				}
			}
		});

		addHandler(new WorldSpeedHandler() {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_mySpeed = res;
			}
		});
		super.addAttributeHandler(new HandleAttribute("Fuel") {
			@Override
			public void setValue(final String name, final String val) {
				try {
					_myFuel = MWCXMLReader.readThisDouble(val);
				} catch (final ParseException e) {
					MWC.Utilities.Errors.Trace.trace(e);
				}
			}
		});
		super.addAttributeHandler(new HandleAttribute("Id") {
			@Override
			public void setValue(final String name, final String val) {
				_myId = Integer.parseInt(val);
			}
		});
		addAttributeHandler(new HandleDateTimeAttribute("Time") {
			@Override
			public void setValue(final String name, final long value) {
				_myTime = value;
			}
		});
		addHandler(new ASSETLocationHandler("Location") {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_myLoc = res;
			}
		});

	}

	@Override
	public void elementClosed() {
		// create the category
		final Status stat = new Status(_myId, _myTime);
		stat.setCourse(_myCourse);
		stat.setSpeed(_mySpeed);
		stat.setFuelLevel(_myFuel);
		stat.setLocation(_myLoc);

		setStatus(stat);

		// and reset
		_myTime = -1;
		_mySpeed = null;
		_myLoc = null;
	}

	abstract public void setStatus(ASSET.Participants.Status stat);

}