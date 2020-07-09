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

package com.borlander.rac353542.bislider.cdata;

import com.borlander.rac353542.bislider.BiSliderLabelProvider;

/**
 * Implementation of the <code>BiSliderLabelProvider</code> suited for working
 * woth singular set of model dats objects.
 */
public abstract class DataObjectLabelProvider implements BiSliderLabelProvider {

	private final DataObjectMapper myMapper;

	public DataObjectLabelProvider(final DataObjectMapper mapper) {
		myMapper = mapper;
	}

	@Override
	public String getLabel(final double value) {
		final Object dataObject = myMapper.double2object(value);
		if (dataObject == null) {
			// contract of DatatObjectMapper is broken but we do not want to
			// fail.
			return "";
		}
		return getLabel(dataObject);
	}

	public abstract String getLabel(Object dataObject);
}
