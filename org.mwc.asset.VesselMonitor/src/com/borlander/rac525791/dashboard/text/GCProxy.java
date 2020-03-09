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

package com.borlander.rac525791.dashboard.text;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class GCProxy {
	private GC myGC;

	public void dispose() {
		if (myGC != null) {
			myGC.dispose();
			myGC = null;
		}
	}

	public int getAverageCharWidth(final Font font) {
		return getGC(font).getFontMetrics().getAverageCharWidth();
	}

	public Point getExtent(final String text, final Font font) {
		return getGC(font).stringExtent(text);
	}

	private GC getGC(final Font font) {
		if (myGC == null || myGC.isDisposed()) {
			myGC = new GC(new Shell());
			// System.out.println("creating new GC");
		}
		myGC.setFont(font);
		return myGC;
	}

}