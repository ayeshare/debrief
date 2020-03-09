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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Vector;

import ASSET.GUI.CommandLine.NewScenarioListener;
import MWC.Algorithms.LiveData.IAttribute;

public interface ISimulationQue {

	/**
	 * common set of attributes for all of the simulations
	 *
	 * @return
	 */
	public Vector<IAttribute> getAttributes();

	/**
	 * access my collection of simulations
	 *
	 * @return
	 */
	public Vector<ISimulation> getSimulations();

	/**
	 * how to listen for the state of the simulations changing
	 *
	 * @return
	 */
	public IAttribute getState();

	/**
	 * whether the que is running
	 *
	 * @return yes/no
	 */
	public boolean isRunning();

	/**
	 * perform a set of runs
	 *
	 * @param out
	 * @param err
	 * @param in
	 * @param object
	 */
	public int nowRun(PrintStream out, PrintStream err, InputStream in, NewScenarioListener scenarioListener);

	/**
	 * start the que running
	 *
	 */
	public void startQue(NewScenarioListener newListener);

	/**
	 * stop the que
	 *
	 */
	public void stopQue();

}