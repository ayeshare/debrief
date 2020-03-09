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
package org.mwc.debrief.track_shift.zig_detector.moving_average;

/**
 * Created by deft on 03/03/2015.
 */
public class CenteredMovingAverage {

	private final int period;

	public CenteredMovingAverage(final int period) {
		assert period > 0 : "Period must be a positive integer";
		this.period = period;
	}

	public double average(final int n, final double[] data) {
		assert n > 0 : "N must be a positive integer.";
		assert data.length > 0 : "Data array must not be empty.";
		assert n < data.length : "N should be less than the data array length " + data.length;

		int nIdx = n;

		// Get N index in data array.
		if (nIdx > data.length) {
			nIdx = data.length - period;
		}

		double sum = data[nIdx];

		int lastBackwardIndex = lastBackwardIndex(period, n);
		if (lastBackwardIndex < 0) {
			lastBackwardIndex = 0;
		}

		int lastForwardIndex = lastForwardIndex(period, n);
		if (lastForwardIndex > data.length - 1) {
			lastForwardIndex = data.length - 1;
		}

		for (int idx = nIdx + 1; idx <= lastForwardIndex; idx++) {
			sum += data[idx];
		}

		for (int idx = nIdx - 1; idx >= lastBackwardIndex; idx--) {
			sum += data[idx];
		}

		return sum / (lastForwardIndex - lastBackwardIndex + 1);
	}

	public int getPeriod() {
		return period;
	}

	private int lastBackwardIndex(final int period, final int n) {
		final int distance = (period - 1) / 2;
		return n - distance;
	}

	private int lastForwardIndex(final int period, final int n) {
		final int mod = (period - 1) % 2;
		final int distance = (period - 1) / 2;
		return n + distance + mod;
	}
}
