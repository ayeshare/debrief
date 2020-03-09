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

public class DirectionArrow extends CompositeDecoration {
	private static final Color LIGHT = new Color(null, 255, 50, 50);
	private static final Color DARK = new Color(null, 200, 0, 0);
	private static final Color SHADOW = new Color(null, 100, 100, 100);

	private static PolygonDecoration createBig() {
		final PointList template = new PointList();
		template.addPoint(-10, -1); // #0
		template.addPoint(-49, -2); // #1
		template.addPoint(-51, -4); // #2
		template.addPoint(-55, -5); // #3 this is gradient end
		template.addPoint(-50, -2);
		template.addPoint(-50, 2);
		template.addPoint(-55, 5);
		template.addPoint(-51, 4);
		template.addPoint(-49, 2);
		template.addPoint(-10, 1);
		template.translate(30, 0);

		final GradientDecoration big = new GradientDecoration();
		big.setTemplate(template);
		big.setGradient(0, DARK, 3, LIGHT);
		return big;
	}

	private static PolygonDecoration createSmall() {
		final PointList template = new PointList();
		template.addPoint(0, 0);
		template.addPoint(-11, -5);
		template.addPoint(-11, 5);
		template.addPoint(0, 0);
		template.translate(30, 0);

		final GradientDecoration small = new GradientDecoration();
		small.setTemplate(template);
		small.setGradient(new Point(-6, -2), LIGHT, new Point(-11, 5), DARK);

		return small;
	}

	public DirectionArrow() {
		super(SHADOW, createSmall(), createBig());
	}

}
