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

package ASSET.Util.XML.Control.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.DetectionObserver;
import ASSET.Scenario.Observers.ProportionDetectedObserver;

abstract class ProportionDetectedObserverHandler extends DetectionObserverHandler {
	/**
	 * ************************************************************ member variables
	 * *************************************************************
	 */
	private final static String _myNewType = "ProportionDetectedObserver";

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */
	static public String getType() {
		return _myNewType;
	}

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public ProportionDetectedObserverHandler() {
		super(_myNewType);
	}

	@Override
	protected DetectionObserver getObserver(final TargetType watch, final TargetType target, final String name,
			final Integer detectionLevel, final boolean isActive) {
		return new ProportionDetectedObserver(watch, target, name, detectionLevel, isActive);
	}

}
