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

package org.mwc.debrief.lite.gui.custom;

/**
 * An interface to describe an object that is capable of painting with an alpha
 * value.
 *
 * @author kschaefer
 */
interface AlphaPaintable {
	/**
	 * Get the current alpha value.
	 *
	 * @return the alpha translucency level for this component. This will be a value
	 *         between 0 and 1, inclusive.
	 */
	float getAlpha();

	/**
	 * Unlike other properties, alpha can be set on a component, or on one of its
	 * parents. If the alpha of a parent component is .4, and the alpha on this
	 * component is .5, effectively the alpha for this component is .4 because the
	 * lowest alpha in the hierarchy &quot;wins.&quot;
	 *
	 * @return the lowest alpha value in the hierarchy
	 */
	float getEffectiveAlpha();

	/**
	 * Returns the state of the panel with respect to inheriting alpha values.
	 *
	 * @return {@code true} if this panel inherits alpha values; {@code false}
	 *         otherwise
	 * @see #setInheritAlpha(boolean)
	 */
	boolean isInheritAlpha();

	/**
	 * Set the alpha transparency level for this component. This automatically
	 * causes a repaint of the component.
	 *
	 * @param alpha must be a value between 0 and 1 inclusive
	 * @throws IllegalArgumentException if the value is invalid
	 */
	void setAlpha(float alpha);

	/**
	 * Determines if the effective alpha of this component should include the alpha
	 * of ancestors.
	 *
	 * @param inheritAlpha {@code true} to include ancestral alpha data;
	 *                     {@code false} otherwise
	 * @see #isInheritAlpha()
	 * @see #getEffectiveAlpha()
	 */
	void setInheritAlpha(boolean inheritAlpha);
}
