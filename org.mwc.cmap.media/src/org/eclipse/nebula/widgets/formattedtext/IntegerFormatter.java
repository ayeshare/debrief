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

package org.eclipse.nebula.widgets.formattedtext;

import java.util.Locale;

/**
 * This class provides formatting of <code>Integer</code> values in a
 * <code>FormattedText</code>.
 * <p>
 *
 * NumberFormatter returns different numeric types based on the current value in
 * the Text field. IntegerFormatter is an override of NumberFormatter allowing
 * to guaranty to always return Integer values (Number.intValue()).
 */
public class IntegerFormatter extends NumberFormatter {
	public IntegerFormatter() {
		super();
	}

	public IntegerFormatter(final Locale loc) {
		super(loc);
	}

	public IntegerFormatter(final String editPattern) {
		super(editPattern);
	}

	public IntegerFormatter(final String editPattern, final Locale loc) {
		super(editPattern, loc);
	}

	public IntegerFormatter(final String editPattern, final String displayPattern) {
		super(editPattern, displayPattern);
	}

	public IntegerFormatter(final String editPattern, final String displayPattern, final Locale loc) {
		super(editPattern, displayPattern, loc);
	}

	/**
	 * Returns the current value of the text control if it is a valid
	 * <code>Integer</code>. If the buffer is flagged as modified, the value is
	 * recalculated by parsing with the <code>nfEdit</code> initialized with the
	 * edit pattern. If the number is not valid, returns <code>null</code>.
	 *
	 * @return current number value if valid, <code>null</code> else
	 * @see ITextFormatter#getValue()
	 */
	@Override
	public Object getValue() {
		final Object value = super.getValue();
		if (value instanceof Number) {
			return new Integer(((Number) value).intValue());
		}
		return super.getValue();
	}

	/**
	 * Returns the type of value this {@link ITextFormatter} handles, i.e. returns
	 * in {@link #getValue()}.<br>
	 * An IntegerFormatter always returns an Integer value.
	 *
	 * @return The value type.
	 */
	@Override
	public Class getValueType() {
		return Integer.class;
	}
}
