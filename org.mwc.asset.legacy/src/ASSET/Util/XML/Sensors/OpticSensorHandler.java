
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
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class OpticSensorHandler extends CoreSensorHandler {

	private final static String type = "OpticSensor";
	private static final String MIN_HEIGHT = "MinHeight";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Sensor.Initial.OpticSensor bb = (ASSET.Models.Sensor.Initial.OpticSensor) toExport;

		WorldDistanceHandler.exportDistance(MIN_HEIGHT, bb.getMinHeight(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	WorldDistance _myMinHeight;

	public OpticSensorHandler() {
		super(type);

		addHandler(new WorldDistanceHandler(MIN_HEIGHT) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_myMinHeight = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		super.elementClosed();

		// and now clear our data
		_myMinHeight = null;
	}

	@Override
	protected SensorType getSensor(final int myId) {
		final ASSET.Models.Sensor.Initial.OpticSensor optic = new ASSET.Models.Sensor.Initial.OpticSensor(myId);
		optic.setMinHeight(_myMinHeight);

		return optic;
	}
}