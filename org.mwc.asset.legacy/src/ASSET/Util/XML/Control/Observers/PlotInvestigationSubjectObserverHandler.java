
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
import ASSET.Scenario.Observers.DetectionObserver;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Plotting.PlotInvestigationSubjectObserver;

abstract public class PlotInvestigationSubjectObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "PlotInvestigationSubjectObserver";

	final static String ACTIVE = "Active";
	final static String WATCH_TYPE = "Watch";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(getType());

		// get data item
		final PlotInvestigationSubjectObserver bb = (PlotInvestigationSubjectObserver) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());
		thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));
		TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);

		// output it's attributes
		parent.appendChild(thisPart);

	}

	static String getType() {
		return type;
	}

	TargetType _watchType = null;

	boolean _isActive;

	String _name;

	public PlotInvestigationSubjectObserverHandler() {
		this(type);
	}

	PlotInvestigationSubjectObserverHandler(final String obs_type) {
		super(obs_type);

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

		addHandler(new TargetHandler(WATCH_TYPE) {
			@Override
			public void setTargetType(final TargetType type) {
				_watchType = type;
			}
		});
	}

	@Override
	public void elementClosed() {
		final PlotInvestigationSubjectObserver poi = new PlotInvestigationSubjectObserver(_watchType, _name, _isActive);
		setObserver(poi);
		// and reset
		_name = null;
		_watchType = null;
	}

	DetectionObserver getObserver(final TargetType watch, final TargetType target, final String name,
			final Integer detectionLevel, final boolean isActive) {
		return new DetectionObserver(watch, target, name, detectionLevel, isActive);
	}

	abstract public void setObserver(ScenarioObserver obs);

}