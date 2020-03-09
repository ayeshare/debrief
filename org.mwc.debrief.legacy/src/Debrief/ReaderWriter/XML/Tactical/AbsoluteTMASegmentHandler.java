
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

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class AbsoluteTMASegmentHandler extends CoreTMASegmentHandler {
	private static final String TMA_SEGMENT = "AbsoluteTMASegment";
	public static final String ORIGIN = "Origin";

	public static void exportThisTMASegment(final org.w3c.dom.Document doc, final Element trk,
			final AbsoluteTMASegment seg) {

		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, TMA_SEGMENT);

		// export the start bits
		CoreTMASegmentHandler.exportThisTMASegment(doc, seg, segE);

		// sort out the remaining attributes
		LocationHandler.exportLocation(seg.getTrackStart(), ORIGIN, segE, doc);

		trk.appendChild(segE);

	}

	protected WorldLocation _origin;

	public AbsoluteTMASegmentHandler() {
		// inform our parent what type of class we are
		super(TMA_SEGMENT);

		addHandler(new LocationHandler(ORIGIN) {
			@Override
			public void setLocation(final WorldLocation val) {
				_origin = val;
			}
		});
	}

	@Override
	protected TrackSegment createTrack() {
		final AbsoluteTMASegment res = new AbsoluteTMASegment(_courseDegs, _speed, _origin, null, null);
		return res;
	}

	@Override
	protected void finishInitialisation(final TrackSegment segment) {
		final AbsoluteTMASegment rel = (AbsoluteTMASegment) segment;

		// ok, the track has it's fixes. pass through to set the time,
		// if necessary
		if (rel.getDTG_Start() == null && !rel.isEmpty()) {
			final FixWrapper firstF = (FixWrapper) rel.first();
			final FixWrapper lastF = (FixWrapper) rel.last();

			rel.setDTG_Start_Silent(firstF.getDateTimeGroup());
			rel.setDTG_End_Silent(lastF.getDateTimeGroup());
		}

		super.finishInitialisation(segment);
	}

}