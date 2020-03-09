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
package MWC.GUI.Properties;

import java.awt.Color;

/* custom-designed set of Debrief color shades
 *
 */
public interface DebriefColors {
	/**
	 * helper, to provide random Debrief-like colors
	 *
	 * @author ian
	 *
	 */
	public static class RandomColorProvider {
		/**
		 * get a random color, based on the supplied hash
		 *
		 * @param hash
		 * @return
		 */
		public static Color getRandomColor(final int hash) {
			final int index = hash % DebriefColors.THIRD_PARTY_COLORS.length;
			final Color res = DebriefColors.THIRD_PARTY_COLORS[index];
			return res;
		}
	}

	final public static Color BLACK = new Color(26, 26, 26);
	final public static Color DARK_BLUE = new Color(101, 149, 204);
	final public static Color DARK_GRAY = new Color(192, 192, 192);
	final public static Color MEDIUM_BLUE = new Color(165, 191, 221);
	final public static Color LIGHT_GRAY = new Color(237, 237, 237);

	final public static Color WHITE = new Color(255, 255, 254);

	/**
	 * introduce pure white constant. We need to check for it, since SWT has trouble
	 * plotting it.
	 */
	final public static Color PURE_WHITE = new Color(255, 255, 255);
	final public static Color RED = new Color(224, 28, 62);
	final public static Color GREEN = new Color(0, 128, 11);

	final public static Color BLUE = new Color(0, 100, 189);
	final public static Color LIGHT_GREEN = new Color(88, 255, 0);
	final public static Color YELLOW = new Color(255, 215, 0);
	final public static Color ORANGE = new Color(255, 150, 0);
	final public static Color BROWN = new Color(153, 102, 0);
	final public static Color CYAN = new Color(0, 255, 255);
	final public static Color PINK = new Color(255, 77, 255);

	final public static Color PURPLE = new Color(161, 0, 230);
	// old colors
	public static Color MAGENTA = Color.magenta;
	public static Color GOLD = new Color(230, 200, 20);

	public static Color GRAY = Color.gray;

	public static Color[] COLORS = new Color[] { BLACK, DARK_BLUE, DARK_GRAY, MEDIUM_BLUE, LIGHT_GRAY, WHITE, RED,
			GREEN, BLUE, LIGHT_GREEN, YELLOW, ORANGE, BROWN, CYAN, PINK, PURPLE, MAGENTA, GOLD, GRAY };

	/**
	 * set of colors that could be used to color third party tracks (avoids standard
	 * red & blue, and gray shades)
	 */
	public static Color[] THIRD_PARTY_COLORS = new Color[] { DARK_BLUE, MEDIUM_BLUE, GREEN, LIGHT_GREEN, YELLOW, ORANGE,
			BROWN, CYAN, PINK, PURPLE, MAGENTA, GOLD };
}
