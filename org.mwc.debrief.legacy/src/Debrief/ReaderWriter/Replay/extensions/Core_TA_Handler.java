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
package Debrief.ReaderWriter.Replay.extensions;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble2;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.ExtensibleLineImporter;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import junit.framework.TestCase;

abstract class Core_TA_Handler implements ExtensibleLineImporter, PlainLineImporter.ImportRequiresFinalisation {

	private class Measurement {
		final public long time;

		public Measurement(final long time2) {
			this.time = time2;
		}
	}

	private class MeasurementDouble extends Measurement {
		final public double value;

		public MeasurementDouble(final long time, final double measurement) {
			super(time);
			value = measurement;
		}

	}

	private class MeasurementDouble2 extends Measurement {
		final public double value1;
		final public double value2;

		public MeasurementDouble2(final long time, final double val1, final double val2) {
			super(time);
			value1 = val1;
			value2 = val2;
		}
	}

	private static class MIndex {

		final private String platform_name;
		final private String sensor_name;
		final private String folder;
		final private String dataset_name;
		final private String units;
		final private String value1Name;
		final private String value2Name;

		public MIndex(final String platform_name, final String sensor_name, final String folder,
				final String dataset_name, final String units, final String value1Name, final String value2Name) {
			this.platform_name = platform_name;
			this.sensor_name = sensor_name;
			this.folder = folder;
			this.dataset_name = dataset_name;
			this.units = units;
			this.value1Name = value1Name;
			this.value2Name = value2Name;
		}

		@Override
		public boolean equals(final Object arg0) {
			final MIndex other = (MIndex) arg0;
			if (!platform_name.equals(other.platform_name))
				return false;
			else if (!sensor_name.equals(other.sensor_name))
				return false;
			else if (!folder.equals(other.folder))
				return false;
			else if (!dataset_name.equals(other.dataset_name))
				return false;
			else if (!units.equals(other.units))
				return false;
			else if (value1Name != null && !value1Name.equals(other.value1Name))
				return false;
			else if (value2Name != null && !value2Name.equals(other.value2Name))
				return false;
			else
				return true;
		}

		@Override
		public int hashCode() {
			int hash = platform_name.hashCode() + sensor_name.hashCode() + folder.hashCode() + dataset_name.hashCode()
					+ units.hashCode();
			if (value1Name != null) {
				hash += value1Name.hashCode() + value2Name.hashCode();
			}
			return hash;
		}

	}

	public static class TestStorage extends TestCase {
		public void testFolders() {
			final Layers layers = new Layers();
			final TrackWrapper track = new TrackWrapper();
			track.setName("Platform");
			final SensorWrapper sensor = new SensorWrapper("Sensor");
			track.add(sensor);
			layers.addThisLayer(track);

			final TA_Modules_DataHandler handler = new TA_Modules_DataHandler();
			handler.setLayers(layers);
			handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", "Some units", new HiResDate(1200000),
					12.33);
			handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", "Some units", new HiResDate(1300000),
					15.33);
			handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", "Some units", new HiResDate(1400000),
					11.33);
			handler.finalise();

			// check it worked
			// find the measurements
			DataFolder topF = (DataFolder) sensor.getAdditionalData().getThisType(DataFolder.class);
			if (topF == null) {
				topF = new DataFolder();
				sensor.getAdditionalData().add(topF);
			}

			topF.printAll();

			final DataFolder subF = (DataFolder) topF.get("Modules");
			final TimeSeriesDatasetDouble dataset = (TimeSeriesDatasetDouble) subF.get("Fore");
			assertEquals("has items", 3, dataset.size());

		}
	}

	protected static final String CENTRE_OF_GRAVITY = "Centre of Gravity";

	protected static boolean isNull(final String value) {
		return (value.toUpperCase().equals("NULL"));
	}

	private Layers _layers;

	private final String _myType;

	private Map<MIndex, List<Measurement>> _measurements = null;

	Core_TA_Handler(final String type) {
		_myType = type;
	}

	@Override
	final public boolean canExportThis(final Object val) {
		return false;
	}

	@Override
	final public String exportThis(final Plottable theShape) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void finalise() {
		if (_measurements != null) {
			// loop through datasets, storing them
			for (final MIndex key : _measurements.keySet()) {
				final List<Measurement> items = _measurements.get(key);

				// ok, build the dataset
				if (items.size() > 0) {
					final TimeSeriesDatasetCore ds;
					final Measurement first = items.get(0);
					if (first instanceof MeasurementDouble) {
						final double[] values = new double[items.size()];
						final long[] times = new long[items.size()];
						int ctr = 0;
						for (final Measurement meas : items) {
							final MeasurementDouble md = (MeasurementDouble) meas;
							times[ctr] = meas.time;
							values[ctr] = md.value;
							ctr++;
						}
						ds = new TimeSeriesDatasetDouble(key.dataset_name, key.units, times, values);
					} else if (first instanceof MeasurementDouble2) {
						final double[] values1 = new double[items.size()];
						final double[] values2 = new double[items.size()];
						final long[] times = new long[items.size()];
						int ctr = 0;
						for (final Measurement meas : items) {
							final MeasurementDouble2 md = (MeasurementDouble2) meas;
							times[ctr] = meas.time;
							values1[ctr] = md.value1;
							values2[ctr] = md.value2;
							ctr++;
						}
						ds = new TimeSeriesDatasetDouble2(key.dataset_name, key.units, key.value1Name, key.value2Name,
								times, values1, values2);
					} else {
						throw new IllegalArgumentException("We haven't built structure properly");
					}

					// store the dataset
					storeMeasurement2(key.platform_name, key.sensor_name, key.folder, ds);
				}
			}

			// clear the map
			_measurements.clear();
		}
	}

	/**
	 * find (or create) a folder with the given name
	 *
	 */
	private DataFolder getFolder(final DataFolder folder, final String name) {
		DataFolder res = null;

		for (final DataItem item : folder) {
			if (item.getName().equals(name) && (item instanceof DataFolder)) {
				res = (DataFolder) item;
				break;
			}
		}

		if (res == null) {
			res = new DataFolder(name);
			folder.add(res);
		}

		return res;
	}

	@Override
	final public String getSymbology() {
		return null;
	}

	@Override
	final public String getYourType() {
		return ";" + _myType + ":";
	}

	@Override
	final public void setLayers(final Layers parent) {
		_layers = parent;
	}

	/**
	 * store this measurement
	 *
	 * @param platform_name the platform to store the data under
	 * @param sensor_name   the sensor to store the data under (or null to go in the
	 *                      top level)
	 * @param folder        the folder to store the dataset into (use "/" to
	 *                      indicate sub-folders)
	 * @param dataset_name  the dataset to put the measurement into
	 * @param theDate       the time of the measurement
	 * @param measurement   the measurement
	 */
	protected void storeMeasurement(final String platform_name, final String sensor_name, final String folder,
			final String dataset_name, final String units, final HiResDate theDate, final double measurement) {
		// create the measurement
		final MeasurementDouble measure = new MeasurementDouble(theDate.getDate().getTime(), measurement);

		storeValue(platform_name, sensor_name, folder, dataset_name, units, measure, null, null);

	}

	private void storeMeasurement2(final String platform_name, final String sensor_name, final String folder,
			final TimeSeriesDatasetCore dataset) {

		// find the platform
		final TrackWrapper track = (TrackWrapper) _layers.findLayer(platform_name);
		if (track == null) {
			System.err.println("Track not found for:" + platform_name);
			return;
		}

		// find the sensor
		SensorWrapper ourSensor = null;
		final BaseLayer sensors = track.getSensors();
		final Enumeration<Editable> numer = sensors.elements();
		while (numer.hasMoreElements()) {
			final SensorWrapper thisSensor = (SensorWrapper) numer.nextElement();
			if (thisSensor.getName().equals(sensor_name)) {
				ourSensor = thisSensor;
				break;
			}
		}

		if (ourSensor == null) {
			// ok, create an empty sensor?
			ourSensor = new SensorWrapper(sensor_name);
			track.getSensors().add(ourSensor);
		}

		// find the measurements
		DataFolder dataFolder = (DataFolder) ourSensor.getAdditionalData().getThisType(DataFolder.class);
		if (dataFolder == null) {
			dataFolder = new DataFolder();
			ourSensor.getAdditionalData().add(dataFolder);
		}

		// walk the folder structure, if necessary
		if (folder != null) {
			if (folder.contains("/")) {
				final String[] levels = folder.split("/");
				for (final String level : levels) {
					dataFolder = getFolder(dataFolder, level);
				}
			} else {
				dataFolder = getFolder(dataFolder, folder);
			}
		}

		// add the dataset to this folder
		dataFolder.add(dataset);

	}

	/**
	 * store this measurement
	 *
	 * @param platform_name the platform to store the data under
	 * @param sensor_name   the sensor to store the data under (or null to go in the
	 *                      top level)
	 * @param folder        the folder to store the dataset into (use "/" to
	 *                      indicate sub-folders)
	 * @param dataset_name  the dataset to put the measurement into
	 * @param theDate       the time of the measurement
	 * @param measurement   the measurement
	 */
	protected void storeMeasurement2D(final String platform_name, final String sensor_name, final String folder,
			final String dataset_name, final String units, final HiResDate theDate, final String value1Name,
			final String value2Name, final double measurement1, final double measurement2) {
		// create the measurement
		final MeasurementDouble2 measure = new MeasurementDouble2(theDate.getDate().getTime(), measurement1,
				measurement2);

		// and store it
		storeValue(platform_name, sensor_name, folder, dataset_name, units, measure, value1Name, value2Name);
	}

	private void storeValue(final String platform_name, final String sensor_name, final String folder,
			final String dataset_name, final String units, final Measurement measure, final String value1Name,
			final String value2Name) {
		// create the index value
		final MIndex mindex = new MIndex(platform_name, sensor_name, folder, dataset_name, units, value1Name,
				value2Name);

		if (_measurements == null) {
			_measurements = new HashMap<MIndex, List<Measurement>>();
		}

		List<Measurement> thisList = _measurements.get(mindex);
		if (thisList == null) {
			thisList = new ArrayList<Measurement>();
			_measurements.put(mindex, thisList);
		}

		thisList.add(measure);
	}
}
