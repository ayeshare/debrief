
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

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Waterfall;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

abstract public class SequenceHandler extends WaterfallHandler {

	/**
	 * ******************************************************************* interface
	 * to represent this and the Sequence class, allowing us to re-use the add
	 * behaviours method
	 * *******************************************************************
	 */
	public static interface BehaviourListHandler {
		public void addModel(final ASSET.Models.DecisionType dec);
	}

	private final static String type = "Sequence";

	protected final static String STAY_ALIVE = "StayAlive";

	static public void exportSequence(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		exportSequence(type, toExport, parent, doc);
	}

	static public void exportSequence(final String theName, final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(theName);

		// get data item
		final Waterfall bb = (Waterfall) toExport;

		// do the parent export bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// thisPart.setAttribute("MIN_DEPTH", writeThis(bb.getMinDepth()));
		// step through the models
		final java.util.Iterator<DecisionType> it = bb.getModels().iterator();
		while (it.hasNext()) {
			final ASSET.Models.DecisionType dec = it.next();

			exportThisDecisionModel(dec, thisPart, doc);
		}

		parent.appendChild(thisPart);

	}

	public SequenceHandler(final int thisDepth) {
		this(type, thisDepth);
	}

	public SequenceHandler(final String type, final int thisDepth) {
		super(type, thisDepth);

		addAttributeHandler(new HandleBooleanAttribute(STAY_ALIVE) {
			@Override
			public void setValue(final String name, final boolean value) {
				final Sequence seq = (Sequence) _myList;
				seq.setStayAlive(value);
			}
		});
	}

	@Override
	protected BehaviourList createNewList() {
		return new Sequence();
	}

	@Override
	abstract public void setModel(ASSET.Models.DecisionType dec);

}