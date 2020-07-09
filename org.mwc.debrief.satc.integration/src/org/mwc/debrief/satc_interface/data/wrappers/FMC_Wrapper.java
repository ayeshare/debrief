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
import java.io.Serializable;

import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution.CoreMeasurement;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution.FMeasurement;

import MWC.GUI.Editable;

public class FMC_Wrapper extends
		CoreLayer_Wrapper<FrequencyMeasurementContribution, FMeasurement, FMC_Wrapper.FrequencyMeasurementEditable> {

	public class FMC_Info extends Editable.EditorType implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public FMC_Info(final FMC_Wrapper data) {
			super(data, data.getName(), "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "name of this contribution", EditorType.FORMAT),
						displayProp("SoundSpeed", "Sound speed", "Speed of Sound (kts)", EditorType.SPATIAL),
						displayProp("BaseFrequency", "Base frequency", "Base radiated frequency", EditorType.SPATIAL) };

				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public class FrequencyMeasurementEditable extends CoreLayer_Wrapper.CoreMeasurementWrapper {

		// ///////////////////////////////////////////////////////////
		// info class
		// //////////////////////////////////////////////////////////
		public class Meas_Info extends Editable.EditorType implements Serializable {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Meas_Info(final FrequencyMeasurementEditable data) {
				super(data, data.getName(), "");
			}

			@Override
			public PropertyDescriptor[] getPropertyDescriptors() {
				try {
					final PropertyDescriptor[] res = {
							prop("Active", "whether to use this bearing", EditorType.OPTIONAL),
							prop("Frequency", "Measured value of frequency", EditorType.SPATIAL), };

					return res;
				} catch (final IntrospectionException e) {
					return super.getPropertyDescriptors();
				}
			}
		}

		private EditorType _myEditor;

		private double Frequency;

		public FrequencyMeasurementEditable(final CoreMeasurement measurement) {
			super(measurement);
		}

		private FMeasurement getFM() {
			return (FMeasurement) _myMeas;
		}

		public double getFrequency() {
			return getFM().getFrequency();
		}

		@Override
		public EditorType getInfo() {
			if (_myEditor == null)
				_myEditor = new Meas_Info(this);
			return _myEditor;
		}

		public void setFrequency(final double frequency) {
			getFM().setFrequency(frequency);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FMC_Wrapper(final FrequencyMeasurementContribution contribution) {
		super(contribution);
	}

	@Override
	protected void addThis(final CoreMeasurement meas) {
		final FrequencyMeasurementEditable thisMe = new FrequencyMeasurementEditable(meas);
		_myElements.add(thisMe);
	}

	public double getBaseFrequency() {
		return getFMC().getBaseFrequency();
	}

	private FrequencyMeasurementContribution getFMC() {
		return (FrequencyMeasurementContribution) super.getBMC();
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new FMC_Info(this);
		return _myEditor;
	}

	public double getSoundSpeed() {
		return getFMC().getSoundSpeed();
	}

	public void setBaseFrequency(final double baseFrequency) {
		getFMC().setBaseFrequency(baseFrequency);
	}

	public void setSoundSpeed(final double soundSpeed) {
		getFMC().setSoundSpeed(soundSpeed);
	}

}