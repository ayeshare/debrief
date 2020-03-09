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

package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

public class BoundsGradientDecoration extends AbstractGradientDecoration {
	private final Point LOCATION = new Point();
	private final Point REFERENCE = new Point();
	private Color myLocationColor;
	private Color myReferenceColor;
	private boolean myFromTopRightToBottomLeft;

	public BoundsGradientDecoration() {
		setReferencePointColor(ColorConstants.blue);
		setLocationColor(ColorConstants.red);

		setForegroundColor(ColorConstants.black);
		setBackgroundColor(ColorConstants.black);
	}

	@Override
	protected Pattern createPattern() {
		final Quadrant quadrant = (myFromTopRightToBottomLeft) ? getQuadrant().flip() : getQuadrant();
		final Rectangle localBounds = getBounds();
		quadrant.point(localBounds, LOCATION);
		quadrant.oppositePoint(localBounds, REFERENCE);
		return new Pattern(null, LOCATION.x, LOCATION.y, REFERENCE.x, REFERENCE.y, myLocationColor, myReferenceColor);
	}

	public void setFromTopRightToBottomLeft(final boolean value) {
		myFromTopRightToBottomLeft = value;
	}

	public void setLocationColor(final Color locationColor) {
		myLocationColor = locationColor;
	}

	public void setReferencePointColor(final Color referenceColor) {
		myReferenceColor = referenceColor;
	}

}
