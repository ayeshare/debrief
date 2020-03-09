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

package com.borlander.rac353542.bislider.impl;

import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderUIModel;
import com.borlander.rac353542.bislider.ColorInterpolation;

class Segmenter implements Disposable {
	private static class SegmentsArrayAsEnumeration implements ColoredSegmentEnumeration {
		private int myNextIndex;
		private final ColoredSegment[] myArray;
		private final int myLastIndexExclusive;

		public SegmentsArrayAsEnumeration(final ColoredSegment[] segments) {
			this(segments, 0, segments.length);
		}

		public SegmentsArrayAsEnumeration(final ColoredSegment[] segments, final int startIndexInclusive,
				final int lastIndexExclusive) {
			myNextIndex = startIndexInclusive;
			myArray = segments;
			myLastIndexExclusive = lastIndexExclusive;
		}

		@Override
		public boolean hasMoreElements() {
			return myNextIndex < myLastIndexExclusive;
		}

		@Override
		public ColoredSegment next() {
			return myArray[myNextIndex++];
		}

		@Override
		public ColoredSegment nextElement() {
			return next();
		}
	}

	private final BiSliderDataModel myDataModel;
	private final BiSliderUIModel myUiModel;
	private ColorInterpolation myInterpolation;
	private double myCachedTotalMin;
	private double myCachedTotalMax;
	private double myCachedSegmentSize;
	private ColoredSegment[] mySegments;
	private final BiSliderDataModel.Listener myDataModelListener;

	private final BiSliderUIModel.Listener myUIModelListener;

	public Segmenter(final BiSliderDataModel dataModel, final BiSliderUIModel uiModel) {
		myDataModel = dataModel;
		myUiModel = uiModel;
		cacheDataModelValues();
		updateInterpolation();
		computeSegments();
		myDataModelListener = new BiSliderDataModel.Listener() {
			@Override
			public void dataModelChanged(final BiSliderDataModel model, final boolean moreChangesExpectedInNearFuture) {
				onExternalChanges();
			}
		};
		myDataModel.addListener(myDataModelListener);

		myUIModelListener = new BiSliderUIModel.Listener() {
			@Override
			public void uiModelChanged(final BiSliderUIModel model) {
				onExternalChanges();
			}
		};
		myUiModel.addListener(myUIModelListener);
	}

	public ColoredSegmentEnumeration allSegments() {
		return new SegmentsArrayAsEnumeration(mySegments);
	}

	private void cacheDataModelValues() {
		myCachedTotalMax = myDataModel.getTotalMaximum();
		myCachedTotalMin = myDataModel.getTotalMinimum();
		myCachedSegmentSize = myDataModel.getSegmentLength();
	}

	private void computeSegments() {
		final double segmentSize = myDataModel.getSegmentLength();
		final int segmentsCount = (int) Math.ceil(myDataModel.getTotalDelta() / segmentSize);
		mySegments = new ColoredSegment[segmentsCount];
		double nextSegmentStart = myDataModel.getTotalMinimum();
		for (int i = 0; i < segmentsCount; i++, nextSegmentStart += segmentSize) {
			final ColorDescriptor descriptor = new ColorDescriptor(myInterpolation.interpolateRGB(nextSegmentStart));
			mySegments[i] = new ColoredSegment(nextSegmentStart, nextSegmentStart + segmentSize, descriptor);
		}
	}

	private void disposeSegments() {
		if (mySegments != null) {
			for (int i = 0; i < mySegments.length; i++) {
				mySegments[i].getColorDescriptor().freeResources();
			}
			mySegments = null;
		}
	}

	@Override
	public void freeResources() {
		myDataModel.removeListener(myDataModelListener);
		myUiModel.removeListener(myUIModelListener);
		disposeSegments();
	}

	public ColoredSegment getSegment(double value) {
		value = Math.max(value, myCachedTotalMin);
		value = Math.min(value, myCachedTotalMax);
		final double segmentSize = myDataModel.getSegmentLength();
		int index = (int) Math.floor((value - myCachedTotalMin) / segmentSize);
		index = Math.min(index, mySegments.length - 1);
		return mySegments[index];
	}

	private boolean isSignificantChanges() {
		return myCachedSegmentSize != myDataModel.getSegmentLength()
				|| myCachedTotalMax != myDataModel.getTotalMaximum()
				|| myCachedTotalMin != myDataModel.getTotalMinimum()
				|| !myInterpolation.isSameInterpolationMode(myUiModel.getColorInterpolation());
	}

	void onExternalChanges() {
		if (isSignificantChanges()) {
			updateInterpolation();
			disposeSegments();
			computeSegments();
		}
		cacheDataModelValues();
	}

	public ColoredSegmentEnumeration segments(double minValue, double maxValue) {
		minValue = Math.max(minValue, myCachedTotalMin);
		maxValue = Math.min(maxValue, myCachedTotalMax);

		final double segmentSize = myDataModel.getSegmentLength();
		final int startIndex = (int) Math.floor((minValue - myCachedTotalMin) / segmentSize);
		final int lastIndex = (int) Math.ceil((maxValue - myCachedTotalMin) / segmentSize);
		return new SegmentsArrayAsEnumeration(mySegments, startIndex, lastIndex);
	}

	private void updateInterpolation() {
		final ColorInterpolation interpolation = myUiModel.getColorInterpolation();
		if (myInterpolation != null && myInterpolation.isSameInterpolationMode(interpolation)) {
			return;
		}
		myInterpolation = interpolation;
		myInterpolation.setContext(myUiModel.getMinimumRGB(), myUiModel.getMaximumRGB(), myDataModel.getTotalMinimum(),
				myDataModel.getTotalMaximum());
	}

}
