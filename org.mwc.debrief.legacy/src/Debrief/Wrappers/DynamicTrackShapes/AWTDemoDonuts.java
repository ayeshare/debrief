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
package Debrief.Wrappers.DynamicTrackShapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class AWTDemoDonuts extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		final AWTDemoDonuts awtDemoDonuts = new AWTDemoDonuts();
		awtDemoDonuts.setParameters(100, 400, 150, 200);
		awtDemoDonuts.createFullDonut();
		awtDemoDonuts.setParameters(100, 100, 150, 200, 45, 135);
		awtDemoDonuts.createSectorDonut();
		awtDemoDonuts.showShapes();
	}

	private int xDestination;

	private int yDestination;
	private double innerRadius;

	private double outerRadius;
	private double minAngle;

	private double angleExtent;

	private int leftCornerShift;
	private Shape fullDonut;

	private Shape sectorDonut;

	public AWTDemoDonuts() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(400, 700);
		setLayout(null);
	}

	public void createFullDonut() {
		final Ellipse2D circleOuter = new Ellipse2D.Double();
		final Ellipse2D circleInner = new Ellipse2D.Double();
		circleOuter.setFrame(xDestination, yDestination, outerRadius, outerRadius);
		circleInner.setFrame(xDestination + leftCornerShift, yDestination + leftCornerShift, innerRadius, innerRadius);

		fullDonut = subtract(circleOuter, circleInner);
	}

	public void createSectorDonut() {
		final Ellipse2D circleOuter = new Ellipse2D.Double();
		circleOuter.setFrame(xDestination, yDestination, outerRadius, outerRadius);

		final Ellipse2D circleInner = new Ellipse2D.Double();
		circleInner.setFrame(xDestination + leftCornerShift, yDestination + leftCornerShift, innerRadius, innerRadius);

		/**
		 * Create circle sector from maxAngle to minAngle.
		 */
		final Arc2D circleSector = new Arc2D.Double(xDestination - 1, yDestination - 1, outerRadius + 2,
				outerRadius + 2, -(minAngle - 90), 360 - angleExtent, Arc2D.PIE);

		/**
		 * Subtract inner circle from outer thus creating needed donut. Subtract circle
		 * sector from maxAngle to minAngle from created donut.
		 */
		sectorDonut = subtract(subtract(circleOuter, circleInner), circleSector);
	}

	@Override
	public void paint(final Graphics graphics) {
		if (sectorDonut == null || fullDonut == null) {
			System.out.println("PLease create full and sector donuts first.");
		}
		final Graphics2D graphics2D = (Graphics2D) graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		graphics2D.setPaint(Color.green);
		graphics2D.fill(sectorDonut);
		graphics2D.draw(sectorDonut);

		graphics2D.setPaint(Color.green);
		graphics2D.fill(fullDonut);
		graphics2D.draw(fullDonut);
	}

	public void setParameters(final int x, final int y, final double innerR, final double outerR) {
		xDestination = x;
		yDestination = y;
		innerRadius = innerR;
		outerRadius = outerR;
		leftCornerShift = (int) (outerRadius - innerRadius) / 2;
	}

	/**
	 * Method to show shapes.
	 *
	 * @param x      of upper left corner of donut sector
	 * @param y      of upper left corner of donut sector
	 * @param innerR radius of inner circle in donut
	 * @param outerR raius of outer circle in donut
	 * @param minA   start angle of donut sector
	 * @param maxA   end angle of donut sector
	 */
	public void setParameters(final int x, final int y, final double innerR, final double outerR, final double minA,
			final double maxA) {
		setParameters(x, y, innerR, outerR);
		minAngle = minA;
		angleExtent = maxA - minA;

	}

	public void showShapes() {
		setVisible(true);
	}

	/**
	 * Helper method to create shapes by subtracting some two of them
	 *
	 * @param shape1 minuend
	 * @param shape2 subtrahend
	 * @return new shape
	 */
	private Shape subtract(final Shape shape1, final Shape shape2) {
		final Shape result = new Area(shape1);
		((Area) result).subtract(new Area(shape2));
		return result;
	}
}