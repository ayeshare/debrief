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

package org.bitbucket.es4gwt.shared.elastic.query;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Mikael Couzic
 */

class Field implements ElasticQuery {

	private final String requestString;

	Field(final String fieldName, final String fieldValue) {
		checkNotNull(fieldName);
		checkNotNull(fieldValue);
		requestString = "{\"field\":{\"" + fieldName + "\":\"" + fieldValue + "\"}}";
	}

	@Override
	public String toRequestString() {
		return requestString;
	}

}
