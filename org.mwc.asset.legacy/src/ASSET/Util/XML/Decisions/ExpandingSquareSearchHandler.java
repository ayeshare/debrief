
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

import ASSET.Models.Decision.Tactical.ExpandingSquareSearch;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

abstract public class ExpandingSquareSearchHandler extends CoreDecisionHandler {

	private final static String type = "ExpandingSquareSearch";
	private final static String CLOCKWISE = "Clockwise";
	private final static String MAX_NUM_LEGS = "MaxNumLegs";
	private final static String INITIAL_TRACK = "InitialTrack";
	private final static String START_POINT = "StartPoint";
	private final static String TRACK_SPACING = "TrackSpacing";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ExpandingSquareSearch bb = (ExpandingSquareSearch) toExport;

		// start with the parent
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		thisPart.setAttribute(INITIAL_TRACK, writeThis(bb.getInitialTrack()));
		thisPart.setAttribute(CLOCKWISE, writeThis(bb.getClockwise()));

		// do we know the number of legs?
		if (bb.getMaxLegs() != null)
			thisPart.setAttribute(MAX_NUM_LEGS, writeThis(bb.getMaxLegs().intValue()));

		// and now the objects
		ASSETLocationHandler.exportLocation(bb.getOrigin(), START_POINT, thisPart, doc);
		MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(TRACK_SPACING, bb.getTrackSpacing(),
				thisPart, doc);

		parent.appendChild(thisPart);

	}

	boolean _clockwise;
	MWC.GenericData.WorldLocation _origin;
	double _track;
	Integer _numLegs = null;

	WorldDistance _spacing;

	public ExpandingSquareSearchHandler() {
		this(type);
	}

	private ExpandingSquareSearchHandler(final String type) {
		super(type);

		addHandler(new ASSETLocationHandler(START_POINT) {
			@Override
			public void setLocation(final WorldLocation res) {
				_origin = res;
			}
		});

		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler(TRACK_SPACING) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_spacing = res;
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(INITIAL_TRACK) {
			@Override
			public void setValue(final String name, final double val) {
				_track = val;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(CLOCKWISE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_clockwise = val;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(MAX_NUM_LEGS) {
			@Override
			public void setValue(final String name, final int value) {
				_numLegs = new Integer(value);
			}
		});

	}

	@Override
	public final void elementClosed() {
		final ExpandingSquareSearch ess = new ExpandingSquareSearch(_clockwise, _track, _numLegs, _origin, _spacing,
				null);

		super.setAttributes(ess);

		setModel(ess);

		_numLegs = null;
		_origin = null;
		_spacing = null;
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);
}