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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;

import com.google.common.collect.Maps;

/**
 * @author Mikael Couzic
 */
public class SearchRequest {

	private static final String UNDEFINED = "Undefined";

	private static final SearchDate UNDEFINED_DATE = new SearchDate(new Date(0l), "1970-01-01") {

		@Override
		public boolean equals(final Object object) {
			return this == object;
		}

		@Override
		public String toString() {
			return "UNDEFINED DATE";
		}
	};

	private final FacetFilters facetFilters = new FacetFilters();
	// Text search related fields
	private String text = UNDEFINED;

	private ElasticFacet[] textSearchParams;
	private final Map<ElasticFacet, FilterMode> filterModes = Maps.newHashMap();
	private SearchDate start = UNDEFINED_DATE;

	private SearchDate end = UNDEFINED_DATE;

	public void after(final SearchDate start) {
		checkNotNull(start);
		checkArgument(start != UNDEFINED_DATE);
		this.start = start;
	}

	public void before(final SearchDate end) {
		checkNotNull(end);
		checkArgument(end != UNDEFINED_DATE);
		this.end = end;
	}

	private boolean equalFilterModes(final SearchRequest that) {
		if (this.filterModes.size() != that.filterModes.size())
			return false;
		for (final Entry<ElasticFacet, FilterMode> entry : this.filterModes.entrySet()) {
			if (!entry.getValue().equals(that.getFilterMode(entry.getKey())))
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof SearchRequest) {
			final SearchRequest that = (SearchRequest) object;
			return this.text.equals(that.text) && this.facetFilters.equals(that.facetFilters) && equalFilterModes(that)
					&& this.start.equals(that.start) && this.end.equals(that.end);
		}
		return false;
	}

	public void fullTextSearch(final String text, final ElasticFacet[] textSearchParams) {
		checkNotNull(text);
		checkArgument(!text.isEmpty());
		checkNotNull(textSearchParams);
		this.text = text;
		checkNotNull(textSearchParams);
		checkArgument(textSearchParams.length > 0);
		this.textSearchParams = textSearchParams;
	}

	public SearchDate getEndDate() {
		return end;
	}

	FacetFilters getFacetFilters() {
		return facetFilters;
	}

	public Iterator<Entry<ElasticFacet, Collection<String>>> getFacetFiltersIterator() {
		return facetFilters.iterator();
	}

	public FilterMode getFilterMode(final ElasticFacet facet) {
		return filterModes.get(facet);
	}

	public Set<ElasticFacet> getFilterModeFacets() {
		return filterModes.keySet();
	}

	public SearchDate getStartDate() {
		return start;
	}

	public String getText() {
		return text;
	}

	public ElasticFacet[] getTextSearchParams() {
		return textSearchParams;
	}

	public boolean hasFacetFilters() {
		return !facetFilters.isEmpty();
	}

	@Override
	public int hashCode() {
		int toReturn = facetFilters.hashCode() + text.hashCode() + start.hashCode() + (end.hashCode() * 100000);
		for (final Entry<ElasticFacet, FilterMode> entry : this.filterModes.entrySet())
			toReturn += entry.getKey().hashCode() + entry.getValue().hashCode();
		return toReturn;
	}

	public boolean isEndDateDefined() {
		return !end.equals(UNDEFINED_DATE);
	}

	public boolean isFiltered() {
		return !facetFilters.isEmpty() || isTextDefined() || isStartDateDefined() || isEndDateDefined();
	}

	public boolean isStartDateDefined() {
		return !start.equals(UNDEFINED_DATE);
	}

	public boolean isTextDefined() {
		return !text.equals(UNDEFINED) && !text.isEmpty();
	}

	public void withFacetFilter(final ElasticFacet facet, final String filterValue) {
		checkNotNull(facet);
		checkNotNull(filterValue);
		facetFilters.with(facet, filterValue);
	}

	public void withFilterMode(final ElasticFacet facet, final FilterMode filterMode) {
		checkNotNull(facet);
		checkNotNull(filterMode);
		filterModes.put(facet, filterMode);
	}

}
