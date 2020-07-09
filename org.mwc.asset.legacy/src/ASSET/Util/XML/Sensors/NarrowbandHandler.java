
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
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

public abstract class NarrowbandHandler extends CoreSensorHandler {

	private final static String type = "NarrowbandSensor";
	protected final static String STEADY_TIME = "SteadyTime";
	protected final static String AMBIGUOUS = "Ambiguous";
	protected final static String SECOND_HARMONIC = "SecondHarmonic";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Sensor.Initial.NarrowbandSensor bb = (ASSET.Models.Sensor.Initial.NarrowbandSensor) toExport;
		CoreSensorHandler.exportCoreSensorBits(thisPart, bb);

		DurationHandler.exportDuration(STEADY_TIME, bb.getSteadyTime(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	protected Duration _mySteadyTime;
	protected Boolean _hasBearing = false;
	protected Boolean _secondHarmonic = null;

	protected Boolean _isAmbiguous = null;

	public NarrowbandHandler() {
		this(type);
	}

	public NarrowbandHandler(final String myType) {
		super(myType);

		super.addHandler(new DurationHandler(STEADY_TIME) {
			@Override
			public void setDuration(final Duration res) {
				_mySteadyTime = res;
			}
		});

		super.addAttributeHandler(new HandleBooleanAttribute(AMBIGUOUS) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isAmbiguous = val;
			}
		});
		super.addAttributeHandler(new HandleBooleanAttribute(SECOND_HARMONIC) {
			@Override
			public void setValue(final String name, final boolean value) {
				_secondHarmonic = value;
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
		final ASSET.Models.Sensor.Initial.NarrowbandSensor bb = new ASSET.Models.Sensor.Initial.NarrowbandSensor(myId);

		super.configureSensor(bb);

		bb.setSteadyTime(_mySteadyTime);
		if (_secondHarmonic != null) {
			bb.setSecondHarmonic(_secondHarmonic.booleanValue());
		}

		_secondHarmonic = null;
		_mySteadyTime = null;

		return bb;
	}
}