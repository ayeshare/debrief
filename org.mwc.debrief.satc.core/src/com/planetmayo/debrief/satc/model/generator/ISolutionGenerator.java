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

import org.mwc.debrief.track_shift.zig_detector.Precision;

import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

/**
 * interface to generate solutions on constrained problem space
 */
public interface ISolutionGenerator {

	/**
	 * someone's interested in solution generation
	 *
	 * @param listener
	 */
	void addReadyListener(IGenerateSolutionsListener listener);

	/**
	 * cancels generate solutions job
	 */
	void cancel();

	/**
	 * clears
	 */
	void clear();

	/**
	 * starts generate solutions job
	 */
	void generateSolutions(boolean fullRerun);

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 *
	 * @return yes/no
	 */
	boolean getAutoSuppress();

	/**
	 * returns current precision
	 */
	Precision getPrecision();

	/**
	 * returns problem space which is used by solution generator
	 */
	SafeProblemSpace getProblemSpace();

	/**
	 * someone's no longer interested in solution generation
	 *
	 * @param listener
	 */
	void removeReadyListener(IGenerateSolutionsListener listener);

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 *
	 * @param autoSuppress yes/no
	 */
	void setAutoSuppress(boolean autoSuppress);

	/**
	 * specify how detailed the maths should be
	 *
	 * @param precision
	 */
	void setPrecision(Precision precision);
}