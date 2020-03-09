
package Debrief.ReaderWriter.XML.Tactical;

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

import org.w3c.dom.Element;

import Debrief.Wrappers.Track.CoreTMASegment;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class CoreTMASegmentHandler extends CoreTrackSegmentHandler {
	public static final String COURSE_DEGS = "CourseDegs";
	public static final String SPEED = "Speed";

	public static void exportThisTMASegment(final org.w3c.dom.Document doc, final CoreTMASegment theSegment,
			final Element theElement) {
		// sort out the remaining attributes
		theElement.setAttribute(COURSE_DEGS, writeThis(theSegment.getCourse()));
		WorldSpeedHandler.exportSpeed(SPEED, theSegment.getSpeed(), theElement, doc);
	}

	protected double _courseDegs = 0d;

	protected WorldSpeed _speed;

	public CoreTMASegmentHandler(final String myName) {
		// inform our parent what type of class we are
		super(myName);

		addAttributeHandler(new HandleDoubleAttribute(COURSE_DEGS) {
			@Override
			public void setValue(final String name, final double val) {
				_courseDegs = val;
			}
		});
		addHandler(new WorldSpeedHandler(SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_speed = res;
			}
		});
	}

}