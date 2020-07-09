
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

import java.util.ArrayList;
import java.util.List;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Recording.TowedArrayLocationObserver;
import ASSET.Scenario.Observers.Recording.TowedArrayLocationObserver.RecorderType;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

abstract class TowedArrayLocationObserverHandler extends CoreFileObserverHandler {

	private final static String type = "TowedArrayLocationObserver";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	TargetType _subjectName;
	List<Double> offsets;
	String messageName;
	Double defaultDepth;

	String sensorName;

	public TowedArrayLocationObserverHandler() {
		super(type);

		// add the other handlers
		addHandler(new TargetTypeHandler("SubjectToTrack") {
			@Override
			public void setTargetType(final TargetType type1) {
				_subjectName = type1;
			}
		});
		addAttributeHandler(new HandleAttribute("OFFSETS") {
			@Override
			public void setValue(final String name, final String val) {
				offsets = new ArrayList<Double>();

				final String[] items = val.split(",");
				for (int i = 0; i < items.length; i++) {
					final String string = items[i];
					offsets.add(Double.parseDouble(string));
				}
			}
		});
		addAttributeHandler(new HandleAttribute("MESSAGE_NAME") {
			@Override
			public void setValue(final String name, final String val) {
				messageName = val;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute("DEFAULT_DEPTH") {
			@Override
			public void setValue(final String name, final double value) {
				defaultDepth = value;
			}
		});
		addAttributeHandler(new HandleAttribute("SENSOR_NAME") {
			@Override
			public void setValue(final String name, final String val) {
				sensorName = val;
			}
		});
	}

	@Override
	public void elementClosed() {
		defaultDepth = 12d;

		// handle the message type
		final RecorderType recorderType;

		// sort out the recorder type, and do some checking
		switch (messageName) {
		case "TA_FORE_AFT":
			// just two lengths
			if (offsets.size() == 2) {
				// ok, safe
				recorderType = RecorderType.HDG_DEPTH;
			} else {
				System.err.println("Wrong number of array offsets supplied. Should be two (fore/after), but got "
						+ offsets.size());
				recorderType = null;
			}
			break;
		case "TA_MODULES":
			recorderType = RecorderType.HDG_DEPTH;
			break;
		case "TA_COG_REL":
			// just two lengths
			if (offsets.size() == 1) {
				// ok, safe
				recorderType = RecorderType.LOC_RELATIVE;
			} else {
				System.err.println("Wrong number of array offsets supplied. Should be one (Centre of Graviry), but got "
						+ offsets.size());
				recorderType = null;
			}
			break;
		case "TA_COG_ABS":
			// just two lengths
			if (offsets.size() == 1) {
				// ok, safe
				recorderType = RecorderType.LOC_ABS;
			} else {
				System.err.println("Wrong number of array offsets supplied. Should be one (Centre of Graviry), but got "
						+ offsets.size());
				recorderType = null;
			}
			break;
		default: {
			System.err.println("Towed Array Location Observer Handler : Message format not found for:" + messageName);
			recorderType = null;
		}
		}

		// have we got suitable data?
		if (recorderType != null) {
			// create ourselves
			final CoreObserver timeO = new TowedArrayLocationObserver(_directory, _fileName, _subjectName, _name,
					_isActive, offsets, recorderType, messageName, defaultDepth, sensorName);

			setObserver(timeO);

		}

		// and reset
		super.elementClosed();

		// clear the params
		_subjectName = null;
		messageName = null;
		defaultDepth = null;
		sensorName = null;
	}

	@Override
	abstract public void setObserver(ScenarioObserver obs);

}