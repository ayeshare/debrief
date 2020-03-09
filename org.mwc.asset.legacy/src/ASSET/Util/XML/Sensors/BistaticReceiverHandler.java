
package ASSET.Util.XML.Sensors;

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

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.Initial.BistaticReceiver;

public abstract class BistaticReceiverHandler extends CoreSensorHandler {

	private final static String type = "BistaticReceiver";

	private final static String SUPPRESS = "Suppress";
	private final static String SUPPRESS_ANGLE = "SuppressAngle";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final BistaticReceiver bb = (BistaticReceiver) toExport;

		// insert the parent bits first
		CoreSensorHandler.exportCoreSensorBits(thisPart, bb);

		thisPart.setAttribute(SUPPRESS, writeThis(bb.isSuppressDirect()));
		thisPart.setAttribute(SUPPRESS_ANGLE, writeThis(bb.getObscureAngle()));

		parent.appendChild(thisPart);
	}

	private Boolean _suppress = null;

	private Double _suppressAngle = null;

	public BistaticReceiverHandler() {
		this(type);
	}

	public BistaticReceiverHandler(final String myType) {
		super(myType);

		addAttributeHandler(new HandleBooleanAttribute(SUPPRESS) {
			@Override
			public void setValue(final String name, final boolean value) {
				_suppress = value;
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(SUPPRESS_ANGLE) {
			@Override
			public void setValue(final String name, final double value) {
				_suppressAngle = value;
			}
		});

	}

	/**
	 * method for child class to instantiate sensor
	 *
	 * @param myId
	 * @param myName
	 * @return the new sensor
	 */
	@Override
	protected SensorType getSensor(final int myId) {
		// get this instance
		final BistaticReceiver bb = new BistaticReceiver(myId);

		if (_suppress != null) {
			bb.setSuppressDirect(_suppress);
		}

		if (_suppressAngle != null) {
			bb.setObscureAngle(_suppressAngle);
		}

		_suppress = null;
		_suppressAngle = null;

		return bb;
	}

}