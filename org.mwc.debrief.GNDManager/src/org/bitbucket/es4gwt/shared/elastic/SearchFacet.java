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

package org.bitbucket.es4gwt.shared.elastic;

import org.bitbucket.es4gwt.shared.elastic.filter.ElasticFilter;

/**
 * @author Mikael Couzic
 */
public class SearchFacet implements ElasticRequestElement {

	private static final int DEFAULT_NUM_RESULTS = 1000;

	private final ElasticFacet facet;
	private final ElasticFilter filter;

	public SearchFacet(final ElasticFacet facet, final ElasticFilter filter) {
		this.facet = facet;
		this.filter = filter;
	}

	@Override
	public String toRequestString() {
		// TODO Replace the use of the DEFAULT_NUM_RESULTS constant by a variable
		final StringBuilder sb = new StringBuilder(
				"\"" + facet + "\":{\"terms\":{\"field\":\"" + facet + "\",\"size\":" + DEFAULT_NUM_RESULTS + "}");
		if (filter != null)
			sb.append(",\"facet_filter\":" + filter.toRequestString());
		return sb.append("}").toString();
	}
}
