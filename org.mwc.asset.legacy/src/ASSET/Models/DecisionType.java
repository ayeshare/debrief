
package ASSET.Models;

import ASSET.Participants.Status;
import MWC.GUI.CanvasType;

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
 * Model representation of a specific behaviour. Takes the current vessel status
 * and the current set of {@link ASSET.Models.Detection.DetectionList sensor
 * detections} and produces a {@link ASSET.Participants.DemandedStatus demanded
 * status}.
 *
 * Frequently the highest level decision model is a
 * {@link ASSET.Models.Decision.Waterfall chain}, which contains a prioritised
 * sequence of child decision models.
 */

public interface DecisionType extends MWC.GUI.Editable, MWCModel {

	/**
	 * interface for if a decision type is paint-able
	 *
	 */
	public static interface Paintable {
		public void paint(CanvasType dest);
	}

	/**
	 * decide the course of action to take, or return null to no be used
	 *
	 * @param status     the current status of the participant
	 * @param chars      the movement chars of this vehicle
	 * @param detections the current list of detections for this participant
	 * @param monitor    the object which handles weapons release/detonation
	 * @param newTime    the time this decision is to be made
	 */
	public ASSET.Participants.DemandedStatus decide(ASSET.Participants.Status status,
			ASSET.Models.Movement.MovementCharacteristics chars, ASSET.Participants.DemandedStatus demStatus,
			ASSET.Models.Detection.DetectionList detections, ASSET.Scenario.ScenarioActivityMonitor monitor,
			long newTime);

	/**
	 * return a string representing what we are currently up to
	 */
	public String getActivity();

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo();

	/**
	 * return the name of this detection model
	 *
	 */
	@Override
	public String getName();

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor();

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 *
	 * @param currentStatus
	 */
	public void interrupted(Status currentStatus);

	/**
	 * find out whether this behaviour is active or not.
	 *
	 * @return yes/no
	 */
	public boolean isActive();

	/**
	 * reset this decision model
	 *
	 */
	public void restart();

	/**
	 * se twhether this behaviour is active or not.
	 *
	 * @param isActive yes/no
	 */
	public void setActive(boolean isActive);

	/**
	 * the name of this detection model
	 *
	 */
	public void setName(String val);

	/** whether decisions should be recorded for this decision type
	 * 
	 * @return yes/no
	 */
	boolean isReportDecisions();

}