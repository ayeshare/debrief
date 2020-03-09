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

package org.mwc.cmap.grideditor.interpolation;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;

class LinearItemsInterpolator extends AbstractItemsInterpolator {

	private static LinearInterpolator createWorker(final TimeStampedDataItem startPoint,
			final TimeStampedDataItem endPoint, final GriddableItemDescriptor descriptor) {
		final double startMillis = extractBaseValue(startPoint);
		final double endMillis = extractBaseValue(endPoint);
		if (startMillis == endMillis) {
			return null;
		}

		final double startValue = getDoubleValue(startPoint, descriptor);
		final double endValue = getDoubleValue(endPoint, descriptor);

		return new LinearInterpolator(endValue, startValue, endMillis, startMillis);
	}

	private final LinearInterpolator myWorker;

	/**
	 * Creates linear interpolator for given bean property and start and end points.
	 *
	 * @throws IllegalArgumentException if descriptor is not interpolate-able,
	 * @see LinearItemsInterpolator#canInterpolate(GriddableItemDescriptor)
	 */
	public LinearItemsInterpolator(final TimeStampedDataItem startPoint, final TimeStampedDataItem endPoint,
			final GriddableItemDescriptor descriptor) {
		super(descriptor);
		myWorker = createWorker(startPoint, endPoint, descriptor);
	}

	/**
	 * @return <code>false</code> if and only if the start and end points had the
	 *         same time-stamp
	 */
	@Override
	public boolean canInterpolate(final TimeStampedDataItem item) {
		return myWorker != null;
	}

	@Override
	public Object getInterpolatedValue(final TimeStampedDataItem item) {
		if (!canInterpolate(item)) {
			throw new IllegalStateException("I told you I can't interpolate item: " + item);
		}

		final double millis = extractBaseValue(item);
		final double interpolatedResult = Double.valueOf(myWorker.interp(millis));
		return getSafeInterpolatedValue(item, getDescriptor(), interpolatedResult);
	}

}
