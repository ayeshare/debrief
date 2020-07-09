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

package com.planetmayo.debrief.satc.log;

import org.eclipse.core.runtime.IStatus;

import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class RCPLogService implements LogService {

	@Override
	public void error(final String message) {
		SATC_Activator.log(IStatus.ERROR, message, null);
	}

	@Override
	public void error(final String message, final Exception ex) {
		SATC_Activator.log(IStatus.ERROR, message, ex);
	}

	@Override
	public void info(final String message) {
		SATC_Activator.log(IStatus.INFO, message, null);
	}

	@Override
	public void info(final String message, final Exception ex) {
		SATC_Activator.log(IStatus.INFO, message, ex);
	}

	@Override
	public void warn(final String message) {
		SATC_Activator.log(IStatus.WARNING, message, null);
	}

	@Override
	public void warn(final String message, final Exception ex) {
		SATC_Activator.log(IStatus.WARNING, message, ex);
	}
}
