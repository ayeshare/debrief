
package ASSET.Server;

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
 * message to indicate that a new scenario has been created
 *
 */
public interface ScenarioCreatedListener extends java.util.EventListener {
	/**
	 * pass on details of the creation of a new scenario
	 *
	 */
	public void scenarioCreated(int index);

	/**
	 * pass on details of the destruction of a scenario
	 *
	 */
	public void scenarioDestroyed(int index);
}