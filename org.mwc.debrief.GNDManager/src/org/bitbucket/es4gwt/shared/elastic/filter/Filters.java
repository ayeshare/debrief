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

import java.util.Collection;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.spec.FilterMode;

import com.google.common.collect.Lists;

/**
 * @author Mikael Couzic
 */
public class Filters {

	public static ElasticFilter allTerms(final ElasticFacet facet, final Collection<String> facetValues) {
		return new Terms(facet, facetValues, FilterMode.ALL_OF);
	}

	public static ElasticFilter and(final Collection<ElasticFilter> filters) {
		return new And(filters);
	}

	public static ElasticFilter and(final ElasticFilter... filters) {
		return new And(Lists.newArrayList(filters));
	}

	/**
	 * @param start The formatted date representing the start of the range (for
	 *              example : 2001-01-01 or 2011-12-31)
	 * @param end   The formatted date representing the end of the range (for
	 *              example : 2001-01-01 or 2011-12-31)
	 */
	public static ElasticFilter dateRange(final String start, final String end) {
		return new DateRange(start, end);
	}

	/**
	 * @param date The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public static ElasticFilter endAfter(final String date) {
		return new EndAfter(date);
	}

	public static ElasticFilter or(final Collection<ElasticFilter> filters) {
		return new Or(filters);
	}

	public static ElasticFilter or(final ElasticFilter... filters) {
		return new Or(Lists.newArrayList(filters));
	}

	/**
	 * @param date The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public static ElasticFilter startBefore(final String date) {
		return new StartBefore(date);
	}

	public static ElasticFilter term(final ElasticFacet facet, final String facetValue) {
		return new Term(facet, facetValue);
	}

	public static ElasticFilter terms(final ElasticFacet facet, final Collection<String> facetValues) {
		return new Terms(facet, facetValues);
	}

	private Filters() {
	}
}
