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

package com.borlander.rac525791.draw2d.ext;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;

public class InvisibleRectangle extends Shape {
	private final boolean myUseLocalCoordinates;

	public InvisibleRectangle() {
		this(false);
	}

	public InvisibleRectangle(final boolean useLocalCoordinates) {
		myUseLocalCoordinates = useLocalCoordinates;
	}

	@Override
	protected void fillShape(final Graphics graphics) {
		// invisible
	}

	@Override
	protected void outlineShape(final Graphics graphics) {
		// invisible
	}

	@Override
	protected boolean useLocalCoordinates() {
		return myUseLocalCoordinates;
	}
}
