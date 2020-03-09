
package ASSET.Util.XML.Movement;

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

abstract public class SSKMovementHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "SSKMovement";
	private final static String CHARGE_RATE = "ChargeRate";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		final ASSET.Models.Movement.SSKMovement sskm = (ASSET.Models.Movement.SSKMovement) toExport;

		thisPart.setAttribute(CHARGE_RATE, writeThis(sskm.getChargeRate()));

		// output it's attributes
		parent.appendChild(thisPart);

	}

	double _chargeRate;

	public SSKMovementHandler() {
		super(type);
		super.addAttributeHandler(new HandleDoubleAttribute(CHARGE_RATE) {
			@Override
			public void setValue(final String name, final double val) {
				_chargeRate = val;
			}
		});
	}

	@Override
	public void elementClosed() {
		final ASSET.Models.Movement.SSKMovement ss = new ASSET.Models.Movement.SSKMovement();
		ss.setChargeRate(_chargeRate);
		setModel(ss);
	}

	abstract public void setModel(ASSET.Models.MovementType dec);

}