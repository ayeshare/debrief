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

package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @author Mikael Couzic
 */

abstract class BooleanOperator implements ElasticFilter {

	private final Set<ElasticFilter> filters;

	BooleanOperator(final Collection<ElasticFilter> filters) {
		checkNotNull(filters);
		checkArgument(!filters.isEmpty(), "Empty collection");
		this.filters = ImmutableSet.copyOf(filters);
	}

	abstract String getOperatorName();

	@Override
	public String toRequestString() {
		if (filters.size() == 1) // No need for boolean filter, just return the single filter
			return filters.iterator().next().toRequestString();

		final StringBuilder sb = new StringBuilder().append("{\"" + getOperatorName() + "\":[");
		for (final Iterator<ElasticFilter> i = filters.iterator(); i.hasNext();) {
			sb.append(i.next().toRequestString());
			if (i.hasNext())
				sb.append(",");
		}
		sb.append("]}");
		return sb.toString();
	}

	@Override
	public String toString() {
		return getClass().getName() + " : " + filters;
	}

}