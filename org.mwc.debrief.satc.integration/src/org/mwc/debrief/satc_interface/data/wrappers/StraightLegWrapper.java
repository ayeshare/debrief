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

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public class StraightLegWrapper extends ContributionWrapper {

	public static class StraightLegInfo extends EditorType {

		public StraightLegInfo(final StraightLegWrapper data) {
			super(data, "Straight Leg", "Straight Leg");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the Name of this leg", FORMAT),
						displayProp("_Start", "Start", "the start date of this leg", FORMAT),
						prop("End", "the finish date of this leg", FORMAT) };

				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	public StraightLegWrapper(final BaseContribution contribution) {
		super(contribution);
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new StraightLegInfo(this);

		return _myEditor;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

}
