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

import static com.google.common.base.Preconditions.checkNotNull;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;

/**
 * @author Mikael Couzic
 */

class Term implements ElasticFilter {

	private final ElasticFacet facet;
	private final String facetValue;

	Term(final ElasticFacet facet, final String facetValue) {
		checkNotNull(facet);
		checkNotNull(facetValue);
		this.facet = facet;
		this.facetValue = facetValue;
	}

	@Override
	public String toRequestString() {
		return "{\"term\":{\"" + facet.toRequestString() + "\":\"" + facetValue + "\"}}";
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
