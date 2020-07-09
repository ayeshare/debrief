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

package org.mwc.cmap.NarrativeViewer;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import MWC.TacticalData.NarrativeEntry;

public abstract class ColumnFilter implements EntryFilter {
	private final TreeSet<String> myAllowedValues = new TreeSet<String>();
	private final SortedSet<String> myAllowedValuesRO = Collections.unmodifiableSortedSet(myAllowedValues);

	@Override
	public boolean accept(final NarrativeEntry entry) {
		if (isEmpty()) {
			return true;
		}

		final String value = getFilterValue(entry);
		return value != null && myAllowedValues.contains(value);
	}

	public void clear() {
		if (!myAllowedValues.isEmpty()) {
			myAllowedValues.clear();
			valuesSetChanged();
		}
	}

	public SortedSet<String> getAllowedValues() {
		return myAllowedValuesRO;
	}

	public abstract String getFilterValue(NarrativeEntry entry);

	public boolean isEmpty() {
		return myAllowedValues.isEmpty();
	}

	public void setAllowedValues(final Collection<String> allowedValues) {
		if (myAllowedValues.isEmpty() && allowedValues.isEmpty()) {
			return;
		}
		myAllowedValues.clear();
		myAllowedValues.addAll(allowedValues);
		valuesSetChanged();
	}

	protected abstract void valuesSetChanged();

}
