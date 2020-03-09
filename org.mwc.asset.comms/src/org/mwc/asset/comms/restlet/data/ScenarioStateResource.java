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

package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Put;

/**
 * The resource associated to a contact.
 */
public interface ScenarioStateResource {

	public final static String START = "Start";
	public final static String STOP = "Stop";
	public final static String FASTER = "Faster";
	public final static String SLOWER = "Slower";

	@Put
	public void store(String newState);
}
