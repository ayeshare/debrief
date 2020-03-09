
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
import ASSET.Models.Sensor.Cookie.PlainCookieSensor;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class PlainCookieSensorHandler extends CoreSensorHandler {

	private final static String type = "PlainCookieSensor";
	private final static String HAS_RANGE = "ProducesRange";

	private static final String DETECTION_RANGE = "DetectionRange";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final PlainCookieSensor bb = (PlainCookieSensor) toExport;

		WorldDistanceHandler.exportDistance(DETECTION_RANGE, bb.getDetectionRange(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	WorldDistance _detRange;

	boolean _produceRange = true;

	public PlainCookieSensorHandler() {
		super(type);

		addHandler(new WorldDistanceHandler(DETECTION_RANGE) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_detRange = res;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(HAS_RANGE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_produceRange = val;
			}
		});
	}

	@Override
	public void elementClosed() {
		super.elementClosed();

		// and now clear our data
		_detRange = null;
		_produceRange = true;
	}

	@Override
	protected SensorType getSensor(final int myId) {
		final ASSET.Models.Sensor.Cookie.PlainCookieSensor cookieS = new PlainCookieSensor(myId, _detRange);
		cookieS.setProducesRange(_produceRange);

		return cookieS;
	}
}