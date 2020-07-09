
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
import ASSET.Models.Sensor.Cookie.TypedCookieSensor;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor.TypedRangeDoublet;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class TypedCookieSensorHandler extends CoreSensorHandler {

	static abstract public class TypedRangeDoubletHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
		private static final String DETECTION_RANGE = "DetectionRange";
		private static final String MY_TYPE = "TypedRangeDoublet";
		private static final String PERIOD = "DetectionPeriod";
		private Vector<String> _targetTypes;
		protected WorldDistance _detRange;
		private Duration _period;

		public TypedRangeDoubletHandler() {
			super(MY_TYPE);

			this.addHandler(new TargetTypeHandler.TypeHandler() {
				@Override
				public void addType(final String attr) {
					if (_targetTypes == null)
						_targetTypes = new Vector<String>();
					_targetTypes.add(attr);
				}
			});
			this.addHandler(new WorldDistanceHandler(DETECTION_RANGE) {

				@Override
				public void setWorldDistance(final WorldDistance res) {
					_detRange = res;
				}
			});
			this.addHandler(new DurationHandler(PERIOD) {

				@Override
				public void setDuration(final Duration res) {
					_period = res;
				}
			});
		}

		@Override
		public void elementClosed() {

			final TypedRangeDoublet res = new TypedRangeDoublet(_targetTypes, _detRange);

			if (_period != null)
				res.setPeriod(_period);

			// pass to parent
			setRangeDoublet(res);

			// restart
			_targetTypes = null;
			_period = null;
			_detRange = null;
		}

		abstract public void setRangeDoublet(TypedRangeDoublet doublet);

	}

	private final static String type = "TypedCookieSensor";
	private final static String HAS_RANGE = "ProducesRange";
	private final static String INCLUDES_NOISE = "IncludesNoise";
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

	Boolean _produceRange = null;
	protected Boolean _includesNoise = null;

	public TypedCookieSensorHandler() {
		super(type);

		addHandler(new TypedRangeDoubletHandler() {

			@Override
			public void setRangeDoublet(final TypedRangeDoublet doublet) {
				if (_rangeDoublets == null)
					_rangeDoublets = new Vector<TypedRangeDoublet>();

				_rangeDoublets.add(doublet);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(HAS_RANGE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_produceRange = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(INCLUDES_NOISE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_includesNoise = val;
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

		_rangeDoublets = null;
		_detectionLevel = null;
		_medium = -1;
		_produceRange = true;
		_includesNoise = null;
	}

	@Override
	protected SensorType getSensor(final int myId) {
		Integer thisDetLevel = DetectionEvent.DETECTED;

		if (_detectionLevel != null) {
			final DetectionStatePropertyEditor detHandler = new DetectionEvent.DetectionStatePropertyEditor();
			detHandler.setAsText(_detectionLevel);
			thisDetLevel = ((Integer) detHandler.getValue());
		}

		final ASSET.Models.Sensor.Cookie.TypedCookieSensor typedSensor = new TypedCookieSensor(myId, _rangeDoublets,
				thisDetLevel);
		if (_produceRange != null) {
			typedSensor.setProducesRange(_produceRange);
			_produceRange = null;
		}
		if (_detectionInterval != null) {
			typedSensor.setTimeBetweenDetectionOpportunities(_detectionInterval);
			_detectionInterval = null;
		}
		if (_includesNoise != null) {
			typedSensor.setApplyNoise(_includesNoise);
		}

		// do we have a medium
		if (_medium != -1)
			typedSensor.setMedium(_medium);

		return typedSensor;
	}
}