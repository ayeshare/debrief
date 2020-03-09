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

package ASSET.GUI.Editors.Sensors;

import java.util.Vector;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.CoreSensor;

public abstract class BaseSensorViewer extends MWC.GUI.Properties.Swing.SwingCustomEditor
		implements java.beans.PropertyChangeListener, MWC.GUI.Properties.NoEditorButtons {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the list of sensor components we build up, consuming them at each refresh
	 */
	protected Vector<Object> _sensorEvents = new Vector<Object>(0, 1);
	/**
	 * the sensor we're listening to
	 */
	protected SensorType _mySensor;

	@Override
	public void doClose() {
		// stop listening to the sensor
		_mySensor.removeSensorCalculationListener(this);

		super.doClose();
	}

	protected abstract void initForm();

	/**
	 * start listening to this sensor
	 *
	 * @param newSensor the sensor to listen to
	 */
	abstract protected void listenTo(SensorType newSensor);

	/**
	 * receive update signals
	 *
	 * @param pe the changed data
	 */
	@Override
	public void propertyChange(final java.beans.PropertyChangeEvent pe) {
		// get the name
		final String type = pe.getPropertyName();

		// is it a new detection?
		if (type == CoreSensor.SENSOR_COMPONENT_EVENT) {
			// store these components
			_sensorEvents.add(pe.getNewValue());

		}
		// does it mark the end of this step?
		else if (type == CoreSensor.DETECTION_CYCLE_COMPLETE) {
			// update the GUI
			updateForm();

			// clear our local list
			_sensorEvents.clear();
		}
	}
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////

	@Override
	public void setObject(final Object value) {
		setValue(value);
	}

	public void setValue(final Object value) {
		//
		if (value instanceof SensorFitEditor.WrappedSensor) {
			//
			final SensorFitEditor.WrappedSensor wrapper = (SensorFitEditor.WrappedSensor) value;

			// store the sensor.
			_mySensor = wrapper.MySensor;

			// ok, start listening to the sensor
			listenTo(_mySensor);

			// create the form
			initForm();

			// and update it.
			updateForm();

		}
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	protected abstract void updateForm();

	////////////////////////////////////////////////////
	// member constructor
	////////////////////////////////////////////////////

	////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////
}
