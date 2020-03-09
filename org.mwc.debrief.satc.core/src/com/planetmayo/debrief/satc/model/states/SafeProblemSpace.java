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

package com.planetmayo.debrief.satc.model.states;

import java.util.Collection;
import java.util.Date;

import com.planetmayo.debrief.satc.model.VehicleType;

public class SafeProblemSpace {
	private final ProblemSpace problemSpace;

	public SafeProblemSpace(final ProblemSpace problemSpace) {
		this.problemSpace = problemSpace;
	}

	public BoundedState getBoundedStateAt(final Date theTime) {
		return problemSpace.getBoundedStateAt(theTime);
	}

	public Collection<BoundedState> getBoundedStatesBetween(final Date startDate, final Date finishDate) {
		return problemSpace.getBoundedStatesBetween(startDate, finishDate);
	}

	public Date getFinishDate() {
		return problemSpace.getFinishDate();
	}

	public Date getStartDate() {
		return problemSpace.getStartDate();
	}

	public VehicleType getVehicleType() {
		return problemSpace.getVehicleType();
	}

	public int size() {
		return problemSpace.size();
	}

	public Collection<BoundedState> states() {
		return problemSpace.states();
	}

	public Collection<BoundedState> statesAfter(final BoundedState state) {
		return problemSpace.statesAfter(state);
	}

	public Collection<BoundedState> times() {
		return problemSpace.states();
	}
}
