
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
import ASSET.Scenario.Observers.RemoveInAreaObserver;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import MWC.GenericData.WorldArea;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAreaHandler;

abstract class RemoveInAreaObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "RemoveInAreaObserver";

	private final static String ACTIVE = "Active";

	private final static String WATCH_TYPE = "Watch";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		throw new UnsupportedOperationException();
		// // create ourselves
		// final org.w3c.dom.Element thisPart = doc.createElement(type);
		//
		// // get data item
		// final ProximityObserver bb = (ProximityObserver) toExport;
		//
		// // output it's attributes
		// thisPart.setAttribute("Name", bb.getName());
		// thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));
		//
		// TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);
		//
		// // output it's attributes
		// parent.appendChild(thisPart);

	}

	protected TargetType _watchType = null;
	protected boolean _isActive;
	protected String _name = "Remove in area Observer";

	protected WorldArea _myArea;

	protected BatchCollatorHandler _myBatcher = new BatchCollatorHandler();

	public RemoveInAreaObserverHandler() {
		this(type);
	}

	public RemoveInAreaObserverHandler(final String type) {
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

		addHandler(new WorldAreaHandler() {

			@Override
			public void setArea(final WorldArea area) {
				_myArea = area;
			}
		});

		addHandler(new TargetHandler(WATCH_TYPE) {
			@Override
			public void setTargetType(final TargetType type1) {
				_watchType = type1;
			}
		});

		addHandler(_myBatcher);
	}

	@Override
	public void elementClosed() {
		// create ourselves
		final BatchCollator timeO = new RemoveInAreaObserver(_watchType, _myArea, _name, _isActive);

		// (possibly) set batch collation results
		_myBatcher.setData(timeO);

		setObserver(timeO);

		// and reset
		_name = "Remove in area Observer";
		_watchType = null;
		_myArea = null;
	}

	abstract public void setObserver(BatchCollator obs);

}