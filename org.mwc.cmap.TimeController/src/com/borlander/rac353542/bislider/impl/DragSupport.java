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

package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

class DragSupport implements MouseListener, MouseMoveListener {

	public static interface DragListener {
		public void dragFinished();

		public void dragStarted();

		public void mouseDragged(MouseEvent e, Point startPoint);
	}

	public static final int MIN_DISTANCE_TO_BE_DRAGGING = 3;
	private final AreaGate myAreaGate;
	private final DragListener myDragListener;
	private Control myControl;
	private boolean myMayBeDragging;
	private boolean myIsDragging;

	private Point myStartPoint;

	public DragSupport(final Control control, final AreaGate areaGate, final DragListener dragListener) {
		myControl = control;
		myAreaGate = areaGate;
		myDragListener = dragListener;
		control.addMouseListener(this);
		control.addMouseMoveListener(this);
	}

	private void draggingStop() {
		if (myIsDragging) {
			myDragListener.dragFinished();
		}
		myIsDragging = false;
		myMayBeDragging = false;
	}

	private boolean isCloseEnough(final MouseEvent e) {
		return Math.abs(e.x - myStartPoint.x) <= MIN_DISTANCE_TO_BE_DRAGGING && //
				Math.abs(e.y - myStartPoint.y) <= MIN_DISTANCE_TO_BE_DRAGGING;
	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		draggingStop();
	}

	@Override
	public void mouseDown(final MouseEvent e) {
		if (myAreaGate.isInsideArea(e.x, e.y)) {
			draggingStop();
			myMayBeDragging = true;
			myStartPoint = new Point(e.x, e.y);
		}
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		if (!myMayBeDragging) {
			return;
		}
		if (!myIsDragging && isCloseEnough(e)) {
			myDragListener.dragStarted();
			myIsDragging = true;
		}
		if (myIsDragging) {
			myDragListener.mouseDragged(e, myStartPoint);
		}
	}

	@Override
	public void mouseUp(final MouseEvent e) {
		draggingStop();
	}

	public void releaseControl() {
		if (myControl != null && !myControl.isDisposed()) {
			myControl.removeMouseListener(this);
			myControl.removeMouseMoveListener(this);
			myControl = null;
		}
	}
}
