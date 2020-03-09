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

package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.geometry.Dimension;

public class FullCircleAngleMapper implements AngleMapper {
	private final double myZeroAngle;

	private final double myFull;

	public FullCircleAngleMapper(final double zeroAngle, final double full) {
		myZeroAngle = zeroAngle;
		myFull = full;
	}

	@Override
	public double computeAngle(final double value) {
		return value / myFull * 2 * Math.PI + myZeroAngle;
	}

	@Override
	public void setAnglesRange(final Dimension minDirection, final Dimension maxDirection) {
		throw new UnsupportedOperationException(
				"I am mapping the whole circle. These directions do not make sense for me");

	}
}
