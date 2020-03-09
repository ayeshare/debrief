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

package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

abstract class DirectionBase implements ControlUIModel {

	@Override
	public final Dimension getMaximumMark() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	@Override
	public final Dimension getUnitsAndMultipliersSize() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	@Override
	public final Point getUnitsPosition() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	@Override
	public final Point getValueTextPosition() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	@Override
	public final Dimension getValueTextSize() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	@Override
	public final Dimension getZeroMark() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	@Override
	public final boolean isFullCircleMapped() {
		return true;
	}

}
