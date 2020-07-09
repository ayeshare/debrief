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

package ASSET;

import ASSET.Server.ScenarioCreatedListener;

/**
 * Interface representing ServerApp objects. A server manages a collection of
 * scenarios, proving external objects a list of them together with their
 * details. Also supports creation of new scenarios.
 */
public interface ServerType {

	/****************************************************
	 * member constants
	 ***************************************************/
	/**
	 * id of an invalid scenario
	 *
	 */
	public final static int INVALID_SCENARIO = -1;

	/**
	 * add object as a listener for this type of event
	 *
	 */
	public void addScenarioCreatedListener(ScenarioCreatedListener listener);

	/**
	 * destroy a scenario (calls the close() method on the scenario, which triggers
	 * the close)
	 *
	 */
	public void closeScenario(int index);

	/**
	 * Create a new scenario. The external client can then request the scenario
	 * itself to perform any edits
	 *
	 * @param scenario_type the type of scenario the client wants
	 * @return the id of the new scenario
	 */
	int createNewScenario(String scenario_type);

	/**
	 * Provide a list of id numbers of scenarios we contain
	 *
	 * @return list of ids of scenarios we contain
	 */
	public Integer[] getListOfScenarios();

	/**
	 * Return a particular scenario - so that the scenario can be controlled
	 * directly. Listeners added/removed. Participants added/removed, etc.
	 */
	ScenarioType getThisScenario(int id);

	/**
	 * remove listener for this type of event
	 *
	 */
	public void removeScenarioCreatedListener(ScenarioCreatedListener listener);

}
