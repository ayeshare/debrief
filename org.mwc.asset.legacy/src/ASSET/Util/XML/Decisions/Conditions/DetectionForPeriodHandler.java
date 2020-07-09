
package ASSET.Util.XML.Decisions.Conditions;

import ASSET.Models.Decision.TargetType;

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

import ASSET.Models.Decision.Conditions.Condition;
import ASSET.Models.Decision.Conditions.Detection;
import ASSET.Models.Decision.Conditions.DetectionForPeriod;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;

abstract public class DetectionForPeriodHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "DetectionForPeriod";

	private final static String CONTINUE_MONITORING = "ContinueMonitoring";
	// private final static String THRESHOLD = "Threshold";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final DetectionForPeriod bb = (DetectionForPeriod) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());
		thisPart.setAttribute(CONTINUE_MONITORING, writeThis(bb.getContinueMonitoring()));
		MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(bb.getRangeThreshold(), thisPart, doc);
		MWC.Utilities.ReaderWriter.XML.Util.DurationHandler.exportDuration(bb.getValidPeriod(), thisPart, doc);
		ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);

		parent.appendChild(thisPart);
	}

	WorldDistance _rangeThreshold;
	String _name;
	TargetType _myTargetType;
	Duration _myDuration;

	boolean _continueMonitoring;

	public DetectionForPeriodHandler() {
		super("DetectionForPeriod");

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(CONTINUE_MONITORING) {
			@Override
			public void setValue(final String name, final boolean value) {
				_continueMonitoring = value;
			}
		});

		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler() {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_rangeThreshold = res;
			}
		});

		addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler() {
			@Override
			public void setTargetType(final TargetType type) {
				_myTargetType = type;
			}
		});

		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.DurationHandler() {
			@Override
			public void setDuration(final Duration res) {
				_myDuration = res;
			}

		});

	}

	@Override
	public void elementClosed() {
		final Detection res = new DetectionForPeriod(_myTargetType, _rangeThreshold, _myDuration, _continueMonitoring);
		res.setName(_name);

		// finally output it
		setCondition(res);

		// and reset
		_myTargetType = null;
		_rangeThreshold = null;
		_myDuration = null;
		_name = null;
	}

	abstract public void setCondition(Condition dec);
}