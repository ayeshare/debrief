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

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.spec.FilterMode;

import com.google.common.collect.ImmutableSet;

/**
 * Smart "terms" filter. If it is passed only one value, it will behave as if it
 * was a "term" (single) filter
 *
 * @author Mikael Couzic
 */

class Terms implements ElasticFilter {

	private final ElasticFacet facet;
	private final Set<String> facetValues;
	private final FilterMode filterMode;

	Terms(final ElasticFacet facet, final Collection<String> facetValues) {
		this(facet, facetValues, FilterMode.ANY_OF);
	}

	Terms(final ElasticFacet facet, final Collection<String> facetValues, final FilterMode filterMode) {
		checkNotNull(facet);
		checkNotNull(facetValues);
		checkArgument(!facetValues.isEmpty(), "Empty collection");
		this.facet = facet;
		this.facetValues = ImmutableSet.copyOf(facetValues);
		this.filterMode = filterMode;
	}

	@Override
	public String toRequestString() {
		if (facetValues.size() == 1) // No need for plural "terms", just return a singular "term"
			return new Term(facet, facetValues.iterator().next()).toRequestString();

		final StringBuilder sb = new StringBuilder("{\"terms\":{\"" + facet.toRequestString() + "\":[");
		for (final Iterator<String> i = facetValues.iterator(); i.hasNext();) {
			sb.append("\"" + i.next() + "\"");
			if (i.hasNext())
				sb.append(",");
		}
		sb.append("]");
		if (filterMode.equals(FilterMode.ALL_OF))
			sb.append(",\"execution\":\"and\"");
		return sb.append("}}").toString();
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
