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

//------------------------------------------------------------------------------
// Copyright (c) 1996, 1996 Borland International, Inc. All Rights Reserved.
//------------------------------------------------------------------------------

package MWC.GUI.SplitPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The ItemPainter interface defines a single item painter for non-edit
 * painting.
 */
public interface ItemPainter {
	/**
	 * This is the default state.
	 */
	public static final int DEFAULT = 0x0000;

	/**
	 * Set if this item is disabled.
	 */
	public static final int DISABLED = 0x0001;

	/**
	 * Set if this item has input focus.
	 */
	public static final int FOCUSED = 0x0002;

	/**
	 * Set if this item is selected or checked.
	 */
	public static final int SELECTED = 0x0004;

	/**
	 * Set if this item has an unknown selected state (overrides selected).
	 */
	public static final int INDETERMINATE = 0x0008;

	/**
	 * Set if this item contents are open (otherwise closed).
	 */
	public static final int OPENED = 0x0010;

	/**
	 * Set if this item's owning window is inactive / not focused.
	 */
	public static final int INACTIVE = 0x0020;

	/**
	 * Returns the preferred size of the ItemPainter.
	 *
	 * @param data     The data object to use for size calculation.
	 * @param graphics The Graphics object to use for size calculation.
	 * @param state    The current state of the object.
	 * @param site     The ItemPaintSite with information about fonts, margins, etc.
	 * @return The calculated Dimension object representing the preferred size of
	 *         this ItemPainter.
	 */
	public Dimension getPreferredSize(Object data, Graphics graphics, int state, ItemPaintSite site);

	/**
	 * Paints the data Object within the Rectangle bounds, using passed Graphics and
	 * state information.
	 *
	 * @param data     The data object to paint.
	 * @param graphics The Graphics object to paint to.
	 * @param bounds   The Rectangle extents to paint in.
	 * @param state    The current state information for the data object.
	 * @param site     The ItemPaintSite with information about fonts, margins, etc.
	 */
	public void paint(Object data, Graphics graphics, Rectangle bounds, int state, ItemPaintSite site);
}
