
package ASSET.Util.XML.Control.Observers;

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

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Summary.TimeToLaunchObserver;

abstract class TimeToLaunchObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "TimeToLaunchObserver";

	private final static String ACTIVE = "Active";

	private final static String TARGET_TYPE = "Target";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final TimeToLaunchObserver bb = (TimeToLaunchObserver) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());
		thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));

		TargetHandler.exportThis(bb.getTargetType(), thisPart, doc, TARGET_TYPE);

		// output it's attributes
		parent.appendChild(thisPart);

	}

	TargetType _targetType = null;
	boolean _isActive;

	String _name;

	public TimeToLaunchObserverHandler() {
		super(type);

		addAttributeHandler(new HandleBooleanAttribute(ACTIVE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isActive = val;
			}
		});

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});

		addHandler(new TargetHandler(TARGET_TYPE) {
			@Override
			public void setTargetType(final TargetType type) {
				_targetType = type;
			}
		});
	}

	@Override
	public void elementClosed() {
		// create ourselves
		final ScenarioObserver timeO = new TimeToLaunchObserver(_targetType, _name, _isActive);

		setObserver(timeO);

		// and reset
		_name = null;
		_targetType = null;
	}

	abstract public void setObserver(ScenarioObserver obs);
}