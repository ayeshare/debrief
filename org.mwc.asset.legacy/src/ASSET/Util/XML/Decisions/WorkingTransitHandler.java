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

package ASSET.Util.XML.Decisions;

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Movement.Transit;
import ASSET.Models.Decision.Movement.WorkingTransit;

/**
 * Created by IntelliJ IDEA. User: ian.mayo Date: 29-Oct-2004 Time: 13:10:14 To
 * change this template use File | Settings | File Templates.
 */
abstract public class WorkingTransitHandler extends TransitHandler {

	private final static String _myType = "WorkingTransit";
	private final static String NUM_STOPS = "NumStops";
	private final static String ACTIVITY = "Activity";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(_myType);

		// get data item
		final ASSET.Models.Decision.Movement.WorkingTransit bb = (ASSET.Models.Decision.Movement.WorkingTransit) toExport;

		exportCoreAttributes(bb, thisPart, doc);

		// and our bits
		SequenceHandler.exportSequence(ACTIVITY, bb.getWorkingActivity(), thisPart, doc);
		thisPart.setAttribute(NUM_STOPS, writeThis(bb.getNumStops()));

		parent.appendChild(thisPart);

	}

	Sequence _myActivity;

	int _numStops = -1;

	public WorkingTransitHandler(final int thisDepth) {
		super(_myType);

		// add our handlers
		addHandler(new SequenceHandler(ACTIVITY, thisDepth) {
			@Override
			public void setModel(final DecisionType dec) {
				_myActivity = (Sequence) dec;
			}
		});

		addAttributeHandler(new HandleIntegerAttribute(NUM_STOPS) {
			@Override
			public void setValue(final String name, final int value) {
				_numStops = value;
			}
		});
	}

	@Override
	protected Transit getBehaviour() {
		final Transit res = new WorkingTransit(_myActivity, super._myPath, super._speed, super._looping, _numStops);
		_myActivity = null;
		_numStops = -1;
		return res;
	}

}
