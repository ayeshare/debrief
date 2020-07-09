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

package ASSET.Models.Decision.Responses;

import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;

public interface Response extends Editable {
	////////////////////////////////////////////////////
	// basic implementation of response
	////////////////////////////////////////////////////
	abstract static public class CoreResponse implements Response, MWC.GUI.Editable {
		/**
		 * the name of this response object
		 *
		 */
		private String _myName = null;

		/**
		 * a local copy of our editable object
		 *
		 */
		MWC.GUI.Editable.EditorType _myEditor = null;

		////////////////////////////////////////////////////
		// response object
		////////////////////////////////////////////////////

		/**
		 * produce the required response
		 *
		 * @param conditionResult the result from the condition test
		 * @param status          the current status
		 * @param detections      the current set of detections
		 * @param monitor         the object monitoring us(for add/remove participants,
		 *                        detonations, etc)
		 * @param time            the current time
		 * @return
		 * @see ASSET.Models.Decision.Conditions.Condition
		 */
		@Override
		abstract public DemandedStatus direct(Object conditionResult, Status status, DemandedStatus demStat,
				DetectionList detections, ScenarioActivityMonitor monitor, long time);

		////////////////////////////////////////////////////
		// accessors
		////////////////////////////////////////////////////
		@Override
		public String getName() {
			return _myName;
		}

		@Override
		public void setName(final String myName) {
			this._myName = myName;
		}

		////////////////////////////////////////////////////
		// editor support
		////////////////////////////////////////////////////

	}

	/**
	 *
	 * @param conditionResult the object returned from the condition object
	 * @param status          the current status of the participant
	 * @param detections      the current set of detections
	 * @param monitor         the monitor object listening out for significant
	 *                        activity
	 * @param time            the current time step
	 * @return the DemandedStatus for this vessel
	 */
	public DemandedStatus direct(Object conditionResult, ASSET.Participants.Status status, DemandedStatus demStat,
			ASSET.Models.Detection.DetectionList detections, ASSET.Scenario.ScenarioActivityMonitor monitor, long time);

	////////////////////////////////////////////////////
	// accessors
	////////////////////////////////////////////////////

	/**
	 * get the description of what we're doing
	 *
	 */
	public String getActivity();

	/**
	 * set the name of this response
	 *
	 * @return the name
	 */
	@Override
	String getName();

	/**
	 * reset the local data
	 *
	 */
	public void restart();

	/**
	 * get the name of this response
	 *
	 * @param myName the name
	 */
	void setName(String myName);

}
