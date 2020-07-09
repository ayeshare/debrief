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
package org.mwc.debrief.track_shift.ambiguity.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
	public static final String MIN_ZIG = "CutOff";

	/**
	 * whether to output slicing diagnostics
	 *
	 */
	public static final String DIAGNOSTICS = "OutputDiagnostics";

	/**
	 * whether to display slicing UI controls
	 *
	 */
	public static final String DISPLAY = "ShowControls";

	/**
	 * minimum rate at which both bearings travel in SAME DIRECTION (workaround for
	 * problem in up-stream processing system)
	 *
	 */
	public static final String MIN_TURN_RATE = "MinTurnRate";

	/**
	 * minimum length of period of cuts to treat them as steady
	 *
	 */
	public static final String MIN_LEG_LENGTH = "MinLegLength";

	/**
	 * minimum course change before we treat it as a zig
	 *
	 */
	public static final String OS_TURN_MIN_COURSE_CHANGE = "OSTurnMinCourseChange";

	/**
	 * minimum time interval before we treat it as missing cuts (since the cuts in
	 * the turn have been deleted).
	 */
	public static final String OS_TURN_MIN_TIME_INTERVAL = "OSTurnMintimeInterval";

	/**
	 * maximum number of legs we let the algorithm process at once
	 *
	 */
	public static final String OS_TURN_MAX_LEGS = "OSTurnMaxLegs";

}
