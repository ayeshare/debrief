
package ASSET.Util.XML.Decisions;

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

import ASSET.Models.Decision.Tactical.SternArcClearance;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

abstract public class SternArcClearanceHandler extends CoreDecisionHandler {

	private final static String type = "SternArcClearance";

	private final static String FREQ = "Frequency";
	private final static String RANDOM = "RandomClearances";
	private final static String STYLE = "Style";
	private final static String COURSE_CHANGE = "CourseChange";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Decision.Tactical.SternArcClearance bb = (ASSET.Models.Decision.Tactical.SternArcClearance) toExport;

		// first the parent bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		thisPart.setAttribute(RANDOM, writeThis(bb.isRandomClearances()));
		thisPart.setAttribute(STYLE, bb.getStyle());
		thisPart.setAttribute(COURSE_CHANGE, writeThis(bb.getCourseChange()));

		DurationHandler.exportDuration(FREQ, bb.getFrequency(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	Duration _myFreq = null;
	boolean _random;
	String _style = null;

	Double _turnAngle = null;

	public SternArcClearanceHandler() {
		super(type);

		addHandler(new DurationHandler(FREQ) {
			@Override
			public void setDuration(final Duration res) {
				_myFreq = res;
			}
		});
		addAttributeHandler(new HandleAttribute(STYLE) {
			@Override
			public void setValue(final String name, final String val) {
				_style = val;
			}
		});
		addAttributeHandler(new HandleAttribute(RANDOM) {
			@Override
			public void setValue(final String name, final String val) {
				_random = Boolean.valueOf(val).booleanValue();
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(COURSE_CHANGE) {
			@Override
			public void setValue(final String name, final double value) {
				_turnAngle = new Double(value);
			}
		});
	}

	@Override
	public void elementClosed() {
		final SternArcClearance ev = new SternArcClearance(_myFreq, _random, _style, _turnAngle);

		// set the parent attributes
		super.setAttributes(ev);

		// finally output it
		setModel(ev);

		_myFreq = null;
		_style = null;
		_turnAngle = null;
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}