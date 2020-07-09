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

package MWC.GUI.TabPanel;

//package symantec.itools.awt.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

/**
 *
 *
 * @version 1.0, Nov 26, 1996
 *
 * @author	Symantec
 *
 */

// 	04/11/97	LAB	Added the getGraphics(Image image, Component component) function
// 	04/20/97	LAB	Added import statements for Image and Component.
//				Fixed a type-o in the getGraphics function I added previously that
//				prevented it from working properly.
//	06/01/97	RKM	Added findComponent method

/**
 * An all-static utility class with handy helper methods to retrieve font
 * information.
 */
public class Util {
	/**
	 * Finds the index of a component in a container.
	 *
	 * @param container the container to search
	 * @param component the component to find
	 * @return the zero-relative component index, or -1 if the component is not
	 *         found in the container
	 */
	public static int findComponent(final Container container, final Component component) {
		final Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] == component)
				return i;
		}
		return -1;
	}

	/**
	 * Retrieves a default dialog font. In this case the typeface is a plain
	 * 12-point Dialog.
	 *
	 * @return the default dialog font
	 */
	public static Font getDefaultFont() {
		return new Font("Dialog", 12, Font.PLAIN);
	}

	/**
	 * Determines the height of the font specified. This is the standard height of a
	 * line of text in the font.
	 *
	 * @param f the font
	 * @return the font height, in pixels
	 */
	@SuppressWarnings("deprecation")
	public static int getFontHeight(final Font f) {
		return getFontHeight(Toolkit.getDefaultToolkit().getFontMetrics(f));
	}

	/**
	 * Determines the height of the font given the metrics of that font. This is the
	 * standard height of a line of text in the font.
	 *
	 * @param m the metrics of the font
	 * @return the font height, in pixels
	 */
	public static int getFontHeight(final FontMetrics m) {
		return m.getHeight();
	}

	/**
	 * Determines the height of the font being used by the given graphics context.
	 * This is the standard height of a line of text in the font.
	 *
	 * @param g a graphics context
	 * @return the font height, in pixels
	 */
	public static int getFontHeight(final Graphics g) {
		return getFontHeight(g.getFontMetrics());
	}

	// getGraphics
	/**
	 * Preserves the font information of a graphics object retrieved from an image
	 * so that susequent calls to getFontMetrics through the graphics object will
	 * not result in a NullPointerException.
	 */
	/**
	 * Preserves the font information of a graphics object retrieved from an image.
	 * This ensures that susequent calls to getFontMetrics through the graphics
	 * object will not result in a NullPointerException. It does this by setting the
	 * font in the image's graphics context to the font in the component, as needed.
	 *
	 * @param image     the image with the desired graphics context
	 * @param component the component
	 * @return a graphics context with a currently set font
	 */
	public static Graphics getGraphics(final Image image, final Component component) {
		if (image == null)
			return null;

		final Graphics graphics = image.getGraphics();
		if (graphics != null && component != null && component.getFont() != null) {
			graphics.setFont(component.getFont());
		}
		return graphics;
	}

	/**
	 * Determines the width of the given string if it were drawn using the specified
	 * font.
	 *
	 * @param g the font
	 * @param s the string to determine the width of
	 * @return the width of the string, in pixels
	 */
	@SuppressWarnings("deprecation")
	public static int getStringWidth(final Font f, final String s) {
		return getStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(f), s);
	}

	/**
	 * Determines the width of the given string if it were drawn in a font with the
	 * specified metrics.
	 *
	 * @param m the font metrics
	 * @param s the string to determine the width of
	 * @return the width of the string, in pixels
	 */
	public static int getStringWidth(final FontMetrics m, final String s) {
		return m.stringWidth(s);
	}

	/**
	 * Determines the width of the given string if it were drawn using the specified
	 * graphics context.
	 *
	 * @param g the graphics context
	 * @param s the string to determine the width of
	 * @return the width of the string, in pixels
	 */
	public static int getStringWidth(final Graphics g, final String s) {
		return getStringWidth(g.getFontMetrics(), s);
	}

	/**
	 * Do not use, all-static class.
	 */
	public Util() {
	}
}
