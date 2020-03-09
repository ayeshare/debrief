
package ASSET.Scenario;

import ASSET.ScenarioType;

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
 * changes in participant members of the scenario
 *
 */
public interface ParticipantsChangedListener extends java.util.EventListener {
	/**
	 * the indicated participant has been added to the scenario
	 *
	 */
	public void newParticipant(int index);

	/**
	 * the indicated participant has been removed from the scenario
	 *
	 */
	public void participantRemoved(int index);

	/**
	 * the scenario has restarted, reset
	 *
	 * @param scenario TODO
	 *
	 */
	public void restart(ScenarioType scenario);
}