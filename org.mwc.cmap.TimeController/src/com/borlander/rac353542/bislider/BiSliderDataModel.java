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
 * Main read-only interface for access the current state of BiSlider. The user
 * is not intended to implement this interface directly, but use one of default
 * implementation instead.
 */
public interface BiSliderDataModel {

	public static interface Listener {

		public void dataModelChanged(BiSliderDataModel dataModel, boolean moreChangesExpectedInNearFuture);
	}

	/**
	 * Extends the read-only <code>BiSliderDataModel</code> interface with write
	 * operations. You may need this interface only if you want to provide custom
	 * inplementation of data model.
	 */
	public static interface Writable extends BiSliderDataModel {

		public void finishCompositeUpdate();

		public void setSegmentCount(int segmentsCount);

		public void setSegmentLength(double segmentLength);

		public void setUserMaximum(double userMaximum);

		public void setUserMinimum(double userMinimum);

		/**
		 * Atomically changes both minimum and maximum user values. It is different to
		 * set values separately due to different validation strategy. In particlular,
		 * in case if currentMin &lt; currentMax &lt; newMin &lt; newMax, then separate
		 * setting of the <code>setUserMinimum(newMin); setUserMaximum(newMax)</code>
		 * will select the range of [currentMinimum, newMaximum].
		 */
		public void setUserRange(double userMin, double userMax);

		public void startCompositeUpdate();
	}

	/**
	 * Register given listener to be notified on changes in the ui model.
	 */
	public void addListener(Listener listener);

	/**
	 * Specifies precision of this data model.
	 * <p>
	 * NOTE: In contrast to other model parameters, this one specifies the metadata
	 * for the whole set of datas that may be represented by this model. Thus, the
	 * return value for this method can not be changed during the whole lifecycle of
	 * model.
	 *
	 * @return the precision of this data model, that is, the minimum delta between
	 *         2 values that should be considered as different.
	 *
	 */
	public double getPrecision();

	/**
	 * @return the length of single segment
	 */
	public double getSegmentLength();

	/**
	 * Convenience method. Fully equivalent to the
	 * <code>getTotalMaximum() - getTotalMinimum()</code>
	 */
	public double getTotalDelta();

	/**
	 * @return the maximum value which may be selected using this slider. The label
	 *         for this value is placed at the right side of horizontal BISlider and
	 *         at the top of vertical one.
	 */
	public double getTotalMaximum();

	/**
	 * @return the least value which may be selected using this slider. The label
	 *         for this value is placed at the left side of horizontal BISlider and
	 *         at the bottom of vertical one.
	 */
	public double getTotalMinimum();

	/**
	 * Convenience method. Fully equivalent to the
	 * <code>getUserMaximum() - getUserMinimum()</code>
	 */
	public double getUserDelta();

	/**
	 * @return the end of the range currently selected by user.
	 */
	public double getUserMaximum();

	/**
	 * @return the start of the range currently selected by user.
	 */
	public double getUserMinimum();

	/**
	 * Unregister given listener from change notifications. It is safe to
	 * <b>call</b> this method during change notification.
	 */
	public void removeListener(Listener listener);
}
