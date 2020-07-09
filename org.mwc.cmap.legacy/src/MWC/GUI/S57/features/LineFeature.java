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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;

public class LineFeature extends S57Feature {
	public class LineFeatureInfo extends Editable.EditorType {

		public LineFeatureInfo(final LineFeature data, final String theName) {
			super(data, theName, "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Color", "the color to plot this feature", SPATIAL) };
				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	final public static Double DEFAULT_SCALE = 1000000d;
	private Color _myColor;

	protected Vector<Vector<WorldLocation>> _lines = new Vector<Vector<WorldLocation>>(0, 1);

	public LineFeature(final String name, final Double minScale, final Color defaultColor) {
		super(name, minScale);
		_myColor = defaultColor;
	}

	/**
	 * add a new line
	 *
	 * @param pts
	 */
	public void addLine(final Vector<WorldLocation> pts) {
		_lines.add(pts);
	}

	@Override
	final EditorType createEditor() {
		return new LineFeatureInfo(this, getName());
	}

	@Override
	public void doPaint(final CanvasType dest) {
		dest.setColor(_myColor);
		for (final Iterator<Vector<WorldLocation>> iterator = _lines.iterator(); iterator.hasNext();) {
			final Vector<WorldLocation> thisLine = iterator.next();
			Point last = null;
			Point startPt = null;
			for (final Iterator<WorldLocation> iter = thisLine.iterator(); iter.hasNext();) {
				final WorldLocation loc = iter.next();

				// if(ctr > 46)
				// {
				// System.err.println("loc " + ctr + " is:" + loc + " x:" +
				// loc.getLong());
				// }

				final Point screen = dest.toScreen(loc);

				// handle unable to gen screen coords (if off visible area)
				if (screen == null)
					return;

				final Point pt = new Point(screen);
				if (startPt == null)
					startPt = new Point(pt);
				if (last != null) {
					if (last.equals(pt)) {
					}
					dest.drawLine(last.x, last.y, pt.x, pt.y);
					// dest.drawText(myFont, "" + ctr, last.x + xOffset, last.y +
					// yOffset);
				}
				last = pt;
			}
			// and close it.
			if ((last != null) && (startPt != null))
				dest.drawLine(last.x, last.y, startPt.x, startPt.y);
			// dest.setColor(Color.red);
			// yOffset += 10;
			// xOffset += 5;
			// break;
		}
	}

	/**
	 * @return the _myColor
	 */
	public final Color getColor() {
		return _myColor;
	}

	/**
	 * @param color the _myColor to set
	 */
	public final void setColor(final Color color) {
		_myColor = color;
	}
}
