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

package com.planetmayo.debrief.satc.model.manager.mock;

import java.util.Arrays;
import java.util.List;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.manager.IVehicleTypesManager;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class MockVehicleTypesManager implements IVehicleTypesManager {

	@Override
	public List<VehicleType> getAllTypes() {
		return Arrays.asList(
				new VehicleType("UK Ferry", GeoSupport.kts2MSec(2), GeoSupport.kts2MSec(30), Math.toRadians(0),
						Math.toRadians(2), 0.2, 0.4, 0.2, 0.4),
				new VehicleType("Medium Tanker", GeoSupport.kts2MSec(1), GeoSupport.kts2MSec(12), Math.toRadians(0),
						Math.toRadians(1), 0.1, 0.4, 0.2, 0.3),
				new VehicleType("Large Tanker", GeoSupport.kts2MSec(1), GeoSupport.kts2MSec(15), Math.toRadians(0),
						Math.toRadians(0.5), 0.1, 0.3, 0.2, 0.3)

		);
	}
}
