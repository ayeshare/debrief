
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

import java.awt.Color;

import org.w3c.dom.Element;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.ToolParent;

abstract public class DynamicInfillSegmentHandler extends CoreTrackSegmentHandler {
	private static final String DYNAMIC_SEGMENT = "DynamicInfillSegment";
	private static final String BEFORE = "BeforeLeg";
	private static final String AFTER = "AfterLeg";

	public static void exportThisSegment(final org.w3c.dom.Document doc, final Element trk,
			final DynamicInfillSegment seg) {
		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, DYNAMIC_SEGMENT);

		// sort out the remaining attributes
		segE.setAttribute(BEFORE, seg.getBeforeName());
		segE.setAttribute(AFTER, seg.getAfterName());

		trk.appendChild(segE);
	}

	private String _beforeName;

	private String _afterName;

	public DynamicInfillSegmentHandler() {
		// inform our parent what type of class we are
		super(DYNAMIC_SEGMENT);

		addAttributeHandler(new HandleAttribute(BEFORE) {
			@Override
			public void setValue(final String name, final String value) {
				_beforeName = value;
			}
		});
		addAttributeHandler(new HandleAttribute(AFTER) {
			@Override
			public void setValue(final String name, final String value) {
				_afterName = value;
			}
		});
	}

	@Override
	protected TrackSegment createTrack() {
		// get the color form the first item
		final Color fixColor;
		if (super._fixes.isEmpty()) {
			// no fixes in solution.
			// updated logic means this shouldn't happen.
			// But, robustly handle it, if it does.
			fixColor = Color.red;
		} else {
			final FixWrapper first = super._fixes.firstElement();
			fixColor = first.getColor();
		}

		final TrackSegment res;
		if (_fixes.size() <= 1) {
			// ok. We used to allow single point infills,
			// now we don't. Skip this infill
			res = null;

			// log this, we may forget we're consciously skipping such short ones
			Application.logError2(ToolParent.WARNING, "We're deliberately skipping this one-point infill:" + _name,
					null);
		} else {
			res = new DynamicInfillSegment(_beforeName, _afterName, fixColor);
		}
		return res;
	}
}