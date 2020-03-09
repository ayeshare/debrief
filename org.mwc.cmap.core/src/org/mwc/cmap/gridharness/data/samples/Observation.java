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

package org.mwc.cmap.gridharness.data.samples;

import java.awt.Color;

import org.mwc.cmap.gridharness.data.WorldDistance2;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;

/**
 * @author Administrator
 *
 */
public class Observation implements TimeStampedDataItem {

	private HiResDate _myTime;

	private double _bearing;

	private WorldDistance2 _range;

	private Color _color;

	public Observation() {
	}

	public Observation(final HiResDate dtg, final double bearing, final WorldDistance2 range, final Color color) {
		_myTime = dtg;
		_bearing = bearing;
		_range = range;
		_color = color;
	}

	public double getBearing() {
		return _bearing;
	}

	@Override
	public Color getColor() {
		return _color;
	}

	@Override
	public HiResDate getDTG() {
		return _myTime;
	}

	public WorldDistance2 getRange() {
		return _range;
	}

	public HiResDate getTime() {
		return _myTime;
	}

	public void setBearing(final double _bearing) {
		this._bearing = _bearing;
	}

	public void setColor(final Color color) {
		_color = color;
	}

	@Override
	public void setDTG(final HiResDate newTime) {
		this._myTime = newTime;
	}

	public void setRange(final WorldDistance2 _range) {
		this._range = _range;
	}

	public void setTime(final HiResDate time) {
		_myTime = time;
	}

	@Override
	public String toString() {
		return "Observation:" + _myTime.toString();
	}

}
