
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

import java.util.Vector;

import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionEvent.DetectionStatePropertyEditor;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Sensor.Cookie.TypedCookieInterceptSensor;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor.TypedRangeDoublet;

abstract public class TypedCookieInterceptHandler extends CoreSensorHandler {

	private final static String type = "TypedCookieInterceptSensor";
	protected final static String DETECTION_LEVEL = "DetectionLevel";

	private final static String MEDIUM = "Medium";
	public static EnvironmentType.MediumPropertyEditor _myEditor = new EnvironmentType.MediumPropertyEditor();

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);
		parent.appendChild(thisPart);

		throw new RuntimeException("failed to implement export for TypedCookieSensorHandler");

	}

	private Vector<TypedRangeDoublet> _rangeDoublets;

	String _detectionLevel;

	int _medium = -1;

	public TypedCookieInterceptHandler() {
		super(type);

		addHandler(new TypedCookieSensorHandler.TypedRangeDoubletHandler() {

			@Override
			public void setRangeDoublet(final TypedRangeDoublet doublet) {
				if (_rangeDoublets == null)
					_rangeDoublets = new Vector<TypedRangeDoublet>();

				_rangeDoublets.add(doublet);
			}
		});

		addAttributeHandler(new HandleAttribute(MEDIUM) {
			@Override
			public void setValue(final String name, final String val) {
				_myEditor.setValue(val);
				_medium = _myEditor.getIndex();
			}
		});

		addAttributeHandler(new HandleAttribute(DETECTION_LEVEL) {
			@Override
			public void setValue(final String name, final String val) {
				_detectionLevel = val;
			}
		});

	}

	@Override
	public void elementClosed() {
		super.elementClosed();

	}

	@Override
	protected SensorType getSensor(final int myId) {
		Integer thisDetLevel = DetectionEvent.DETECTED;

		if (_detectionLevel != null) {
			final DetectionStatePropertyEditor detHandler = new DetectionEvent.DetectionStatePropertyEditor();
			detHandler.setAsText(_detectionLevel);
			thisDetLevel = ((Integer) detHandler.getValue());
		}

		final TypedCookieInterceptSensor typedSensor = new TypedCookieInterceptSensor(myId, _rangeDoublets,
				thisDetLevel);

		// do we have a medium
		if (_medium != -1)
			typedSensor.setMedium(_medium);

		_rangeDoublets = null;
		_detectionLevel = null;
		_medium = -1;

		return typedSensor;
	}

}