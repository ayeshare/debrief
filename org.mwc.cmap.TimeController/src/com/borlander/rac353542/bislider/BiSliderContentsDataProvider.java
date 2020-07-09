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

package com.borlander.rac353542.bislider;

/**
 * Instances of this interface are responsible for rendering of the BiSlider's
 * contents (which may be optionally rendered as a colored segmented outlines).
 *
 */
public interface BiSliderContentsDataProvider {

	/**
	 * Each segment will be drawn according to the value of some function in
	 * segment's center
	 */
	public static abstract class Adapter implements BiSliderContentsDataProvider {

		@Override
		public double getNormalValueAt(final double totalMin, final double totalMax, final double segmentMin,
				final double segmentMax) {
			return getNormalValueAtPoint(totalMin, totalMax, (segmentMax + segmentMin) / 2);
		}

		public abstract double getNormalValueAtPoint(double totalMin, double totalMax, double point);
	}

	/**
	 * Shared instance which may be used to fill all bislider segments.
	 */
	public static final BiSliderContentsDataProvider FILL = new BiSliderContentsDataProvider() {

		@Override
		public double getNormalValueAt(final double totalMin, final double totalMax, final double segmentMin,
				final double segmentMax) {
			return 1.0;
		}
	};
	/**
	 * Shared instance which may be used to avoid colorisation of bislider segments.
	 */
	public static final BiSliderContentsDataProvider LEAVE_BLANK = new BiSliderContentsDataProvider() {

		@Override
		public double getNormalValueAt(final double totalMin, final double totalMax, final double segmentMin,
				final double segmentMax) {
			return 1.0;
		}
	};
	/**
	 * Shared instance which may be used to draw segments distributed normally.
	 * Implemented primarily for demonstration purpose.
	 */
	public static final BiSliderContentsDataProvider NORMAL_DISTRIBUTION = new BiSliderContentsDataProvider() {

		@Override
		public double getNormalValueAt(final double totalMin, final double totalMax, final double segmentMin,
				final double segmentMax) {
			final double arg = (segmentMin + segmentMax) / 2;
			final double mean = (totalMin + totalMax) / 2;
			final double delta = arg - mean;
			final double totalDelta = (totalMax - totalMin) / 2;
			final double divisor = totalDelta * totalDelta / (2 * 2) / Math.log(2.0);
			return Math.exp(-delta * delta / divisor);
		}
	};

	/**
	 * @return the double value between 0.0 to 1.0. If 0.0, segment will not be
	 *         drawn, if 1.0, segment will be completely filled by appropriate
	 *         color.
	 */
	public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax);
}
