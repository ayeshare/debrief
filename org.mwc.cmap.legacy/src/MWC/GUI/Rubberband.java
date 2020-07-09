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

// $RCSfile: Rubberband.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: Rubberband.java,v $
// Revision 1.2  2004/05/25 15:45:49  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:04  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:34+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:31+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:37+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:09+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:00  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:37:10+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:43:07+01  administrator
// make tools serializable
//
// Revision 1.1  1999-07-27 10:50:51+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-23 14:03:52+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//

package MWC.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

abstract public class Rubberband implements MouseListener, MouseMotionListener, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected Point anchorPt = new Point(0, 0);
	protected Point stretchedPt = new Point(0, 0);
	protected Point lastPt = new Point(0, 0);
	protected Point endPt = new Point(0, 0);

	private Component component;
	private boolean firstStretch = true;
	private boolean active = false;

	public Rubberband() {
	}

	public Rubberband(final Component c) {
		setComponent(c);
	}

	public void anchor(final Point p) {
		firstStretch = true;
		anchorPt.x = p.x;
		anchorPt.y = p.y;

		stretchedPt.x = lastPt.x = anchorPt.x;
		stretchedPt.y = lastPt.y = anchorPt.y;
	}

	abstract public void drawLast(Graphics g);

	abstract public void drawNext(Graphics g);

	public void end(final Point p) {
		lastPt.x = endPt.x = p.x;
		lastPt.y = endPt.y = p.y;

		final Graphics g = component.getGraphics();
		if (g != null) {
			try {
				setMyColor(g, Color.white);
				drawLast(g);
			} finally {
				g.dispose();
			}
		}
	}

	public Point getAnchor() {
		return anchorPt;
	}

	public Rectangle getBounds() {
		return new Rectangle(stretchedPt.x < anchorPt.x ? stretchedPt.x : anchorPt.x,
				stretchedPt.y < anchorPt.y ? stretchedPt.y : anchorPt.y, Math.abs(stretchedPt.x - anchorPt.x),
				Math.abs(stretchedPt.y - anchorPt.y));
	}

	public Point getEnd() {
		return endPt;
	}

	public Point getLast() {
		return lastPt;
	}

	public Point getStretched() {
		return stretchedPt;
	}

	public boolean isActive() {
		return active;
	}

	public Rectangle lastBounds() {
		return new Rectangle(lastPt.x < anchorPt.x ? lastPt.x : anchorPt.x,
				lastPt.y < anchorPt.y ? lastPt.y : anchorPt.y, Math.abs(lastPt.x - anchorPt.x),
				Math.abs(lastPt.y - anchorPt.y));
	}

	@Override
	public void mouseClicked(final MouseEvent p1) {
		if (isActive())
			end(p1.getPoint());
	}

	@Override
	public void mouseDragged(final MouseEvent p1) {
		if (isActive())
			stretch(p1.getPoint());
	}

	@Override
	public void mouseEntered(final MouseEvent p1) {
		// do nothing
	}

	@Override
	public void mouseExited(final MouseEvent p1) {
		// do nothing
	}

	@Override
	public void mouseMoved(final MouseEvent p1) {
		// do nothing
	}

	@Override
	public void mousePressed(final MouseEvent p1) {
		if (isActive())
			anchor(p1.getPoint());
	}

	@Override
	public void mouseReleased(final MouseEvent p1) {
		if (isActive())
			end(p1.getPoint());
	}

	public void removeFromComponent(final Component c) {
		c.removeMouseListener(this);
		c.removeMouseMotionListener(this);
	}

	public void setActive(final boolean b) {
		active = b;
	}

	public void setComponent(final Component c) {
		component = c;

		component.addMouseListener(this);
		component.addMouseMotionListener(this);
	}

	protected void setMyColor(final Graphics g, final Color col) {
		if (col != null) {
			g.setXORMode(col);
		} else {
			g.setXORMode(component.getBackground());
		}
	}

	public void stretch(final Point p) {
		lastPt.x = stretchedPt.x;
		lastPt.y = stretchedPt.y;
		stretchedPt.x = p.x;
		stretchedPt.y = p.y;

		final Graphics g = component.getGraphics();
		if (g != null) {
			try {
				setMyColor(g, Color.white);

				if (firstStretch == true)
					firstStretch = false;
				else
					drawLast(g);

				drawNext(g);
			} finally {
				g.dispose();
			}
		}
	}
}
