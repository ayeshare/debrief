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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;

public class SpeedDepthDemandedValueArrow extends CompositeDecoration {
	public static final double EXPECTED_LENGTH = 36;
	private static final Point CENTER = new Point(36, 0);
	private static final int RADIUS = 1;
	public static final Color LIGHT_GREEN = new Color(null, 191, 255, 180);
	public static final Color DARK_GREEN = new Color(null, 92, 255, 64);

	private static PolygonDecoration createCircle() {
		return new CircleDecoration(CENTER, RADIUS, DARK_GREEN, LIGHT_GREEN);
	}

	private static PolygonDecoration createLine() {
		return createLine(CENTER.x, LIGHT_GREEN, DARK_GREEN);
	}

	private static PolygonDecoration createLine(final int distance, final Color far, final Color center) {
		final GradientDecoration result = new GradientDecoration();
		final PointList template = new PointList();
		template.addPoint(distance, 0);
		template.addPoint(0, 0);
		template.addPoint(distance, 0);

		result.setTemplate(template);
		result.setGradient(0, far, 1, center);

		return result;
	}

	public SpeedDepthDemandedValueArrow() {
		super(createLine(), createCircle());
	}

}
