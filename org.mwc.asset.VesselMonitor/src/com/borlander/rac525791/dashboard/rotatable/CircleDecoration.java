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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

public class CircleDecoration extends GradientDecoration {
	private final Point TEMP = new Point();
	private final Point myCenter;
	private final int myRadius;

	public CircleDecoration(final Point center, final int radius, final Color far, final Color near) {
		final Point farPoint = center.getCopy().getTranslated(0, radius);
		final Point nearPoint = center.getCopy().getTranslated(0, -radius);
		setGradient(farPoint, far, nearPoint, near);
		myRadius = radius;
		myCenter = center.getCopy();

		final PointList fakePointsForBoundsOnly = new PointList();
		fakePointsForBoundsOnly.addPoint(center.x + radius + 1, center.y);
		fakePointsForBoundsOnly.addPoint(center.x, center.y + radius + 1);
		fakePointsForBoundsOnly.addPoint(center.x - radius - 1, center.y);
		fakePointsForBoundsOnly.addPoint(center.x, center.y - radius - 1);
		setTemplate(fakePointsForBoundsOnly);
	}

	@Override
	protected void fillShape(final Graphics g) {
		fillShadow(g);

		final Point center = findCenter();
		final Pattern pattern = getPattern(getBounds());
		g.pushState();
		g.setBackgroundPattern(pattern);
		g.fillOval(center.x - myRadius - 1, center.y - myRadius - 1, 2 * myRadius + 2, 2 * myRadius + 2);
		g.popState();
	}

	private Point findCenter() {
		return transformPoint(TEMP.setLocation(myCenter));
	}

	@Override
	protected void outlineShape(final Graphics g) {
		final Point center = findCenter();
		g.pushState();
		g.setForegroundPattern(getPattern(getBounds()));
		g.drawOval(center.x - myRadius, center.y - myRadius, 2 * myRadius, 2 * myRadius);
		g.popState();
	}

}