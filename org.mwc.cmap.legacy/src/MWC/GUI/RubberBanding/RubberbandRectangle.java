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

package MWC.GUI.RubberBanding;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import MWC.GUI.Rubberband;

public class RubberbandRectangle extends Rubberband {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public RubberbandRectangle() {
	}

	public RubberbandRectangle(final Component component) {
		super(component);
	}

	@Override
	public void drawLast(final Graphics graphics) {
		final Rectangle rect = lastBounds();
		graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public void drawNext(final Graphics graphics) {
		final Rectangle rect = getBounds();
		graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
	}
}
