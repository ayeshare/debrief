
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

import ASSET.Models.Decision.Tactical.MarkDip;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class MarkDipHandler extends CoreDecisionHandler {

	private final static String type = "MarkDip";
	private final static String DEPTH = "BodyDepth";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final MarkDip bb = (MarkDip) toExport;

		// first the parent bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		WorldDistanceHandler.exportDistance(DEPTH, bb.getBodyDepth(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	WorldDistance _theDepth;

	public MarkDipHandler() {
		super(type);

		addHandler(new WorldDistanceHandler(DEPTH) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_theDepth = res;
			}
		});

	}

	@Override
	public void elementClosed() {
		final MarkDip tr = new MarkDip(_theDepth);
		super.setAttributes(tr);

		setModel(tr);
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}