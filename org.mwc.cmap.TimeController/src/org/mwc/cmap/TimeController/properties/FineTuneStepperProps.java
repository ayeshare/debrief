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

package org.mwc.cmap.TimeController.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.mwc.cmap.TimeController.controls.DTGBiSlider;

import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;

public class FineTuneStepperProps implements Editable {

	//////////////////////////////////////////////////////
	// bean info for this class
	/////////////////////////////////////////////////////
	public final class StepperInfo extends Editable.EditorType {

		public StepperInfo(final FineTuneStepperProps data) {
			super(data, "Stepper", "Time stepper");
		}

		@Override
		public final PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Value", "the exact value of the time perid marker") };
				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * the slider we're controlling
	 *
	 */
	DTGBiSlider _slider;

	/**
	 * whether we'er editing the start or finish marker
	 *
	 */
	boolean _doMin;

	Editable.EditorType _myInfo;

	/**
	 * create the editable properties for this slider
	 *
	 * @param rangeSlider
	 * @param doMinVal
	 */
	public FineTuneStepperProps(final DTGBiSlider rangeSlider, final boolean doMinVal) {
		_slider = rangeSlider;
		_doMin = doMinVal;
	}

	/////////////////////////////////////////////////////////////////
	// editable support
	/////////////////////////////////////////////////////////////////

	@Override
	public EditorType getInfo() {
		if (_myInfo == null)
			_myInfo = new StepperInfo(this);

		return _myInfo;
	}

	@Override
	public String getName() {
		return "Time slider marker";
	}

	public HiResDate getValue() {
		HiResDate currentVal;
		if (_doMin)
			currentVal = _slider.getPeriod().getStartDTG();
		else
			currentVal = _slider.getPeriod().getEndDTG();
		return currentVal;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	public void setValue(final HiResDate dtg) {
		if (_doMin)
			_slider.updateSelectedRanges(dtg, _slider.getPeriod().getEndDTG());
		else
			_slider.updateSelectedRanges(_slider.getPeriod().getStartDTG(), dtg);
	}

}
