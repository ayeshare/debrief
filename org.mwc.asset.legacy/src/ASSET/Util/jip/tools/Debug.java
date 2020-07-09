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

package ASSET.Util.jip.tools;

public final class Debug {
	private static boolean debugOn;

	static {
		// from System property
		// e.g -Djip.debug=true
		debugOn = false;// Boolean.getBoolean("jip.debug");
	}

	public synchronized static void message(final Object obj) {
		if (debugOn) {
			final StringBuffer buf = new StringBuffer();
			buf.append("debug: ");
			buf.append(obj);
			System.out.println(buf.toString());
		}
	}

}