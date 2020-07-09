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

package org.mwc.debrief.core.gt2plot;

import MWC.Algorithms.EarthModel;
import interfaces.IEarthModelProvider;

/**
 * Provides GeoTools Calculator adapter as Earth model; Is used in the Extension
 * of EarthModelProvider Extension Point.
 */
public class GeoCalculatorProvider implements IEarthModelProvider {

	@Override
	public EarthModel getEarthModel() {
		return new GeoCalculatorAdapter();
	}

}
