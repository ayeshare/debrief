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

package MWC.GUI.S57.features;

import java.awt.Color;
import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;

public class AreaFeature extends LineFeature {

	public AreaFeature(final String name, final Double minScale, final Color defaultColor) {
		super(name, minScale, defaultColor);
	}

	@Override
	public void doPaint(final CanvasType dest) {
		dest.setColor(getColor());
		for (final Iterator<Vector<WorldLocation>> iterator = _lines.iterator(); iterator.hasNext();) {
			final Vector<WorldLocation> thisLine = iterator.next();

			final int npts = thisLine.size();
			final int[] xpts = new int[npts];
			final int[] ypts = new int[npts];

			int ctr = 0;

			for (final Iterator<WorldLocation> iter = thisLine.iterator(); iter.hasNext();) {
				final WorldLocation loc = iter.next();

				final Point screen = dest.toScreen(loc);

				// handle unable to gen screen coords (if off visible area)
				if (screen == null)
					return;

				final Point pt = new Point(screen);
				xpts[ctr] = pt.x;
				ypts[ctr] = pt.y;
				ctr++;
			}
			// and plot it.
			dest.fillPolygon(xpts, ypts, npts);
		}
	}
}
