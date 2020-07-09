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

package org.bitbucket.es4gwt.shared.spec;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

/**
 * @author Mikael Couzic
 */

public class SearchDate {

	private final Date date;
	private final String dateString;

	/**
	 * @param dateString The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public SearchDate(final Date date, final String dateString) {
		checkNotNull(date);
		checkNotNull(dateString);
		this.date = date;
		this.dateString = dateString;
	}

	public Date asDate() {
		return (Date) date.clone();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof SearchDate) {
			final SearchDate that = (SearchDate) object;
			return this.date.getYear() == that.date.getYear() && this.date.getMonth() == that.date.getMonth()
					&& this.date.getDate() == that.date.getDate();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int hashCode() {
		return date.getDate() + date.getMonth() * 100 + date.getYear() * 10000;
	}

	@Override
	public String toString() {
		return dateString;
	}
}
