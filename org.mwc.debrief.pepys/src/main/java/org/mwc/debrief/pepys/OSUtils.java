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

package org.mwc.debrief.pepys;

public class OSUtils {

	public static final boolean WIN;
	public static final boolean MAC;
	public static final boolean LINUX;
	public static final boolean IS_64BIT;
	static {
		final String os = System.getProperty("os.name").toLowerCase();
		boolean win = false, mac = false, linux = false;
		if (os.indexOf("win") != -1) {
			win = true;
		}
		if (os.indexOf("mac os") != -1) {
			mac = true;
		}
		if (os.indexOf("linux") != -1) {
			linux = true;
		}
		WIN = win;
		MAC = mac;
		LINUX = linux;
		final String jvmArch = System.getProperty("os.arch");
		IS_64BIT = jvmArch != null && jvmArch.contains("64");
	}
}
