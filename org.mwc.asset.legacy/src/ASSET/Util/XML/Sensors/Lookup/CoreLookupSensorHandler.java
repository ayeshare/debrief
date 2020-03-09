
package ASSET.Util.XML.Sensors.Lookup;

import ASSET.Models.SensorType;

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

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Util.XML.Sensors.CoreSensorHandler;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

abstract class CoreLookupSensorHandler extends CoreSensorHandler {

	/**
	 * and the other values
	 */
	private static final String VDR = "VDR";
	private static final String TBDO = "TBDO";
	private static final String MRF = "MRF";
	private static final String CRF = "CRF";
	private static final String CTP = "CTP";
	private static final String IRF = "IRF";
	private static final String ITP = "ITP";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		throw new RuntimeException("Export lookup sensor not implemented!");
	}

	double _vdr;
	Duration _tbdo;
	double _mrf;
	double _crf;
	Duration _ctp;
	double _irf;

	Duration _itp;

	public CoreLookupSensorHandler(final String myType) {
		super(myType);

		super.addAttributeHandler(new HandleDoubleAttribute(MRF) {
			@Override
			public void setValue(final String name, final double val) {
				_mrf = val;
			}
		});
		super.addAttributeHandler(new HandleDoubleAttribute(CRF) {
			@Override
			public void setValue(final String name, final double val) {
				_crf = val;
			}
		});
		super.addAttributeHandler(new HandleDoubleAttribute(IRF) {
			@Override
			public void setValue(final String name, final double val) {
				_irf = val;
			}
		});
		super.addAttributeHandler(new HandleDoubleAttribute(VDR) {
			@Override
			public void setValue(final String name, final double val) {
				_vdr = val;
			}
		});

		addHandler(new DurationHandler(CTP) {
			@Override
			public void setDuration(final Duration res) {
				_ctp = res;
			}
		});
		addHandler(new DurationHandler(ITP) {
			@Override
			public void setDuration(final Duration res) {
				_itp = res;
			}
		});

		addHandler(new DurationHandler(TBDO) {
			@Override
			public void setDuration(final Duration res) {
				_tbdo = res;
			}
		});

	}

	abstract protected LookupSensor createLookupSensor(int id, double VDR1, long TBDO1, double MRF1, double CRF1,
			Duration CTP1, double IRF1, Duration ITP1);

	@Override
	public void elementClosed() {
		// let the parent do it's stuff
		super.elementClosed();

		// and clear our data
		_ctp = null;
		_itp = null;
		_tbdo = null;

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
		final LookupSensor theSensor = createLookupSensor(myId, _vdr, _tbdo.getMillis(), _mrf, _crf, _ctp, _irf, _itp);
		return theSensor;
	}
}