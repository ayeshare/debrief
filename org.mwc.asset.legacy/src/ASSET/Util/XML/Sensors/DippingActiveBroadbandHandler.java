
package ASSET.Util.XML.Sensors;

import ASSET.Models.SensorType;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

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

public abstract class DippingActiveBroadbandHandler extends ActiveBroadbandHandler {

	private final static String type1 = "DippingActiveBroadbandSensor";
	private final static String AIR_LOWER_RATE = "AirLowerRate";
	private final static String AIR_RAISE_RATE = "AirRaiseRate";
	private final static String WATER_LOWER_RATE = "WaterLowerRate";
	private final static String WATER_RAISE_RATE = "WaterRaiseRate";
	private final static String RAISE_PAUSE = "RaisePause";
	private final static String LOWER_PAUSE = "LowerPause";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type1);

		// get data item
		final ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor bb = (ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor) toExport;

		// dipping specific stuff
		WorldSpeedHandler.exportSpeed(AIR_LOWER_RATE, bb.getAirLowerRate(), thisPart, doc);
		WorldSpeedHandler.exportSpeed(AIR_RAISE_RATE, bb.getAirRaiseRate(), thisPart, doc);
		WorldSpeedHandler.exportSpeed(WATER_LOWER_RATE, bb.getWaterLowerRate(), thisPart, doc);
		WorldSpeedHandler.exportSpeed(WATER_RAISE_RATE, bb.getWaterRaiseRate(), thisPart, doc);
		DurationHandler.exportDuration(RAISE_PAUSE, bb.getRaisePause(), thisPart, doc);
		DurationHandler.exportDuration(LOWER_PAUSE, bb.getLowerPause(), thisPart, doc);

		// normal sensor stuff
		thisPart.setAttribute(APERTURE, writeThis(bb.getDetectionAperture()));
		thisPart.setAttribute(SOURCE_LEVEL, writeThis(bb.getSourceLevel()));

		CoreSensorHandler.exportCoreSensorBits(thisPart, bb);

		parent.appendChild(thisPart);

	}

	WorldSpeed airLowerRate;
	WorldSpeed airRaiseRate;
	Duration lowerPause;
	Duration raisePause;
	WorldSpeed waterLowerRate;

	WorldSpeed waterRaiseRate;

	public DippingActiveBroadbandHandler() {
		super(type1);

		addHandler(new WorldSpeedHandler(AIR_LOWER_RATE) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				airLowerRate = res;
			}
		});

		addHandler(new WorldSpeedHandler(AIR_RAISE_RATE) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				airRaiseRate = res;
			}
		});

		addHandler(new WorldSpeedHandler(WATER_LOWER_RATE) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				waterLowerRate = res;
			}
		});

		addHandler(new WorldSpeedHandler(WATER_RAISE_RATE) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				waterRaiseRate = res;
			}
		});

		addHandler(new DurationHandler(RAISE_PAUSE) {
			@Override
			public void setDuration(final Duration res) {
				raisePause = res;
			}
		});

		addHandler(new DurationHandler(LOWER_PAUSE) {
			@Override
			public void setDuration(final Duration res) {
				lowerPause = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		super.elementClosed();

		// and clear our data
		_working = true;

		airLowerRate = null;
		airRaiseRate = null;
		lowerPause = null;
		raisePause = null;
		waterLowerRate = null;
		waterRaiseRate = null;

	}

	/**
	 * method for child class to instantiate sensor
	 *
	 * @param myId
	 * @param myName
	 * @return the new sensor
	 */
	protected SensorType getSensor(final int myId, final String myName) {
		// get this instance
		final ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor bb = new ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor(
				myId);
		bb.setName(myName);

		// dipper specific
		bb.setAirLowerRate(airLowerRate);
		bb.setAirRaiseRate(airRaiseRate);
		bb.setLowerPause(lowerPause);
		bb.setRaisePause(raisePause);
		bb.setWaterLowerRate(waterLowerRate);
		bb.setWaterRaiseRate(waterRaiseRate);

		// normal sensor stuff
		bb.setDetectionAperture(_myAperture);
		bb.setSourceLevel(_mySourceLevel);
		bb.setWorking(_working);

		return bb;
	}
}