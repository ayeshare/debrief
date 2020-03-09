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

package org.mwc.debrief.multipath2;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.IMemento;
import org.jfree.data.time.TimeSeries;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.multipath2.MultiPathPresenter.Display.FileHandler;
import org.mwc.debrief.multipath2.MultiPathPresenter.Display.ValueHandler;
import org.mwc.debrief.multipath2.model.MultiPathModel;
import org.mwc.debrief.multipath2.model.MultiPathModel.CalculationException;
import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;
import org.mwc.debrief.multipath2.model.SVP;
import org.mwc.debrief.multipath2.model.TimeDeltas;

import MWC.GenericData.WatchableList;
import MWC.TacticalData.TrackDataProvider;
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

public class MultiPathPresenter {

	/**
	 * UI component of multipath analysis
	 *
	 * @author ianmayo
	 *
	 */
	public static interface Display {
		/**
		 * interface for anybody that wants to know about files being dropped
		 *
		 * @author ianmayo
		 *
		 */
		public static interface FileHandler {
			/**
			 * the specified file has been dropped
			 *
			 * @param path
			 */
			void newFile(String path);
		}

		/**
		 * interface for anybody that wants to know about a slider being dragged
		 *
		 * @author ianmayo
		 *
		 */
		public static interface ValueHandler {
			/**
			 * the new value on the slider
			 *
			 * @param val
			 */
			void newValue(int val);
		}

		/**
		 * let someone know about the drag-handle being dragged
		 *
		 * @param handler
		 */
		public void addDragHandler(ValueHandler handler);

		/**
		 * let someone listen out for the magic button being pressed
		 *
		 * @param listener
		 */
		public void addMagicListener(SelectionListener listener);

		/**
		 * let someone know about a ranges file being dropped
		 *
		 * @param handler
		 */
		public void addRangesListener(FileHandler handler);

		/**
		 * let someone know about a new SVP file being dropped
		 *
		 * @param handler
		 */
		public void addSVPListener(FileHandler handler);

		/**
		 * let someone know about a new time-delta file being dropped
		 *
		 * @param handler
		 */
		public void addTimeDeltaListener(FileHandler handler);

		/**
		 * show these data series
		 *
		 * @param _measuredSeries
		 * @param calculated
		 */
		public void display(TimeSeries _measuredSeries, TimeSeries calculated);

		/**
		 * see if anybody can provide tracks
		 *
		 * @return
		 */
		public TrackDataProvider getDataProvider();

		/**
		 * indicate whether the slider should be enabled
		 *
		 * @param b yes/no
		 */
		public void setEnabled(boolean b);

		/**
		 * show the interval filename
		 *
		 * @param fName
		 */
		public void setIntervalName(String fName);

		/**
		 * specify the upper limit for the slider
		 *
		 * @param maxDepth
		 */
		public void setSliderMax(int maxDepth);

		/**
		 * feedback on the current slider value
		 *
		 * @param val
		 */
		public void setSliderText(String text);

		/**
		 * set the value on the slider
		 *
		 * @param _curDepth
		 */
		public void setSliderVal(int _curDepth);

		/**
		 * show the SVP filename
		 *
		 * @param fName
		 */
		public void setSVPName(String fName);

		/**
		 * there's a problem, show it to the user
		 *
		 * @param string
		 */
		public void showError(String string);
	}

	private static final String INTERVAL_FILE = "INTERVAL_FILE";
	private static final String SVP_FILE = "SVP_FILE";
	public static final int DEFAULT_DEPTH = 50;

	private static final String DEPTH_VAL = "DEPTH_VAL";;

	protected final Display _display;
	protected final MultiPathModel _model;
	private TimeSeries _measuredSeries = null;
	protected SVP _svp = null;
	protected TimeDeltas _times = null;
	protected String _intervalPath;
	protected int _curDepth = DEFAULT_DEPTH;
	protected String _svpPath;
	protected ValueHandler _dragHandler;

	/**
	 * initialise presenter
	 *
	 * @param display
	 * @param model
	 */
	public MultiPathPresenter(final Display display) {
		_display = display;
		_model = new MultiPathModel();

	}

	/**
	 * connect the presenter to the UI component, now that it's initialised
	 *
	 */
	public void bind() {
		// setup assorted listeners
		_dragHandler = new ValueHandler() {
			@Override
			public void newValue(final int val) {
				updateCalc(val);
			}
		};
		_display.addDragHandler(_dragHandler);

		_display.addSVPListener(new FileHandler() {
			@Override
			public void newFile(final String path) {
				loadSVP(path);
			}
		});

		_display.addTimeDeltaListener(new FileHandler() {
			@Override
			public void newFile(final String path) {
				loadIntervals(path);
			}
		});

		_display.addMagicListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				doMagic();
			}
		});

		// aah, do we have any pending files?
		if (_svpPath != null)
			loadSVP(_svpPath);

		if (_intervalPath != null)
			loadIntervals(_intervalPath);

		// lastly, check if we're enabled.
		checkEnablement();
	}

	/**
	 * see if we have sufficient data to enable the display
	 *
	 */
	protected void checkEnablement() {

		if (_svpPath == null) {
			// disable it
			_display.setEnabled(false);

			// show error
			_display.setSliderText("Waiting for SVP");
		} else if (_intervalPath == null) {
			// disable it
			_display.setEnabled(false);

			// show error
			_display.setSliderText("Waiting for interval data");
		} else {
			// just check the init is complete
			if ((_svp != null) && (_times != null)) {

				// enable it
				_display.setEnabled(true);

				// initialise the slider
				_display.setSliderVal(_curDepth);

				// and give it a sensible start value
				_dragHandler.newValue(_curDepth);
			}
		}
	}

	protected MinimisationFunction createMiracle() {
		// get the tracks
		final TrackDataProvider tv = getTracks();
		final WatchableList primary = tv.getPrimaryTrack();
		final WatchableList secondary = tv.getSecondaryTracks()[0];

		return new MultiPathModel.MiracleFunction(primary, secondary, _svp, _times);
	}

	protected void disableUI() {

	}

	/**
	 * do an optimisation on the current datasets
	 *
	 */
	protected void doMagic() {
		// Create instace of class holding function to be minimised
		final MinimisationFunction funct = createMiracle();

		// Create instance of Minimisation
		final Minimisation min = new Minimisation();

		// initial estimates
		final double[] start = { 100 };

		// initial step sizes
		final double[] step = { 60 };

		// convergence tolerance
		final double ftol = 1e-8;

		// set the minimum depth
		min.addConstraint(0, -1, 0d);

		// and the maximum depth
		final double maxDepth = _svp.getMaxDepth();
		min.addConstraint(0, 1, maxDepth);

		// Nelder and Mead minimisation procedure
		min.nelderMead(funct, start, step, ftol, 500);

		// get the results out
		final double[] param = min.getParamValues();

		final double depth = param[0];

		CorePlugin.logError(IStatus.INFO, "Optimised multipath depth is " + depth, null);

		// fire in the minimum
		updateCalc((int) depth);

		_display.setSliderVal((int) depth);

	}

	protected TimeSeries getCalculatedProfile(final int val) {
		TimeSeries res = null;
		final TrackDataProvider tv = getTracks();

		// check we've actually got some tracks
		if (tv != null) {
			final WatchableList primary = tv.getPrimaryTrack();
			final WatchableList secondary = tv.getSecondaryTracks()[0];
			if ((primary != null) && (secondary != null)) {
				res = _model.getCalculatedProfileFor(primary, secondary, _svp, _times, val);
			}
		}
		return res;
	}

	private TrackDataProvider getTracks() {
		// do we have a tote?
		TrackDataProvider tv = _display.getDataProvider();

		// do we only have one seconary?
		if (tv == null) {
			_display.showError("Must have a primary and a secondary track on the tote");
		} else if (tv.getPrimaryTrack() == null) {
			tv = null;
			_display.showError("Needs a primary track (or location)");
		} else if (tv.getSecondaryTracks() == null) {
			tv = null;
			_display.showError("Secondary track missing");
		} else if (tv.getSecondaryTracks().length == 0) {
			tv = null;
			_display.showError("Secondary track missing");
		} else if (tv.getSecondaryTracks().length > 1) {
			tv = null;
			_display.showError("Too many secondary tracks");
		} else if (_times == null) {
			tv = null;
			_display.showError("Waiting for interval data");
		}
		return tv;
	}

	/**
	 * initialise ourselves from the memento
	 *
	 */
	public void init(final IMemento memento) {
		if (memento != null) {
			_svpPath = memento.getString(SVP_FILE);
			_intervalPath = memento.getString(INTERVAL_FILE);
			final Integer depth = memento.getInteger(DEPTH_VAL);
			if (depth != null)
				_curDepth = depth;
		}
	}

	/**
	 * load the intervals from the supplied file
	 *
	 * @param path
	 */
	protected void loadIntervals(final String path) {
		try {
			// clear the path = we'll overwrite it if we're successful
			_intervalPath = null;
			_display.setIntervalName("[pending]");

			_times = new TimeDeltas();

			_times.load(path);

			_intervalPath = path;

			// reset the measured series
			_measuredSeries = null;

			// get the filename
			final File file = new File(path);
			final String fName = file.getName();
			_display.setIntervalName(fName);

		} catch (final ParseException e) {
			CorePlugin.logError(IStatus.ERROR, "time-delta formatting problem", e);
			_times = null;
		} catch (final IOException e) {
			CorePlugin.logError(IStatus.ERROR, "time-delta file-read problem", e);
			_times = null;
		} catch (final DataFormatException e) {
			_display.showError("Does not look like interval data");
		}

		// check if UI should be enabled
		checkEnablement();
	}

	protected void loadSVP(final String path) {
		try {

			// clear the path = we'll overwrite it if we're successful
			_svpPath = null;
			_display.setSVPName("[pending]");

			_svp = new SVP();

			_svp.load(path);

			_svpPath = path;

			// display the filename
			final File file = new File(path);
			final String fName = file.getName();
			_display.setSVPName(fName);

			// and set the depth slider max value
			final double maxDepth = _svp.getMaxDepth();
			_display.setSliderMax((int) maxDepth);

		} catch (final ParseException e) {
			CorePlugin.logError(IStatus.ERROR, "time-delta formatting problem", e);
			_svp = null;
		} catch (final IOException e) {
			CorePlugin.logError(IStatus.ERROR, "time-delta file-read problem", e);
			_svp = null;
		} catch (final DataFormatException e) {
			_display.showError("Does not look like SVP data");
		}

		// check if UI should be enabled
		checkEnablement();

	}

	public void saveState(final IMemento memento) {
		// store the filenames
		if (_svpPath != null)
			memento.putString(SVP_FILE, _svpPath);

		if (_intervalPath != null)
			memento.putString(INTERVAL_FILE, _intervalPath);

		memento.putInteger(DEPTH_VAL, _curDepth);
	}

	protected void updateCalc(final int val) {

		// do we have our measured series?
		if (_measuredSeries == null) {
			_measuredSeries = _model.getMeasuredProfileFor(_times);
		}

		// remember the depth
		_curDepth = val;

		// cool, valid data
		_display.setSliderText("Depth:" + val + "m");

		TimeSeries calculated = null;
		try {
			calculated = getCalculatedProfile(val);
		} catch (final CalculationException e) {
			final String trouble = e.getMessage();
			_display.setSliderText(trouble);
		}

		if (calculated != null) {
			// ok, ready to plot
			_display.display(_measuredSeries, calculated);
		}
	}
}
