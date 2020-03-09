
package ASSET.Util.XML.Decisions.Conditions;

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

import ASSET.Models.Decision.Conditions.Condition;
import ASSET.Models.Decision.Conditions.ElapsedTime;
import MWC.GenericData.Duration;

abstract public class ElapsedTimeHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "ElapsedTime";

	// private final static String THRESHOLD = "Duration";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ElapsedTime bb = (ElapsedTime) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());
		MWC.Utilities.ReaderWriter.XML.Util.DurationHandler.exportDuration(bb.getDuration(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	Duration _duration;

	String _name;

	public ElapsedTimeHandler() {
		super("ElapsedTime");

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});

		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.DurationHandler() {
			@Override
			public void setDuration(final Duration res) {
				_duration = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		final ElapsedTime res = new ElapsedTime(_duration);
		res.setName(_name);

		// finally output it
		setCondition(res);

		// and reset
		_duration = null;
		_name = null;
	}

	abstract public void setCondition(Condition dec);

}