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

package ASSET.Participants;

/**
 * class which extends DemandedStatus by adding optional sensor lineup change
 *
 */

public class DemandedSensorStatus {
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////

	/**
	 * the medium this status should apply to
	 *
	 */
	private final int _medium;

	/**
	 * whether to switch on or off
	 *
	 */
	private final boolean _switchOn;

	/**
	 * the depth to deploy to (optional)
	 *
	 */
	private Double _deployDepth = null;

	////////////////////////////////////////////////////
	// member constructor
	////////////////////////////////////////////////////

	/**
	 * create new demanded status which also changes sensor lineupo
	 *
	 * @param sensorMedium the medium for this sensor(s)
	 * @param switchOn     whether to switch on or off
	 */
	public DemandedSensorStatus(final int sensorMedium, final boolean switchOn) {
		_medium = sensorMedium;
		_switchOn = switchOn;
	}

	////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////

	public Double getDeployDepth() {
		return _deployDepth;
	}

	public int getMedium() {
		return _medium;
	}

	public boolean getSwitchOn() {
		return _switchOn;
	}

	public void setDeployDepth(final Double deployDepth) {
		this._deployDepth = deployDepth;
	}

}
