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

package ASSET.Util.XML.Sensors;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.Initial.InitialSensor;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 05-Feb-2004 Time: 11:29:48 To
 * change this template use File | Settings | File Templates.
 */
public abstract class CoreSensorHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	private static final String NAME = "Name";
	private final static String WORKING = "Working";
	private final static String DETECTION_INTERVAL = "DetectionIntervalMillis";
	private final static String SENSOR_OFFSET = "SensorOffset";
	private final static String AMBIGUOUS = "Ambiguous";
	private final static String PRODUCE_RANGE = "CanProduceRange";
	private static final String ID_VAL = "id";

	/**
	 * export the sensor bits handled by this core class
	 *
	 * @param sensorElement
	 * @param toExport
	 */
	static public void exportCoreSensorBits(final org.w3c.dom.Element sensorElement, final Object toExport) {
		final CoreSensor cs = (CoreSensor) toExport;
		sensorElement.setAttribute(WORKING, writeThis(cs.isWorking()));
		sensorElement.setAttribute(NAME, cs.getName());
		sensorElement.setAttribute(ID_VAL, writeThis(cs.getId()));

		if (cs instanceof InitialSensor) {
			final InitialSensor is = (InitialSensor) cs;
			sensorElement.setAttribute(AMBIGUOUS, writeThis(is.isAmbiguous()));
			sensorElement.setAttribute(PRODUCE_RANGE, writeThis(is.canProduceRange()));
		}
	}

	int _myId;
	String _myName;
	protected Integer _detectionInterval = null;
	protected boolean _working = true;

	protected WorldDistance _sensorOffset = null;
	private Boolean _isAmbiguous = null;

	private Boolean _canProduceRange = null;

	public CoreSensorHandler(final String myType) {
		super(myType);

		super.addAttributeHandler(new HandleAttribute(ID_VAL) {
			@Override
			public void setValue(final String name, final String val) {
				_myId = Integer.parseInt(val);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(DETECTION_INTERVAL) {
			@Override
			public void setValue(final String name, final int val) {
				_detectionInterval = val;
			}
		});

		super.addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_myName = val;
			}
		});
		super.addAttributeHandler(new HandleBooleanAttribute(WORKING) {
			@Override
			public void setValue(final String name, final boolean val) {
				_working = val;
			}
		});
		addHandler(new WorldDistanceHandler(SENSOR_OFFSET) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_sensorOffset = res;
			}
		});

		super.addAttributeHandler(new HandleBooleanAttribute(AMBIGUOUS) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isAmbiguous = val;
			}
		});

		super.addAttributeHandler(new HandleBooleanAttribute(PRODUCE_RANGE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_canProduceRange = val;
			}
		});
	}

	/**
	 * callback to store the sensor in the parent
	 *
	 * @param sensor the new sensor
	 */
	abstract public void addSensor(SensorType sensor);

	/**
	 * let the child implementations use our common processing
	 *
	 * @param sensor
	 */
	protected void configureSensor(final CoreSensor sensor) {
		if (sensor instanceof InitialSensor) {
			final InitialSensor is = (InitialSensor) sensor;
			if (_isAmbiguous != null) {
				is.setAmbiguous(_isAmbiguous);
			}
			if (_canProduceRange != null) {
				is.setCanProduceRange(_canProduceRange);
			}
		}

		_isAmbiguous = false;
		_canProduceRange = true;
	}

	@Override
	public void elementClosed() {
		// do we have an id?
		if (_myId <= 0)
			_myId = ASSET.Util.IdNumber.generateInt();

		// get this instance
		final SensorType sensor = getSensor(_myId);
		sensor.setName(_myName);

		// ok - now store it
		addSensor(sensor);
		sensor.setWorking(_working);

		if (sensor instanceof CoreSensor) {
			final CoreSensor core = (CoreSensor) sensor;
			if (_detectionInterval != null) {
				core.setTimeBetweenDetectionOpportunities(_detectionInterval);
				_detectionInterval = null;
			}

			if (_sensorOffset != null) {
				core.setSensorOffset(_sensorOffset);
			}
		}

		// and clear the data
		_sensorOffset = null;
		_myName = null;
		_myId = -1;
		_working = true;
	}

	/**
	 * method for child class to instantiate sensor
	 *
	 * @param myId
	 * @return the new sensor
	 */
	abstract protected SensorType getSensor(int myId);

}
