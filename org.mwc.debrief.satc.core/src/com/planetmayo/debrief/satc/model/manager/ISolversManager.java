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

package com.planetmayo.debrief.satc.model.manager;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.ISolver;

public interface ISolversManager {

	void addSolversManagerListener(ISolversManagerListener listener);

	ISolver createSolver(String name);

	/**
	 * if current active solver is solver specified in parameter the method
	 * deactivates it otherwise leaves active solver unchanged
	 * 
	 * @param solver
	 */
	void deactivateSolverIfActive(ISolver solver);

	/**
	 * @return active solver or null if no solver is active
	 */
	ISolver getActiveSolver();

	List<ISolver> getAvailableSolvers();

	void removeSolverManagerListener(ISolversManagerListener listener);

	/**
	 * Deactivates current active solver and activates specified one if it isn't
	 * null
	 * 
	 * @param solver
	 */
	void setActiveSolver(ISolver solver);
}
