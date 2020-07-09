
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

import ASSET.Models.Decision.Movement.Move;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class MoveHandler extends CoreDecisionHandler {

	private final static String type = "Move";
	private final static String SPEED = "Speed";
	private final static String COURSE = "Course";
	private final static String VECTOR = "Vector";
	private final static String HEIGHT = "Height";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final Move bb = (Move) toExport;

		// output it's attributes
		CoreDecisionHandler.exportThis(bb, thisPart, doc);
		if (bb.getSpeed() != null)
			WorldSpeedHandler.exportSpeed(SPEED, bb.getSpeed(), thisPart, doc);
		if (bb.getCourse() != null)
			thisPart.setAttribute(COURSE, writeThis(bb.getCourse().doubleValue()));
		if (bb.getHeight() != null)
			WorldDistanceHandler.exportDistance(HEIGHT, bb.getHeight(), thisPart, doc);

		if (bb.getDistance() != null)
			WorldDistanceHandler.exportDistance(VECTOR, bb.getDistance(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	WorldSpeed _speed;
	Double _course;
	WorldDistance _height = null;

	WorldDistance _distance;

	public MoveHandler() {
		super(type);

		addHandler(new WorldSpeedHandler(SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_speed = res;
			}
		});

		addHandler(new WorldDistanceHandler(HEIGHT) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_height = res;
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(COURSE) {
			@Override
			public void setValue(final String name, final double val) {
				_course = new Double(val);
			}
		});
		addHandler(new WorldDistanceHandler(VECTOR) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_distance = res;
			}
		});

	}

	@Override
	public void elementClosed() {
		final Move tr = new Move();
		super.setAttributes(tr);
		tr.setDistance(_distance);
		tr.setSpeed(_speed);
		tr.setCourse(_course);

		if (_height != null)
			tr.setHeight(_height);

		setModel(tr);

		_height = null;
		_distance = null;
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}