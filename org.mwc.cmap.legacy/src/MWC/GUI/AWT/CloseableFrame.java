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

package MWC.GUI.AWT;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowEvent;

public class CloseableFrame extends Frame {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CloseableFrame(final String theStr) {
		super(theStr);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.setBackground(Color.lightGray);
	}

	@Override
	protected void processWindowEvent(final WindowEvent p1) {
		super.processWindowEvent(p1);
		if (p1.getID() == WindowEvent.WINDOW_CLOSING) {
			// close the frame
			this.dispose();
		}
	}
}
