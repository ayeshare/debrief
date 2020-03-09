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

package com.planetmayo.debrief.satc_rcp.ui.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class CompoundConverter implements IConverter {

	private final IConverter[] converters;

	public CompoundConverter(final IConverter... converters) {
		super();
		if (converters == null || converters.length == 0) {
			throw new IllegalArgumentException("Convertes should be specified");
		}
		for (final IConverter converter : converters) {
			if (!(converter.getToType() instanceof Class && converter.getFromType() instanceof Class)) {
				throw new IllegalArgumentException("Types of convertes should be Class instances");
			}
		}
		Class<?> toType = (Class<?>) converters[0].getToType();
		for (int i = 1; i < converters.length; i++) {
			final Class<?> fromType = (Class<?>) converters[i].getFromType();
			if (!fromType.isAssignableFrom(toType)) {
				throw new IllegalArgumentException("CompoundConverters must have converters of the same type");
			}
			toType = (Class<?>) converters[i].getToType();
		}
		this.converters = converters;
	}

	@Override
	public Object convert(final Object arg0) {
		Object result = arg0;
		for (final IConverter converter : converters) {
			result = converter.convert(result);
		}
		return result;
	}

	@Override
	public Object getFromType() {
		return converters[0].getFromType();
	}

	@Override
	public Object getToType() {
		return converters[converters.length - 1].getToType();
	}
}
