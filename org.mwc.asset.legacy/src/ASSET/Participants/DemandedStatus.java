
package ASSET.Participants;

import java.util.Vector;

import ASSET.Models.Movement.HighLevelDemandedStatus;
import ASSET.Models.Movement.SimpleDemandedStatus;

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

/**
 * the demanded status for this participant. The demanded status represents the
 * output of a {@link ASSET.Models.DecisionType decision model}, and is used as
 * an input to the {@link ASSET.Models.MovementType manoeuvering model} .
 */

abstract public class DemandedStatus {

	/**
	 * ******************************************************************* copy
	 * constructor
	 * *******************************************************************
	 */
	public static DemandedStatus copy(final long newTime, final DemandedStatus other) {
		DemandedStatus res = null;

		if (other != null) {

			if (other instanceof SimpleDemandedStatus) {
				res = new SimpleDemandedStatus(newTime, (SimpleDemandedStatus) other);
			} else if (other instanceof HighLevelDemandedStatus) {
				res = new HighLevelDemandedStatus(newTime, (HighLevelDemandedStatus) other);
			} else {
				throw new RuntimeException("Failed to match demanded status type");
			}

			// initialise the set of states (if we have to)
			if (other._myStates != null) {
				if (res._myStates == null) {
					res._myStates = new Status.States();
				}
				// copy the set of states
				res._myStates.addAll(other._myStates);
			}

			// copy the dem sensor states
			// does he have any?
			if (other._sensorStates != null) {
				if (other._sensorStates.size() > 0) {

					// yes, do we have any?
					if (res._sensorStates == null)
						res._sensorStates = new Vector<DemandedSensorStatus>(0, 1);

					res._sensorStates.addAll(other._sensorStates);
				}
			}
		}

		return res;
	}

	/**
	 * the set of demanded vessel states (using lazy instantiation)
	 */
	private transient Status.States _myStates = null;

	/**
	 * time this status message recorded
	 */
	private long _time;

	/**
	 * id of participant sending this message
	 */
	private long _id;

	// //////////////////////////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////////////////////

	/**
	 * a list of demanded sensor states
	 */
	private transient Vector<DemandedSensorStatus> _sensorStates = null;

	protected DemandedStatus(final long id, final long time) {
		_id = id;
		_time = time;
	}

	// //////////////////////////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////////////////////

	/**
	 * extend the list of demanded sensor states
	 */
	public void add(final DemandedSensorStatus state) {
		if (_sensorStates == null)
			_sensorStates = new Vector<DemandedSensorStatus>(1, 1);

		_sensorStates.add(state);
	}

	/**
	 * get the id of the participant for this status
	 */
	public long getId() {
		return _id;
	}

	/**
	 * get the list of sensor states
	 */
	public Vector<DemandedSensorStatus> getSensorStates() {
		return _sensorStates;
	}

	/**
	 * get the time this status was recorded
	 */
	public long getTime() {
		return _time;
	}

	// ////////////////////////////////////////////////
	// state management
	// ////////////////////////////////////////////////

	/**
	 * are we currently in this state?
	 *
	 * @param state state to check
	 * @return yes/no
	 */
	public boolean is(final String state) {
		boolean res = false;
		if (_myStates == null)
			res = false;
		else
			res = _myStates.contains(state);

		return res;
	}

	/**
	 * set the vessel in this particular state
	 *
	 * @param state the new state we're entering
	 */
	public void set(final String state) {
		// create the states, if we have to
		if (_myStates == null) {
			_myStates = new Status.States();
		}

		_myStates.add(state);
	}

	/**
	 * set the id of this participant
	 */
	public void setId(final long val) {
		_id = val;
	}

	/**
	 * set the time this status was recorded
	 */
	public void setTime(final long time) {
		_time = time;
	}

	/**
	 * stop the vessel being in this particular state
	 *
	 * @param state the state we're ending
	 */
	public void unset(final String state) {
		if (_myStates != null) {
			_myStates.remove(state);
		}
	}

}