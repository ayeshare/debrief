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

package ASSET.Util.XML.Decisions;

/**
 * Read in a Switch behaviour object
 */

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Switch;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

abstract public class SwitchHandler extends WaterfallHandler {

	private final static String type = "Switch";
	private final static String INDEX = "Index";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final Switch bb = (Switch) toExport;

		// do the parent export bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// thisPart.setAttribute("MIN_DEPTH", writeThis(bb.getMinDepth()));
		// step through the models
		final java.util.Iterator<DecisionType> it = bb.getModels().iterator();
		while (it.hasNext()) {
			final ASSET.Models.DecisionType dec = it.next();

			exportThisDecisionModel(dec, thisPart, doc);
		}

		thisPart.setAttribute(INDEX, writeThis(bb.getIndex()));

		parent.appendChild(thisPart);

	}

	int _index;

	public SwitchHandler(final int thisDepth) {
		super(type, thisDepth);

		super.addAttributeHandler(new HandleIntegerAttribute(INDEX) {
			@Override
			public void setValue(final String name, final int value) {
				_index = value;
			}
		});
	}

	@Override
	protected BehaviourList createNewList() {
		return new Switch();
	}

	/**
	 * set our attributes within this decision object
	 *
	 * @param decision the decision object to update
	 */
	@Override
	protected void setAttributes(final CoreDecision decision) {
		super.setAttributes(decision);
		((Switch) _myList).setIndex(_index);
	}

	@Override
	abstract public void setModel(ASSET.Models.DecisionType dec);
}