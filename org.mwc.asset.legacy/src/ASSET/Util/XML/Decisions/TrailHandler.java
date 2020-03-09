
package ASSET.Util.XML.Decisions;

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

import ASSET.Models.Decision.Movement.Trail;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class TrailHandler extends CoreDecisionHandler {

	private final static String type = "Trail";
	private final static String ALLOWABLE_ERROR = "AllowableError";
	private final static String ALLOWABLE_SPEED = "AllowSpeedChange";
	private final static String RANGE = "TrailRange";
	private final static String TRAIL_HEIGHT = "Height";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Decision.Movement.Trail bb = (ASSET.Models.Decision.Movement.Trail) toExport;

		// first the parent bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		if (bb.getTrailHeight() != null) {
			WorldDistanceHandler.exportDistance(TRAIL_HEIGHT, bb.getTrailHeight(), thisPart, doc);
		}
		ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);
		WorldDistanceHandler.exportDistance(RANGE, bb.getTrailRange(), thisPart, doc);
		WorldDistanceHandler.exportDistance(ALLOWABLE_ERROR, bb.getAllowableError(), thisPart, doc);

		thisPart.setAttribute(ALLOWABLE_SPEED, "" + bb.isAllowSpeedChange());

		parent.appendChild(thisPart);

	}

	TargetType _myTargetType;
	WorldDistance _myRange;
	WorldDistance _myAllowableError;
	WorldDistance _trailHeight = null;

	Boolean _myAllowSpeedChange;

	public TrailHandler() {
		super(type);

		addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler() {
			@Override
			public void setTargetType(final ASSET.Models.Decision.TargetType type) {
				_myTargetType = type;
			}
		});
		addHandler(new WorldDistanceHandler(RANGE) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_myRange = res;
			}
		});
		addHandler(new WorldDistanceHandler(TRAIL_HEIGHT) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_trailHeight = res;
			}
		});
		addHandler(new WorldDistanceHandler(ALLOWABLE_ERROR) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_myAllowableError = res;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(ALLOWABLE_SPEED) {
			@Override
			public void setValue(final String name, final boolean value) {
				_myAllowSpeedChange = new Boolean(value);
			}
		});

	}

	@Override
	public void elementClosed() {
		final Trail tr = new Trail(_myRange);
		tr.setTargetType(_myTargetType);
		tr.setAllowableError(_myAllowableError);

		if (_myAllowSpeedChange != null)
			tr.setAllowSpeedChange(_myAllowSpeedChange);

		super.setAttributes(tr);

		if (_trailHeight != null)
			tr.setTrailHeight(_trailHeight);

		setModel(tr);

		_myRange = null;
		_trailHeight = null;
		_myTargetType = null;
		_myAllowableError = null;
		_myAllowSpeedChange = null;

	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}