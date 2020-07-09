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

package ASSET.Scenario.LiveScenario;

import MWC.Algorithms.LiveData.Attribute;

public abstract class Simulation implements ISimulation {

	/**
	 * the current (watchable) time
	 *
	 */
	protected Attribute _time;

	/**
	 * name of this simulation
	 *
	 */
	private final String _name;

	/**
	 * the current simulation time
	 *
	 */
	private long _currentTime;

	protected boolean _isRunning = false;

	public Simulation(final String name) {
		_name = name;

		_time = new Attribute("Time", "n/a", true);
		_time.fireUpdate(this, getTime(), 0);

	}

	/**
	 * simulation is complete, inform listeners
	 *
	 */
	protected void complete() {
		_isRunning = false;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public long getTime() {
		return _currentTime;
	}

	public boolean isRunning() {
		return _isRunning;
	}

	@Override
	public void start() {
		_isRunning = true;
	}

	@Override
	public void stop() {
		_isRunning = false;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
