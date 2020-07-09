
package ASSET.Models;

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
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;

/**
 * definition of an algorithm that describes the movement of a particular
 * platform. It takes a {@link ASSET.Participants.Status current status} and a
 * {@link ASSET.Participants.DemandedStatus demanded status} and moves the
 * platform forward in time to produce a {@link ASSET.Participants.Status new
 * status}. The algorithm is informed of the
 * {@link ASSET.Models.Movement.MovementCharacteristics movement
 * characteristics} of the current vessel.
 *
 */

public interface MovementType extends MWCModel {
	/**
	 * move platform forward one time step
	 *
	 * @param millis         the current scenario time
	 * @param currentStatus  the current platform status
	 * @param demandedStatus the current demanded status of the platform
	 * @param moves          a set of movement characteristics for this particular
	 *                       platform
	 * @return the updated status
	 */
	public Status step(long millis, Status currentStatus, DemandedStatus demandedStatus,
			ASSET.Models.Movement.MovementCharacteristics moves);
}