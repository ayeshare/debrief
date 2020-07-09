
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

public abstract class BroadbandHandler extends CoreSensorHandler {

	private final static String type = "BroadbandSensor";
	protected final static String APERTURE = "Aperture";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Sensor.Initial.BroadbandSensor bb = (ASSET.Models.Sensor.Initial.BroadbandSensor) toExport;

		// insert the parent bits first
		CoreSensorHandler.exportCoreSensorBits(thisPart, bb);

		// and now our bits
		thisPart.setAttribute(APERTURE, writeThis(bb.getDetectionAperture()));

		parent.appendChild(thisPart);
	}

	protected double _myAperture;

	public BroadbandHandler() {
		this(type);
	}

	public BroadbandHandler(final String myType) {
		super(myType);

		super.addAttributeHandler(new HandleDoubleAttribute(APERTURE) {
			@Override
			public void setValue(final String name, final double val) {
				_myAperture = val;
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
		final ASSET.Models.Sensor.Initial.BroadbandSensor bb = new ASSET.Models.Sensor.Initial.BroadbandSensor(myId);

		super.configureSensor(bb);

		bb.setDetectionAperture(_myAperture);

		_myAperture = 0;

		return bb;
	}

}