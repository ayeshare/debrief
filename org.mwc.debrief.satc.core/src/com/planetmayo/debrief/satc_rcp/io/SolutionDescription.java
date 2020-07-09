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

package com.planetmayo.debrief.satc_rcp.io;

import java.util.List;

import org.mwc.debrief.track_shift.zig_detector.Precision;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("solution")
public class SolutionDescription {
	private VehicleType vehicleType;

	private Precision precision;

	@XStreamAlias("contributions")
	private List<BaseContribution> contributions;

	@XStreamAsAttribute
	private int version;

	public List<BaseContribution> getContributions() {
		return contributions;
	}

	public Precision getPrecision() {
		return precision;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public int getVersion() {
		return version;
	}

	public void setContributions(final List<BaseContribution> contributions) {
		this.contributions = contributions;
	}

	public void setPrecision(final Precision precision) {
		this.precision = precision;
	}

	public void setVehicleType(final VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public void setVersion(final int version) {
		this.version = version;
	}
}
