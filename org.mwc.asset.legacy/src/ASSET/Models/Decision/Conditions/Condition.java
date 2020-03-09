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

package ASSET.Models.Decision.Conditions;

import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;

/**
 * interface for behaviour objects which act as conditions (as part of a
 * composite structure)
 *
 */
public interface Condition extends Editable {
	/**
	 * the core content of a condition
	 *
	 */
	static abstract public class CoreCondition implements Condition, MWC.GUI.Editable {
		////////////////////////////////////////////////////
		// member objects
		////////////////////////////////////////////////////

		/**
		 * a local copy of our editable object
		 *
		 */
		MWC.GUI.Editable.EditorType _myEditor = null;

		/**
		 * the name of this condition
		 *
		 */
		private String _myName = null;

		////////////////////////////////////////////////////
		// constructor
		////////////////////////////////////////////////////
		/**
		 *
		 * @param name then name of this condition
		 */
		CoreCondition(final String name) {
			_myName = name;
		}

		////////////////////////////////////////////////////
		// conditions
		////////////////////////////////////////////////////

		@Override
		final public String getName() {
			return _myName;
		}

		////////////////////////////////////////////////////
		// accessor/setters
		////////////////////////////////////////////////////

		@Override
		final public void setName(final String name) {
			_myName = name;
		}

		/**
		 * the condition we want to test
		 *
		 * @param status     current ownship status
		 * @param detections detections produced by the current sensor fit
		 * @param time       the current time
		 * @param monitor    the scenario we are working from
		 * @return either null (for failed) or an object containing details of the
		 *         success
		 */
		@Override
		abstract public Object test(Status status, DetectionList detections, long time,
				ScenarioActivityMonitor monitor);
	}

	/**
	 *
	 * @return the name of this condition
	 */
	@Override
	public String getName();

	/**
	 * restart the condition
	 *
	 */
	public void restart();

	/**
	 *
	 * @param name the new name for this condition
	 */
	public void setName(String name);

	/**
	 * decide the course of action to take, or return null to no be used
	 *
	 * @param status     the current status of the participant
	 * @param detections the current list of detections for this participant
	 * @param time       the time this decision was made
	 * @return an object related to the type of condition reached (on success), or
	 *         null to signify failure
	 */
	public Object test(ASSET.Participants.Status status, ASSET.Models.Detection.DetectionList detections, long time,
			ScenarioActivityMonitor monitor);

}
