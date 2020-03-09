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

package org.mwc.debrief.satc_interface.data.wrappers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

public class CourseForecastWrapper extends ContributionWrapper {

	public static class CourseInfo extends EditorType {

		public CourseInfo(final CourseForecastWrapper data) {
			super(data, "Course Forecast", "CourseForecast");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the Name of this leg", FORMAT),
						displayProp("_Start", "Start date", "the start date of this leg", FORMAT),
						displayProp("End", "End date", "the finish date of this leg", FORMAT),
						displayProp("MinCourse", "Minimum course", "the minimum course", SPATIAL),
						displayProp("MaxCourse", "Maximum course", "the maximum course", SPATIAL)

				};

				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	private final CourseForecastContribution _courseF;

	public CourseForecastWrapper(final CourseForecastContribution contribution) {
		super(contribution);
		_courseF = contribution;
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new CourseInfo(this);

		return _myEditor;
	}

	public double getMaxCourse() {
		return (int) Math.toDegrees(_courseF.getMaxCourse());
	}

	public double getMinCourse() {
		return (int) Math.toDegrees(_courseF.getMinCourse());
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	public void setMaxCourse(final double maxCourse) {
		_courseF.setMaxCourse(Math.toRadians(maxCourse));
	}

	public void setMinCourse(final double minCourse) {
		_courseF.setMinCourse(Math.toRadians(minCourse));
	}

}
