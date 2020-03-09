
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

import Debrief.Wrappers.Track.TrackSegment;

abstract public class TrackSegmentHandler extends CoreTrackSegmentHandler {
	private static final String TRACK_SEGMENT = "TrackSegment";
	public static final String PLOT_RELATIVE = "PlotRelative";

	public static void exportThisSegment(final org.w3c.dom.Document doc, final Element trk, final TrackSegment seg) {

		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, TRACK_SEGMENT);

		// sort out the remaining attributes
		segE.setAttribute(PLOT_RELATIVE, writeThis(seg.getPlotRelative()));

		trk.appendChild(segE);
	}

	private boolean _relativeMode = false;

	public TrackSegmentHandler() {
		// inform our parent what type of class we are
		super(TRACK_SEGMENT);

		addAttributeHandler(new HandleBooleanAttribute(PLOT_RELATIVE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_relativeMode = val;
			}
		});

	}

	@Override
	protected TrackSegment createTrack() {
		final TrackSegment res = new TrackSegment(_relativeMode);

		// reset the relative mode flag
		_relativeMode = false;

		return res;
	}

}