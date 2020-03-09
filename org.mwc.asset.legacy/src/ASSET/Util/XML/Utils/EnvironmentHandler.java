
package ASSET.Util.XML.Utils;

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
import ASSET.Models.Environment.CoreEnvironment;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Sensor.Lookup.MADLookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import ASSET.Util.XML.Utils.LookupEnvironment.MADLookupTableHandler;
import ASSET.Util.XML.Utils.LookupEnvironment.OpticLookupTableHandler;
import ASSET.Util.XML.Utils.LookupEnvironment.RadarLookupTableHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class EnvironmentHandler extends MWCXMLReader {

	private static String type = "Environment";
	public static final String NAME = "Name";

	private static final String SEA_STATE = "SeaState";
	private static final String ATMOS_ATTEN = "AtmosphericAttenuation";
	private static final String LIGHT_LEVEL = "LightLevel";
	private static final String RADAR_LOOKUP = "RadarLookupEnvironment";
	private static final String OPTIC_LOOKUP = "VisualLookupEnvironment";
	private static final String MAD_LOOKUP = "MADLookupEnvironment";

	/**
	 * export this environment object
	 *
	 * @param environment
	 * @param parent
	 * @param doc
	 */
	public static void exportEnvironment(final SimpleEnvironment environment, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element envElement = doc.createElement(type);

		// set the attributes for this object
		setCoreEnvAttributes(environment, envElement, doc);

		parent.appendChild(envElement);
	}

	/**
	 * set the attributes common to all core environments
	 *
	 * @param environment
	 * @param envElement
	 */
	protected static void setCoreEnvAttributes(final SimpleEnvironment environment,
			final org.w3c.dom.Element envElement, final org.w3c.dom.Document doc) {
		// set the attributes
		// get an integer value for the atmos
		final EnvironmentType.AtmosphericAttenuationPropertyEditor atmosConverter = new EnvironmentType.AtmosphericAttenuationPropertyEditor();

		final EnvironmentType.LightLevelPropertyEditor lightConverter = new EnvironmentType.LightLevelPropertyEditor();

		// and get the integer index
		atmosConverter.setIndex(environment.getAtmosphericAttentuationFor(0, null));
		final String atmosStr = atmosConverter.getAsText();

		// and get the integer index
		lightConverter.setIndex(environment.getLightLevelFor(0, null));
		final String lightStr = lightConverter.getAsText();

		envElement.setAttribute(NAME, environment.getName());
		envElement.setAttribute(SEA_STATE, writeThis(environment.getSeaStateFor(0, null)));
		envElement.setAttribute(ATMOS_ATTEN, atmosStr);
		envElement.setAttribute(LIGHT_LEVEL, lightStr);

		// hey, we've also got to export our lookup tables
		final RadarLookupSensor.RadarEnvironment radar = environment.getRadarEnvironment();
		if (radar != null) {
			RadarLookupTableHandler.exportThis(RADAR_LOOKUP, radar, envElement, doc);
		}

		final OpticLookupSensor.OpticEnvironment optic = environment.getOpticEnvironment();
		if (optic != null) {
			OpticLookupTableHandler.exportThis(OPTIC_LOOKUP, optic, envElement, doc);
		}

	}

	int _seaState;
	String _atmos;

	String _light;
	String _myName;
	RadarLookupSensor.RadarEnvironment _radarEnv;

	OpticLookupSensor.OpticEnvironment _opticEnv;

	MADLookupSensor.MADEnvironment _madEnv;

	public EnvironmentHandler() {
		this(type);
	}

	public EnvironmentHandler(final String theType) {
		super(theType);

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_myName = val;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(SEA_STATE) {
			@Override
			public void setValue(final String name, final int val) {
				_seaState = val;
			}
		});
		addAttributeHandler(new HandleAttribute(ATMOS_ATTEN) {
			@Override
			public void setValue(final String name, final String val) {
				_atmos = val;
			}
		});
		addAttributeHandler(new HandleAttribute(LIGHT_LEVEL) {
			@Override
			public void setValue(final String name, final String val) {
				_light = val;
			}
		});

		addHandler(new RadarLookupTableHandler(RADAR_LOOKUP) {
			@Override
			public void setRadarEnvironment(final RadarLookupSensor.RadarEnvironment env) {
				_radarEnv = env;
			}
		});

		addHandler(new OpticLookupTableHandler(OPTIC_LOOKUP) {
			@Override
			public void setOpticEnvironment(final OpticLookupSensor.OpticEnvironment env) {
				_opticEnv = env;
			}
		});

		addHandler(new MADLookupTableHandler(MAD_LOOKUP) {
			@Override
			public void setMADEnvironment(final MADLookupSensor.MADEnvironment env) {
				_madEnv = env;
			}
		});

	}

	@Override
	public void elementClosed() {
		// get an integer value for the atmos
		final EnvironmentType.AtmosphericAttenuationPropertyEditor atmosConverter = new EnvironmentType.AtmosphericAttenuationPropertyEditor();

		// and one for the light
		final EnvironmentType.LightLevelPropertyEditor lightConverter = new EnvironmentType.LightLevelPropertyEditor();

		// and get the integer index
		atmosConverter.setAsText(_atmos);
		final int atmosId = atmosConverter.getIndex();

		// and get the integer index
		lightConverter.setAsText(_light);
		final int lightId = lightConverter.getIndex();

		// produce a value using these units
		final CoreEnvironment newEnv = getNewEnvironment(atmosId, lightId);
		newEnv.setName(_myName);

		// also set our other values
		if (_radarEnv != null)
			newEnv.setRadarEnvironment(_radarEnv);

		if (_opticEnv != null)
			newEnv.setOpticEnvironment(_opticEnv);

		if (_madEnv != null)
			newEnv.setMADEnvironment(_madEnv);

		// and store it
		setEnvironment(newEnv);

		// and clear
		_atmos = null;
		_radarEnv = null;
		_opticEnv = null;
		_madEnv = null;
	}

	/**
	 * overrideable getter method to get our environment
	 *
	 * @param atmosId
	 * @param lightId
	 * @return
	 */
	protected SimpleEnvironment getNewEnvironment(final int atmosId, final int lightId) {
		return new SimpleEnvironment(atmosId, _seaState, lightId);
	}

	/**
	 * send out the results data
	 *
	 * @param res
	 */
	abstract public void setEnvironment(EnvironmentType res);

}