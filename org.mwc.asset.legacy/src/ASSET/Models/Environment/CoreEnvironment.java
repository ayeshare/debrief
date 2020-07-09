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

import ASSET.Models.MWCModel;
import ASSET.Models.Sensor.Lookup.MADLookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

abstract public class CoreEnvironment implements EnvironmentType, java.io.Serializable, MWCModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * store the maximum number of mediums we will need to work with
	 */
	public static final int MAX_NUM_MEDIUMS = 10;

	public static void main(final String[] args) {
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		final WorldLocation destination = origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Yds2Degs(20000), 0));
		final CoreEnvironment ce = new SimpleEnvironment(1, 1, 1);
		final double res = ce.getResultantEnergyAt(1, origin, destination, 200);
		System.out.println("res is:" + res);
	}

	private String _myName;

	//////////////////////////////////////////////////
	// lookup sensor tables
	//////////////////////////////////////////////////

	/**
	 * the set of mediums we know about
	 */
	private final MediumType[] _myMediums = new MediumType[MAX_NUM_MEDIUMS];

	/**
	 * our description of radar lookup environment
	 *
	 */
	private RadarLookupSensor.RadarEnvironment _radarEnvironment;

	/**
	 * our description of the visual lookup environment
	 *
	 */
	private OpticLookupSensor.OpticEnvironment _opticEnvironment;

	////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/**
	 * our description of the MAD environment
	 *
	 */
	private MADLookupSensor.MADEnvironment _madEnvironment;

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////

	public CoreEnvironment() {
		_myMediums[EnvironmentType.BROADBAND_PASSIVE] = new ASSET.Models.Environment.Mediums.BroadbandMedium();
		_myMediums[EnvironmentType.NARROWBAND] = new ASSET.Models.Environment.Mediums.BroadbandMedium() {

			@Override
			public String getName() {
				return "Narrowband";
			}
		};
		_myName = "Core";
	}

	/**
	 * get the background noise in the indicated bearing from the indicated location
	 *
	 * @param medium   the medium we're looking at
	 * @param origin   where we are at the moment
	 * @param brg_degs the direction we're looking at
	 * @return the background noise
	 */
	@Override
	public double getBkgndNoise(final int medium, final WorldLocation origin, final double brg_degs) {
		double res = INVALID_RESULT;

		// retrieve this medium
		final MediumType thisEnv = _myMediums[medium];

		// did we find it?
		if (thisEnv != null) {
			// calculate the resultant energy
			res = thisEnv.getBkgndNoise(origin, brg_degs);
		}

		return res;
	}

	/**
	 * get the light level at this location
	 *
	 * @param time     the time we're talking about
	 * @param location the location we're talking about
	 * @return the current light level
	 */
	@Override
	public int getLightLevelFor(final long time, final WorldLocation location) {
		return EnvironmentType.DUSK;
	}

	/**
	 * get the loss in indicated medium between the indicated points
	 *
	 * @param medium      the medium we're looking at
	 * @param origin      the source of the energy
	 * @param destination the destination we're looking at
	 * @return the loss between the points
	 */
	@Override
	public double getLossBetween(final int medium, final WorldLocation origin, final WorldLocation destination) {
		double res = INVALID_RESULT;

		// retrieve this medium
		final MediumType thisEnv = _myMediums[medium];

		// did we find it?
		if (thisEnv != null) {
			// calculate the resultant energy
			res = thisEnv.getLossBetween(origin, destination);
		}

		return res;
	}

	/**
	 * get the MAD lookup table
	 *
	 * @return
	 */
	@Override
	public MADLookupSensor.MADEnvironment getMADEnvironment() {
		return _madEnvironment;
	}

	//////////////////////////////////////////////////
	// lookup support
	//////////////////////////////////////////////////

	@Override
	public String getName() {
		return _myName;
	}

	/**
	 * get the optic lookup tables for this environment
	 *
	 * @return
	 */
	@Override
	public OpticLookupSensor.OpticEnvironment getOpticEnvironment() {
		return _opticEnvironment;
	}

	/**
	 * get the radar lookup tables for this environment
	 *
	 * @return
	 */
	@Override
	public RadarLookupSensor.RadarEnvironment getRadarEnvironment() {
		return _radarEnvironment;
	}

	@Override
	public double getResultantEnergyAt(final int medium, final WorldLocation origin, final WorldLocation destination,
			final double sourceLevel) {
		double res = INVALID_RESULT;

		// retrieve this medium
		final MediumType thisEnv = _myMediums[medium];

		// did we find it?
		if (thisEnv != null) {
			// calculate the resultant energy
			res = thisEnv.getResultantEnergyAt(origin, destination, sourceLevel);
		}

		return res;

	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: CoreEnvironment.java,v $
	 * Revision 1.2  2006/09/11 15:15:01  Ian.Mayo
	 * Give environments a name
	 *
	 * Revision 1.1  2006/08/08 14:21:43  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:51  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.9  2004/11/02 20:51:43  ian
	 * Implement MAD sensors
	 *
	 * Revision 1.8  2004/10/27 15:06:06  Ian.Mayo
	 * Reflect changed signature of lookup environments
	 *
	 * Revision 1.7  2004/10/26 14:45:39  Ian.Mayo
	 * Store lookup tables here.
	 *
	 * Revision 1.6  2004/10/20 15:14:48  Ian.Mayo
	 * Switch to array of mediums, instead of inefficient HashMap
	 *
	 * Revision 1.5  2004/05/24 16:01:06  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.2  2004/03/25 22:47:16  ian
	 * Reflect new simple environment constructor
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:53  ian
	 * no message
	 * <p/>
	 * Revision 1.4  2004/02/18 08:49:23  Ian.Mayo
	 * Sync from home
	 * <p/>
	 * Revision 1.2  2003/11/07 14:40:31  Ian.Mayo
	 * Include templated javadoc
	 * <p/>
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	/**
	 * set the MAD lookup table
	 *
	 * @param madEnvironment
	 */
	public void setMADEnvironment(final MADLookupSensor.MADEnvironment madEnvironment) {
		this._madEnvironment = madEnvironment;
	}

	////////////////////////////////////////////////////////////
	// other env factors
	////////////////////////////////////////////////////////////

	@Override
	public void setName(final String name) {
		_myName = name;
	}

	/**
	 * set the optic lookup tables for this environment
	 *
	 * @param opticEnvironment
	 */
	public void setOpticEnvironment(final OpticLookupSensor.OpticEnvironment opticEnvironment) {
		this._opticEnvironment = opticEnvironment;
	}

	/**
	 * set the radar lookup tables for this environment
	 *
	 * @param radarEnvironment
	 */
	public void setRadarEnvironment(final RadarLookupSensor.RadarEnvironment radarEnvironment) {
		this._radarEnvironment = radarEnvironment;
	}
}
