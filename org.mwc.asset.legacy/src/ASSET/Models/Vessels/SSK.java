
package ASSET.Models.Vessels;

import ASSET.Models.Movement.SSKMovement;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class SSK extends SSN {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the Height we snort at
	 *
	 */
	static final public double CHARGE_HEIGHT = -15;

	public SSK(final int id) {
		this(id, null, null, null);
	}

	public SSK(final int id, final ASSET.Participants.Status status, final ASSET.Participants.DemandedStatus demStatus,
			final String name) {
		super(id, status, demStatus, name);

		super.setMovementModel(new ASSET.Models.Movement.SSKMovement());
	}

	public double getChargeRate() {
		final SSKMovement theMovement = (SSKMovement) super.getMovementModel();
		return theMovement.getChargeRate();
	}

	public void setChargeRate(final double val) {
		final SSKMovement theMovement = (SSKMovement) super.getMovementModel();
		theMovement.setChargeRate(val);
	}

}