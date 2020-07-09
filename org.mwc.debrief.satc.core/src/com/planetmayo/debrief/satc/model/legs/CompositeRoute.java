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

package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collection;

/**
 * a series of straight/altering legs
 *
 * @author Ian
 *
 */
public class CompositeRoute {
	private final ArrayList<CoreRoute> _legs;

	public CompositeRoute() {
		_legs = new ArrayList<CoreRoute>();
	}

	public CompositeRoute(final Collection<CoreRoute> legs) {
		_legs = new ArrayList<CoreRoute>();
		for (final CoreRoute route : legs) {
			if (route != null) {
				_legs.add(route);
			}
		}
	}

	/**
	 * add this leg to our data
	 *
	 * @param topR
	 */
	public void add(final CoreRoute topR) {
		_legs.add(topR);
	}

	/**
	 * get the legs that comprise this route
	 *
	 */
	public Collection<CoreRoute> getLegs() {
		return _legs;
	}

	public double getScore() {
		double sum = 0;
		for (final CoreRoute route : _legs) {
			sum += route.getScore();
		}
		return sum;
	}
}
