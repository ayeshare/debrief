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

package MWC.GUI.Shapes;

// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.StringTokenizer;

/**
 * A custom component that displays multiple lines of text with specified
 * margins and alignment. In Java 1.1, we could extend Component instead of
 * Canvas, making this a more efficient "Lightweight component"
 */
public class MultiLineLabel extends Canvas {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final int LEFT = 0, CENTER = 1, RIGHT = 2; // alignment values
	// User-specified attributes
	protected String label; // The label, not broken into lines
	protected int margin_width; // Left and right margins
	protected int margin_height; // Top and bottom margins
	protected int alignment; // The alignment of the text.
	// Computed state values
	protected int num_lines; // The number of lines
	protected String[] lines; // The label, broken into lines
	protected int[] line_widths; // How wide each line is
	protected int max_width; // The width of the widest line
	protected int line_height; // Total height of the font
	protected int line_ascent; // Font height above baseline
	protected boolean measured = false; // Have the lines been measured?

	public MultiLineLabel() {
		this("");
	}

	public MultiLineLabel(final String label) {
		this(label, 10, 10, LEFT);
	}

	public MultiLineLabel(final String label, final int alignment) {
		this(label, 10, 10, alignment);
	}

	public MultiLineLabel(final String label, final int margin_width, final int margin_height) {
		this(label, margin_width, margin_height, LEFT);
	}

	// Here are five versions of the constructor
	public MultiLineLabel(final String label, final int margin_width, final int margin_height, final int alignment) {
		this.label = label; // Remember all the properties
		this.margin_width = margin_width;
		this.margin_height = margin_height;
		this.alignment = alignment;
		newLabel(); // Break the label up into lines
	}

	public int getAlignment() {
		return alignment;
	}

	public String getLabel() {
		return label;
	}

	public int getMarginHeight() {
		return margin_height;
	}

	public int getMarginWidth() {
		return margin_width;
	}

	/**
	 * This internal method figures out how the font is, and how wide each line of
	 * the label is, and how wide the widest line is.
	 */
	@SuppressWarnings("deprecation")
	protected synchronized void measure() {
		final FontMetrics fm = this.getToolkit().getFontMetrics(this.getFont());
		line_height = fm.getHeight();
		line_ascent = fm.getAscent();
		max_width = 0;
		for (int i = 0; i < num_lines; i++) {
			line_widths[i] = fm.stringWidth(lines[i]);
			if (line_widths[i] > max_width)
				max_width = line_widths[i];
		}
		measured = true;
	}

	/**
	 * This method is called when the layout manager wants to know the bare minimum
	 * amount of space we need to get by. For Java 1.1, we'd use getMinimumSize().
	 */
	@Override
	public Dimension minimumSize() {
		return preferredSize();
	}

	/**
	 * This internal method breaks a specified label up into an array of lines. It
	 * uses the StringTokenizer utility class.
	 */
	protected synchronized void newLabel() {
		final StringTokenizer t = new StringTokenizer(label, "\n");
		num_lines = t.countTokens();
		lines = new String[num_lines];
		line_widths = new int[num_lines];
		for (int i = 0; i < num_lines; i++)
			lines[i] = t.nextToken();
	}

	/**
	 * This method draws the label (same method that applets use). Note that it
	 * handles the margins and the alignment, but that it doesn't have to worry
	 * about the color or font--the superclass takes care of setting those in the
	 * Graphics object we're passed.
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void paint(final Graphics g) {
		int x, y;
		final Dimension size = this.size(); // use getSize() in Java 1.1
		if (!measured)
			measure();
		y = line_ascent + (size.height - num_lines * line_height) / 2;
		for (int i = 0; i < num_lines; i++, y += line_height) {
			switch (alignment) {
			default:
			case LEFT:
				x = margin_width;
				break;
			case CENTER:
				x = (size.width - line_widths[i]) / 2;
				break;
			case RIGHT:
				x = size.width - margin_width - line_widths[i];
				break;
			}
			g.drawString(lines[i], x, y);
		}
	}

	/**
	 * This method is called by a layout manager when it wants to know how big we'd
	 * like to be. In Java 1.1, getPreferredSize() is the preferred version of this
	 * method. We use this deprecated version so that this component can
	 * interoperate with 1.0 components.
	 */
	@Override
	public Dimension preferredSize() {
		if (!measured)
			measure();
		return new Dimension(max_width + 2 * margin_width, num_lines * line_height + 2 * margin_height);
	}

	public void setAlignment(final int a) {
		alignment = a;
		repaint();
	}

	@Override
	public void setFont(final Font f) {
		super.setFont(f); // tell our superclass about the new font
		measured = false; // Note that we need to remeasure lines
		repaint(); // Request a redraw
	}

	@Override
	public void setForeground(final Color c) {
		super.setForeground(c); // tell our superclass about the new color
		repaint(); // Request a redraw (size is unchanged)
	}

	// Methods to set and query the various attributes of the component
	// Note that some query methods are inherited from the superclass.
	public void setLabel(final String label) {
		this.label = label;
		newLabel(); // Break the label into lines
		measured = false; // Note that we need to measure lines
		repaint(); // Request a redraw
	}

	public void setMarginHeight(final int mh) {
		margin_height = mh;
		repaint();
	}

	public void setMarginWidth(final int mw) {
		margin_width = mw;
		repaint();
	}
}
