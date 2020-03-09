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

package org.mwc.cmap.plotViewer.actions;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.editors.chart.CoreTracker;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.Algorithms.Conversions;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * @author ian.mayo
 */
final public class RangeBearing extends CoreDragAction {

	/**
	 * embedded class that handles the range/bearing measurement
	 *
	 * @author Ian
	 */
	final public static class RangeBearingMode extends SWTChart.PlotMouseDragger {

		private final PaintListener paintListener = new PaintListener() {
			@Override
			public void paintControl(final PaintEvent ev) {
				if (_lastRect != null)
					try {
						// update the range/bearing text
						plotUpdate(ev.gc);
					} catch (final Exception e) {
						e.printStackTrace();
					}
			}
		};

		/**
		 * the start point, in world coordinates (so we don't have to calculate it as
		 * often)
		 */
		WorldLocation _startLocation;

		/**
		 * the start point, in screen coordinates - where we started our drag
		 */
		Point _startPoint;

		/**
		 * the last rectangle drawn, so we can erase it on the next update
		 */
		Rectangle _lastRect;

		/**
		 * the canvas we're updating..
		 */
		SWTCanvas _myCanvas;

		@Override
		final public void doMouseDrag(final Point pt, final int JITTER, final Layers theLayers,
				final SWTCanvas theCanvas) {
			if (_startPoint != null) {

				final int dx = pt.x - _startPoint.x;
				final int dy = pt.y - _startPoint.y;

				// Draw selection rectangle
				_lastRect = new Rectangle(_startPoint.x, _startPoint.y, dx, dy);

				if (_myCanvas != null)
					_myCanvas.getCanvas().redraw();
				_myCanvas.getCanvas().update();

			} else {
				// System.out.println("no point.");
			}

		}

		@Override
		final public void doMouseUp(final Point point, final int keyState) {

			_myCanvas.getCanvas().removePaintListener(paintListener);
			_myCanvas.getCanvas().redraw();
			_myCanvas.getCanvas().update();
			_startPoint = null;
			_lastRect = null;
			_myCanvas = null;
			_startLocation = null;

		}

		@Override
		final public void mouseDown(final Point point, final SWTCanvas canvas, final PlainChart theChart) {
			_startPoint = point;
			_myCanvas = canvas;
			_startLocation = new WorldLocation(_myCanvas.getProjection().toWorld(new java.awt.Point(point.x, point.y)));
			_myCanvas.getCanvas().addPaintListener(paintListener);
		}

		@SuppressWarnings("deprecation")
		final private void plotUpdate(final GC dest) {

			final java.awt.Point endPoint = new java.awt.Point(_lastRect.x + _lastRect.width,
					_lastRect.y + _lastRect.height);

			final Color oldForeground = dest.getForeground();
			final Color f = new Color(Display.getDefault(), 255, 255, 255);
			dest.setXORMode(true);
			dest.setForeground(f);
			dest.setLineWidth(2);
			dest.drawLine(_lastRect.x, _lastRect.y, _lastRect.x + _lastRect.width, _lastRect.y + _lastRect.height);

			// also put in a text-label
			final WorldLocation endLocation = _myCanvas.getProjection().toWorld(endPoint);
			final WorldVector sep = endLocation.subtract(_startLocation);

			final String myUnits = CorePlugin.getToolParent()
					.getProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);

			final double rng = Conversions.convertRange(sep.getRange(), myUnits);
			double brg = sep.getBearing();
			brg = brg * 180 / Math.PI;
			if (brg < 0)
				brg += 360;
			final DecimalFormat df = new DecimalFormat("0.00");
			final String numComponent = df.format(rng);
			final String txt = "[" + numComponent + myUnits + " " + (int) brg + "\u00b0" + "]";

			// decide the mid-point
			final java.awt.Point loc = new java.awt.Point(_lastRect.x + _lastRect.width / 2,
					_lastRect.y + _lastRect.height / 2);

			// find out how big the text is
			final FontMetrics fm = dest.getFontMetrics();

			loc.translate(0, fm.getHeight() / 2);
			loc.translate(-txt.length() / 2 * fm.getAverageCharWidth(), 0);

			// ok, do the write operation

			// avoid XOR mode - see https://github.com/debrief/debrief/issues/1225
			dest.setXORMode(false);
			final Color ob = dest.getBackground();
			final Color of = dest.getForeground();
			final Color nb = new Color(Display.getCurrent(), DebriefColors.BLACK.getRed(),
					DebriefColors.BLACK.getGreen(), DebriefColors.BLACK.getBlue());

			final Color nf = new Color(Display.getCurrent(), DebriefColors.WHITE.getRed(),
					DebriefColors.WHITE.getGreen(), DebriefColors.WHITE.getBlue());

			dest.setBackground(nb);
			dest.setForeground(nf);
			dest.drawText(txt, loc.x, loc.y, SWT.NONE);
			dest.setBackground(ob);
			dest.setForeground(of);
			dest.setXORMode(true);
			nb.dispose();
			nf.dispose();

			// also get the RangeTracker to display the range/bearing
			CoreTracker.write(txt);
			// revert old foregoround color
			dest.setForeground(oldForeground);
			// dispose created colors
			f.dispose();
			dest.setXORMode(false);
		}
	}

	@Override
	public PlotMouseDragger getDragMode() {
		return new RangeBearingMode();
	}

}