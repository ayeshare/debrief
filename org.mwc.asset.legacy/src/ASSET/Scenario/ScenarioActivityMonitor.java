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

package ASSET.Scenario;

import ASSET.ParticipantType;
import MWC.GenericData.WorldLocation;

/**
 * class used to represent an object which listens out for significant activity
 * within a scenario, typically weapon detonations (see
 * {@link ASSET.Models.Decision.Tactical.Detonate detonate}) and creation of new
 * participants ({@link ASSET.Models.Decision.Tactical.LaunchWeapon firing a
 * weapon}) or ({@link ASSET.Models.Decision.Tactical.LaunchSensor dropping a
 * sonar buoy}) triggers this).
 */

public interface ScenarioActivityMonitor {
	/**
	 * method to add a new participant
	 *
	 * @param newPart the new participant
	 *
	 */
	public void createParticipant(ASSET.ParticipantType newPart);

	/**
	 * the detonation event itself
	 *
	 * @param id    the id of the weapon which exploded (or INVALID_ID)
	 * @param loc   the location of the detonation
	 * @param power the strength of the detonation
	 */
	public void detonationAt(int id, WorldLocation loc, double power);

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	public Integer[] getListOfParticipants();

	/**
	 * method to get a participant
	 *
	 * @param id the id of the participant
	 * @return the participant we're looking for
	 *
	 */
	public ParticipantType getThisParticipant(final int id);

}
