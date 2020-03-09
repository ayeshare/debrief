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

package org.mwc.cmap.TimeController.views;

import java.util.Date;

import MWC.GenericData.HiResDate;
import junit.framework.TestCase;

abstract class SliderRangeManagement {

	public static class TestRangeMgt extends TestCase {
		static class MySlider extends SliderRangeManagement {
			@Override
			public void setEnabled(final boolean val) {
				_isEnabled = val;
			}

			@Override
			public void setMaxVal(final int max) {
				_maxVal = max;
			}

			@Override
			public void setMinVal(final int min) {
				_minVal = min;
			}

			@Override
			public void setTickSize(final int small, final int large, final int drag) {
				_small = small;
				_large = large;
				_drag = drag;
			}

		}

		private static int _maxVal, _minVal;
		private static int _small, _large, _drag;

		private static boolean _isEnabled;

		@Override
		public void setUp() {
			_small = _large = _drag = -1;
			_minVal = _maxVal = -1;
			_isEnabled = false;
		}

		@SuppressWarnings("deprecation")
		public void testRanges() {

			// check we're initialised
			assertEquals("not initialised", _small, -1);

			final MySlider slider = new MySlider();
			HiResDate minR = new HiResDate(new Date(0));
			HiResDate maxR = new HiResDate(new Date(500));
			slider.resetRange(minR, maxR);

			assertEquals("is enabled", true, _isEnabled);
			assertEquals("correct for tiny range", 0, _minVal);
			assertEquals("correct for tiny range", 500000, _maxVal);
			assertEquals("correct for tiny range", 1, _small);
			assertEquals("correct for tiny range", 10000, _large);
			assertEquals("correct for tiny range", 0, _drag);

			// try a couple of conversions
			int sVal = slider.toSliderUnits(new HiResDate(new Date(250)));
			assertEquals("correct slider value", 250000, sVal);
			final long sMicros = slider.fromSliderUnits(250000, 1).getMicros();
			assertEquals("correct slider value", 250000, sMicros);

			minR = new HiResDate(new Date(2009, 1, 1, 12, 0, 0));
			maxR = new HiResDate(new Date(2009, 1, 1, 13, 0, 0));
			slider.resetRange(minR, maxR);

			assertEquals("is enabled", true, _isEnabled);
			assertEquals("correct for mid range", 0, _minVal);
			assertEquals("correct for mid range", 3600, _maxVal);
			assertEquals("correct for mid range", 1, _small);
			assertEquals("correct for mid range", 600, _large);
			assertEquals("correct for mid range", 0, _drag);

			// try a couple of conversions
			sVal = slider.toSliderUnits(new HiResDate(new Date(2009, 1, 1, 12, 0, 30)));
			assertEquals("correct slider value", 30, sVal);
			final HiResDate dt = slider.fromSliderUnits(250000, 1);
			final String dtStr = MWC.Utilities.TextFormatting.FormatRNDateTime.toMediumString(dt.getDate().getTime());
			System.out.println(dtStr);
			assertEquals("correct slider value", 250000, sMicros);

			minR = new HiResDate(new Date(2009, 1, 1, 12, 0, 0));
			maxR = new HiResDate(new Date(2090, 1, 1, 12, 0, 1));
			slider.resetRange(minR, maxR);

			assertEquals("is in minutes mode", MINUTE_STEP, slider._timeScalar);
			assertEquals("is enabled", true, _isEnabled);
			assertEquals("correct for large range", 0, _minVal);
			assertEquals("correct for large range", 42602400, _maxVal);
			assertEquals("correct for large range", 1, _small);
			assertEquals("correct for large range", 6000000, _large);
			assertEquals("correct for large range", 0, _drag);

		}
	}

	private static final int MINUTE_STEP = 60000000;

	private static final int SECOND_STEP = 1000000;

	private static final int MICRO_STEP = 1;

	/**
	 * the start time - used as an offset
	 */
	private HiResDate _startTime;

	// only let slider work in micros if there is under a second of data
	private final int TIME_SPAN_TO_USE_MICROS = 1000000;

	/**
	 * how we convert a screen drag pixel to a time jump
	 *
	 */
	long _timeScalar = 1;

	public HiResDate fromSliderUnits(final int value, final long sliderResolution) {
		long newValue = value;

		// convert the resolution to micros
		final long sliderResolutionMicros = sliderResolution * 1000;

		// convert reading to real time units
		newValue *= _timeScalar;

		// re-apply the offset
		long newDate = _startTime.getMicros() + newValue;

		// and trim the resulting value
		newDate = (newDate / sliderResolutionMicros) * sliderResolutionMicros;

		return new HiResDate(0, newDate);
	}

	public void resetRange(final HiResDate min, final HiResDate max) {
		if ((min != null) && (max != null)) {
			// ok - store the start time
			_startTime = min;

			// yes - initialise the ranges
			final long range = max.getMicros() - min.getMicros();

			int maxVal = 100;

			if (range > 0) {
				// double-check the min value
				setMinVal(0);

				if (range < TIME_SPAN_TO_USE_MICROS) {
					maxVal = (int) range;
					setMaxVal(maxVal);
					setEnabled(true);
					_timeScalar = MICRO_STEP;
				} else {
					final long rangeMillis = range / 1000;
					final long rangeSecs = rangeMillis / 1000;
					if (rangeSecs < Integer.MAX_VALUE) {
						// ok, we're going to run in second resolution
						maxVal = (int) rangeSecs;
						setMaxVal(maxVal);
						_timeScalar = SECOND_STEP;
						setEnabled(true);
					} else {
						// let's run in minute resolution
						final long rangeMins = rangeSecs / 60;
						if (rangeMins < Integer.MAX_VALUE) {
							// ok, we're going to run in minute resolution
							maxVal = (int) rangeMins;
							setMaxVal(maxVal);
							_timeScalar = MINUTE_STEP;
							setEnabled(true);
						} else {
							// hey, we must be running in units which are too large.
							setEnabled(false);
						}
					}
				}

				// ok. just sort out the step size when the user clicks on the slider
				int smallTick = 5;
				int largeTick = 5;
				final int dragSize = 0;

				if (_timeScalar == MICRO_STEP) {
					smallTick = 1;
					largeTick = 1000;
				} else if (_timeScalar == SECOND_STEP) {
					smallTick = 1; // one second
					largeTick = 60; // one minute
				} else if (_timeScalar == MINUTE_STEP) {
					smallTick = 1; // one minute
					largeTick = 60; // one hour
				} else
					throw new RuntimeException("Failed to determine correct step size");

				// hmm, try to trim down the size of the large ticks, we don't want too
				// many do we?
				while (largeTick < (maxVal / 50)) {
					largeTick *= 10;
				}

				setTickSize(smallTick, largeTick, dragSize);

				// ok, we've finished updating the form. back to normal processing
				// _updatingForm = false;
			}
		}
	}

	public abstract void setEnabled(boolean val);

	public abstract void setMaxVal(int max);

	public abstract void setMinVal(int min);

	public abstract void setTickSize(int small, int large, int drag);

	public int toSliderUnits(final HiResDate now) {
		int res = -1;

		// do we know our start time?
		if (_startTime != null) {
			long offset = now.getMicros() - _startTime.getMicros();

			offset /= _timeScalar;

			res = (int) offset;
		}

		return res;

	}
}