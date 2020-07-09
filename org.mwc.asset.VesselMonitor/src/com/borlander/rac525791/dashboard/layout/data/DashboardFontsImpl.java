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

package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import com.borlander.rac525791.dashboard.layout.DashboardFonts;

public class DashboardFontsImpl implements DashboardFonts {
	private Display myDisplay;
	private Font myValueFont;
	private Font myUnitsFont;
	private Font myTextFont;

	private final int myTextSize;
	private final int myValueSize;
	private final int myUnitsSize;
	private final boolean myTextBold = true;

	public DashboardFontsImpl(final int text, final int value, final int units) {
		myTextSize = text;
		myValueSize = value;
		myUnitsSize = units;
	}

	@Override
	public void dispose() {
		if (myTextFont != null) {
			myTextFont.dispose();
			myTextFont = null;
		}
		if (myValueFont != null) {
			myValueFont.dispose();
			myValueFont = null;
		}
		if (myUnitsFont != null) {
			myUnitsFont.dispose();
			myUnitsFont = null;
		}
		myDisplay = null;
	}

	private Display getDisplay() {
		if (myDisplay == null || myDisplay.isDisposed()) {
			myDisplay = Display.getCurrent();
			myDisplay.disposeExec(new Runnable() {
				@Override
				public void run() {
					DashboardFontsImpl.this.dispose();
				}
			});
		}
		return myDisplay;
	}

	@Override
	public Font getTextFont() {
		if (myTextFont == null || myTextFont.isDisposed()) {
			myTextFont = new Font(getDisplay(), "Arial", myTextSize, myTextBold ? SWT.BOLD : SWT.NORMAL);
		}
		return myTextFont;
	}

	@Override
	public Font getUnitsFont() {
		if (myUnitsFont == null || myUnitsFont.isDisposed()) {
			myUnitsFont = new Font(getDisplay(), "Arial", myUnitsSize, SWT.NORMAL);
		}
		return myUnitsFont;
	}

	@Override
	public Font getValueFont() {
		if (myValueFont == null || myValueFont.isDisposed()) {
			myValueFont = new Font(getDisplay(), "Arial", myValueSize, SWT.BOLD);
		}
		return myValueFont;
	}

}
