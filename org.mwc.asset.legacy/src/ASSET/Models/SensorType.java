
package ASSET.Models;

import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.SensorDataProvider;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GenericData.WorldDistance;

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

public interface SensorType extends MWC.GUI.Editable, SensorDataProvider {

	public static interface ActiveSensor {

		/**
		 * get the source level
		 *
		 * @return the source level (in relevant units)
		 */
		public double getSourceLevel();
	}

	/**
	 * allow somebody to start listening to the components of our calculation
	 *
	 * @param listener
	 */
	public void addSensorCalculationListener(java.beans.PropertyChangeListener listener);

	/**
	 * see if we detect any other vessels
	 *
	 * @param environment        the environment we're living in
	 * @param existingDetections the existing set of detections
	 * @param ownship            ourselves
	 * @param scenario           the scenario we live in
	 * @param time               the current DTG
	 */
	public void detects(final ASSET.Models.Environment.EnvironmentType environment,
			final DetectionList existingDetections, final ASSET.ParticipantType ownship,
			final ASSET.ScenarioType scenario, long time);

	/**
	 * the estimated range for a detection of this type (where applicable)
	 */
	public WorldDistance getEstimatedRange();

	/**
	 * get the id of this sensor
	 */
	public int getId();

	/**
	 * the type of medium we look at
	 *
	 * @return the id of the medium
	 * @see ASSET.Models.Environment.EnvironmentType
	 */
	public int getMedium();

	/**
	 * get the name of this sensor
	 */
	@Override
	public String getName();

	/**
	 * handle the demanded change in sensor lineup
	 *
	 * @param status
	 */
	public void inform(DemandedSensorStatus status);

	/**
	 * determine the state of this sensor
	 *
	 * @return yes/no for if it's working
	 */
	public boolean isWorking();

	/**
	 * remove a calculation listener
	 *
	 * @param listener
	 */
	public void removeSensorCalculationListener(java.beans.PropertyChangeListener listener);

	/**
	 * restart this sensors
	 */
	public void restart();

	/**
	 * set the name of this sensor
	 *
	 */
	public void setName(String name);

	/**
	 * control the state of this sensor
	 *
	 * @param switchOn whether to switch it on or off.
	 */
	public void setWorking(boolean switchOn);

	////////////////////////////////////////////////////
	// interface used for active sensors
	////////////////////////////////////////////////////

	/**
	 * if this sensor has a dynamic behaviour, update it according to the demanded
	 * status
	 *
	 * @param myDemandedStatus
	 * @param myStatus
	 * @param newTime
	 */
	void update(DemandedStatus myDemandedStatus, Status myStatus, long newTime);

}