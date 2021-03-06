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

package com.planetmayo.debrief.satc.model.contributions;

import java.beans.PropertyChangeListener;
import java.util.Date;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;

public class CompositeStraightLegForecastContribution extends StraightLegForecastContribution {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String SPEED = "speed";
	public static final String COURSE = "course";

	/**
	 * the speed forecast
	 *
	 */
	private final SpeedForecastContribution speed = new SpeedForecastContribution();

	/**
	 * the course forecast
	 *
	 */
	private final CourseForecastContribution course = new CourseForecastContribution();

	public CompositeStraightLegForecastContribution() {
		speed.setMinSpeed(null);
		speed.setMaxSpeed(null);
		speed.setEstimate(null);
		course.setMinCourse(null);
		course.setMaxCourse(null);
		course.setEstimate(null);
	}

	@Override
	public void actUpon(final ProblemSpace space) throws IncompatibleStateException {
		// handle the straight leg component first
		super.actUpon(space);

		// now apply the constraints from the speed forecast
		speed.actUpon(space);

		// and the course forecast
		course.actUpon(space);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		course.addPropertyChangeListener(listener);
		speed.addPropertyChangeListener(listener);

	}

	@Override
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		super.addPropertyChangeListener(propertyName, listener);
		course.addPropertyChangeListener(propertyName, listener);
		speed.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	protected double calcError(final State thisState) {
		// combine the three errors
		return super.calcError(thisState) + course.calcError(thisState) + speed.calcError(thisState);
	}

	/**
	 * get the course constraint
	 *
	 * @return
	 */
	public CourseForecastContribution getCourse() {
		return course;
	}

	/**
	 * get the speed constraint
	 *
	 * @return
	 */
	public SpeedForecastContribution getSpeed() {
		return speed;
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		course.removePropertyChangeListener(listener);
		speed.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		super.removePropertyChangeListener(propertyName, listener);
		course.removePropertyChangeListener(propertyName, listener);
		speed.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public void setFinishDate(final Date newFinishDate) {
		// set the parent value
		super.setFinishDate(newFinishDate);

		// now set the child values
		course.setFinishDate(newFinishDate);
		speed.setFinishDate(newFinishDate);
	}

	@Override
	public void setStartDate(final Date newStartDate) {
		// set the parent value
		super.setStartDate(newStartDate);

		// now set the child values
		course.setStartDate(newStartDate);
		speed.setStartDate(newStartDate);
	}

}
