
package ASSET.Util.XML.Vessels;

import ASSET.Models.Vessels.SonarBuoyField;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAreaHandler;
import MWC.Utilities.ReaderWriter.XML.Util.XMLTimeRangeHandler;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

abstract public class BuoyFieldHandler extends ParticipantHandler {

	static private final String myType = "BuoyField";
	static private final String COVER_NAME = "Coverage";
	static private final String PERIOD_NAME = "TimePeriod";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(myType);

		ParticipantHandler.exportThis(toExport, thisPart, doc);

		final SonarBuoyField field = (SonarBuoyField) toExport;
		final WorldArea coverage = field.getCoverage();
		WorldAreaHandler.exportThis(coverage, thisPart, doc, COVER_NAME);

		final TimePeriod period = field.getTimePeriod();
		if (period != null)
			XMLTimeRangeHandler.exportThis(period.getStartDTG(), period.getEndDTG(), thisPart, doc, PERIOD_NAME);

		parent.appendChild(thisPart);

	}

	protected WorldArea _coverage;

	protected TimePeriod _period;

	public BuoyFieldHandler() {
		super(myType);

		// add handlers for the SU properties
		// none in this instance

		addHandler(new WorldAreaHandler(COVER_NAME) {
			@Override
			public void setArea(final WorldArea area) {
				_coverage = area;
			}
		});

		addHandler(new XMLTimeRangeHandler(PERIOD_NAME) {
			@Override
			public void setTimeRange(final HiResDate start, final HiResDate end) {
				_period = new TimePeriod.BaseTimePeriod(start, end);
			}
		});

	}

	@Override
	protected ASSET.ParticipantType getParticipant(final int index) {
		final SonarBuoyField thisField = new SonarBuoyField(index, _coverage);

		// do we have a period?
		if (_period != null)
			thisField.setTimePeriod(_period);

		_coverage = null;
		_period = null;

		return thisField;
	}
}