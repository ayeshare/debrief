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

package ASSET.Models.Vessels;

import ASSET.Models.Environment.EnvironmentType;

public class Torpedo extends SSN {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Torpedo(final int id) {
		super(id);
	}

	public Torpedo(final int id, final ASSET.Participants.Status status,
			final ASSET.Participants.DemandedStatus demStatus, final String name) {
		super(id, status, demStatus, name);
	}

	public void initialise() {
		_radiatedChars.add(EnvironmentType.BROADBAND_PASSIVE, new ASSET.Models.Mediums.BroadbandRadNoise(170.78));
		if (getStatus() != null)
			this.getStatus().setFuelLevel(100);
	}
}
