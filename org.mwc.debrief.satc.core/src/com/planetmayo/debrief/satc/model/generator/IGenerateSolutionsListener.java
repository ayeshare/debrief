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

package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.legs.CompositeRoute;

public interface IGenerateSolutionsListener {

	/**
	 * solution generator finished generation.
	 *
	 * If generation job finished successfully it will be called after
	 * solutionsReady.
	 *
	 * If generation job was canceled or finished with error this method will be
	 * called after jobManager closes all job resources
	 */
	void finishedGeneration(Throwable error);

	/**
	 * we have some solutions
	 * 
	 * @param routes
	 *
	 */
	void solutionsReady(CompositeRoute[] routes);

	/**
	 * we're about to start generating solutions
	 *
	 */
	void startingGeneration();
}