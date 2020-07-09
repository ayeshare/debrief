
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

import ASSET.Models.Decision.Tactical.SSKRecharge;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class SSKRechargeHandler extends CoreDecisionHandler {

	private final static String type = "SSKRecharge";

	private final static String MIN_LEVEL = "MinLevel";
	private final static String SAFE_LEVEL = "SafeLevel";
	private final static String SNORT_SPEED = "SnortSpeed";
	private final static String EVADE_THESE = "EvadeThese";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Decision.Tactical.SSKRecharge bb = (ASSET.Models.Decision.Tactical.SSKRecharge) toExport;

		// first output the parent bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		thisPart.setAttribute(MIN_LEVEL, writeThis(bb.getMinLevel()));
		thisPart.setAttribute(SAFE_LEVEL, writeThis(bb.getSafeLevel()));

		ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetToEvade(), thisPart, doc);
		WorldSpeedHandler.exportSpeed(SNORT_SPEED, bb.getSnortSpeed(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	double _minLevel;
	double _safeLevel;
	WorldSpeed _snortSpeed;

	ASSET.Models.Decision.TargetType _evadeThese;

	public SSKRechargeHandler() {
		super(type);

		addAttributeHandler(new HandleDoubleAttribute(MIN_LEVEL) {
			@Override
			public void setValue(final String name, final double val) {
				_minLevel = val;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(SAFE_LEVEL) {
			@Override
			public void setValue(final String name, final double val) {
				_safeLevel = val;
			}
		});
		addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler(EVADE_THESE) {
			@Override
			public void setTargetType(final ASSET.Models.Decision.TargetType type) {
				_evadeThese = type;
			}
		});
		addHandler(new WorldSpeedHandler(SNORT_SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_snortSpeed = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		final SSKRecharge ev = new SSKRecharge();
		super.setAttributes(ev);
		ev.setMinLevel(_minLevel);
		ev.setSafeLevel(_safeLevel);
		ev.setSnortSpeed(_snortSpeed);
		ev.setTargetToEvade(_evadeThese);

		// finally output it
		setModel(ev);
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}