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

package ASSET.Models.Environment;

import ASSET.Models.Sensor.Lookup.MADLookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import MWC.GUI.Properties.AbstractPropertyEditor;
import MWC.GenericData.WorldLocation;

public interface EnvironmentType {
	/**
	 * define a property editor so the user can see/change levels of atmos
	 * attenutation
	 */
	public static class AtmosphericAttenuationPropertyEditor extends AbstractPropertyEditor {
		////////////////////////////////////////////////////
		// member objects
		////////////////////////////////////////////////////
		private final String _stringTags[] = { "VERY_CLEAR", "CLEAR", "LIGHT_HAZE", "HAZE", "MIST", "FOG" };

		////////////////////////////////////////////////////
		// member methods
		////////////////////////////////////////////////////
		@Override
		public String[] getTags() {
			return _stringTags;
		}

	}

	/**
	 * define a property editor so the user can see/change levels of atmos
	 * attenutation
	 */
	public static class LightLevelPropertyEditor extends AbstractPropertyEditor {
		////////////////////////////////////////////////////
		// member objects
		////////////////////////////////////////////////////
		private final String _stringTags[] = { "DAYLIGHT", "DUSK", "MOON_NIGHT", "DARK_NIGHT", };

		////////////////////////////////////////////////////
		// member methods
		////////////////////////////////////////////////////
		@Override
		public String[] getTags() {
			return _stringTags;
		}

	}

	/**
	 * define a property editor so the user can see/change mediums
	 */
	public static class MediumPropertyEditor extends AbstractPropertyEditor {
		////////////////////////////////////////////////////
		// member objects
		////////////////////////////////////////////////////
		private final String _stringTags[] = { "PassiveBB", "Narrowband", "Visual", "Radar", "Magnetic", "ActiveBB" };

		////////////////////////////////////////////////////
		// member methods
		////////////////////////////////////////////////////
		@Override
		public String[] getTags() {
			return _stringTags;
		}

	}

	public static final int BROADBAND_PASSIVE = 0;
	public static final int NARROWBAND = 1;
	static final public int VISUAL = 2;

	static final public int RADAR = 3;
	static final public int MAGNETIC = 4;

	public static final int BROADBAND_ACTIVE = 5;
	////////////////////////////////////////////////////////////
	// lookup environments
	///////////////////////////////////////////////////////////
	public static final int OPTIC_LOOKUP = 10;
	public static final int OPTIC_RADAR = 11;
	////////////////////////////////////////////////////////////
	// atmospheric attenutation factors
	////////////////////////////////////////////////////////////
	public static final int VERY_CLEAR = 0;
	public static final int CLEAR = 1;
	public static final int LIGHT_HAZE = 2;

	public static final int HAZE = 3;
	public static final int MIST = 4;
	public static final int FOG = 5;
	////////////////////////////////////////////////////////////
	// light level factors
	////////////////////////////////////////////////////////////
	public static final int DAYLIGHT = 0;

	public static final int DUSK = 1;

	public static final int MOON_NIGHT = 2;

	public static final int DARK_NIGHT = 3;

	/**
	 * constant to represent incalcuble result
	 */
	public static final int INVALID_RESULT = -1;

	/**
	 * get the atmospheric attenuation
	 *
	 * @param time     current time
	 * @param location place to get data for
	 * @return one of the atmospheric attenuation factors
	 */
	public int getAtmosphericAttentuationFor(long time, WorldLocation location);

	/**
	 * get the background noise in the indicated bearing from the indicated location
	 *
	 * @param medium   the medium we're looking at
	 * @param origin   where we are at the moment
	 * @param brg_degs the direction we're looking at
	 * @return the background noise
	 */
	public double getBkgndNoise(int medium, WorldLocation origin, double brg_degs);

	/**
	 * get the light level at this location
	 *
	 * @param time     the time we're talking about
	 * @param location the location we're talking about
	 * @return the current light level
	 */
	public int getLightLevelFor(long time, WorldLocation location);

	/**
	 * get the loss in indicated medium between the indicated points
	 *
	 * @param medium      the medium we're looking at
	 * @param origin      the source of the energy
	 * @param destination the destination we're looking at
	 * @return the loss between the points
	 */
	public double getLossBetween(int medium, WorldLocation origin, WorldLocation destination);

	MADLookupSensor.MADEnvironment getMADEnvironment();

	/**
	 * get the name of this environment
	 */
	public String getName();

	OpticLookupSensor.OpticEnvironment getOpticEnvironment();

	RadarLookupSensor.RadarEnvironment getRadarEnvironment();

	/**
	 * get the loss in indicated medium between the indicated points
	 *
	 * @param medium      the medium we're looking at
	 * @param origin      the source of the energy
	 * @param destination the destination we're looking at
	 * @param sourceLevel the amount of noise radiated from the source
	 * @return the resultant energy at the destination
	 */
	public double getResultantEnergyAt(int medium, WorldLocation origin, WorldLocation destination, double sourceLevel);

	/**
	 * get the sea state
	 *
	 * @param time     current time
	 * @param location place to get data for
	 * @return sea state, from 0 to 10
	 */
	public int getSeaStateFor(long time, WorldLocation location);

	/**
	 * and set it
	 *
	 */
	public void setName(String name);

}
