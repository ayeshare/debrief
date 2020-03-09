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

package org.mwc.cmap.grideditor.command;

import java.lang.reflect.Method;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;
import MWC.TacticalData.GND.GDataItem;

/**
 * Very simplified version of the Apache Commons BeanUtil's helpers.
 * <p>
 * Provides utility methods to invoke setters/getters of some bean' property.
 */
public class BeanUtil {

	private static String capitalize(final String text) {
		if (text == null) {
			throw new NullPointerException();
		}
		if (text.length() == 0) {
			return text;
		}
		final char firstChar = text.charAt(0);
		return Character.isUpperCase(firstChar) ? text : Character.toUpperCase(firstChar) + text.substring(1);
	}

	private static String getGetterName(final GriddableItemDescriptor descriptor) {
		return "get" + capitalize(descriptor.getName());
	}

	public static Object getItemValue(final TimeStampedDataItem item, final GriddableItemDescriptor descriptor) {
		return getItemValue(item, descriptor, Object.class);
	}

	/**
	 * Reflectively invokes getter associated with given
	 * {@link GriddableItemDescriptor} on given subject {@link TimeStampedDataItem}
	 *
	 * @param item       subject item to invoke getter on
	 * @param descriptor meta-data describing the bean property
	 * @param resultType desired result type, used to avoid casts at the caller side
	 *                   only
	 * @return the value of bean property for given subject
	 * @throws IllegalArgumentException if descriptor is not applicable to given
	 *                                  item or not configured properly (e.g, if
	 *                                  getter not accessible, or does not conform
	 *                                  to Beans specification, etc)
	 * @throws ClassCastException       if desired result type is not compatible
	 *                                  with actual value
	 * @throws NullPointerException     if given item is <code>null</code>
	 */
	public static <T> T getItemValue(final TimeStampedDataItem item, final GriddableItemDescriptor descriptor,
			final Class<T> resultType) {
		if (item == null) {
			throw new NullPointerException();
		}
		// special case - GDataItem
		if (item instanceof GDataItem) {
			// we don't use bean introspection for these types. They're schema free,
			// so we don't have getters and setters
			final GDataItem g = (GDataItem) item;
			return resultType.cast(g.getValue(descriptor.getName()));
		}

		final String getterName = getGetterName(descriptor);
		Method getter;
		try {
			getter = item.getClass().getMethod(getterName);
		} catch (final NoSuchMethodException e) {
			throw new IllegalArgumentException(//
					"Descriptor: " + descriptor.getTitle() + //
							" is not applicable to item: " + item.getClass() + //
							", there are no getters with name : " + getterName);
		}
		final Class<?> actualGetterType = getter.getReturnType();
		// if (!descriptor.getType().isAssignableFrom(actualGetterType)) {
		// throw new IllegalArgumentException(//
		// "Descriptor: " + descriptor.getTitle() + //
		// " is not applicable to item: " + item.getClass() + ", expected type: " +
		// descriptor.getType() + //
		// ", actual getter type: " + actualGetterType);
		// }
		if (!resultType.isAssignableFrom(actualGetterType)) {
			// take auto-boxing into account
			boolean autoBoxable = actualGetterType.isPrimitive() && resultType.equals(Object.class);
			if (!autoBoxable) {
				autoBoxable = actualGetterType.isPrimitive() && resultType.equals(Number.class);
			}
			if (!autoBoxable) {
				throw new ClassCastException(//
						"Can not cast actual getter type: " + actualGetterType + //
								" to desired type " + resultType);
			}
		}

		Object result;
		try {
			result = getter.invoke(item);
		} catch (final Exception e) {
			throw new IllegalArgumentException(//
					"Can't invoke getter " + getterName + //
							" for object " + item + //
							" of class " + item.getClass(), //
					e);
		}
		return resultType.cast(result);
	}

	public static Method getSetter(final TimeStampedDataItem item, final GriddableItemDescriptor descriptor,
			final String setterName) {
		Method setter = null;

		try {
			setter = item.getClass().getMethod(setterName, descriptor.getType());
		} catch (final NoSuchMethodException e) {
		}

		// did it work?
		if (setter == null) {
			// is it because we're using a complex primitive?
			if (descriptor.getType() == Double.class) {
				try {
					setter = item.getClass().getMethod(setterName, double.class);
				} catch (final NoSuchMethodException e) {
				}
			}
			if (descriptor.getType() == Boolean.class)
				try {
					setter = item.getClass().getMethod(setterName, boolean.class);
				} catch (final NoSuchMethodException e) {
				}
		}

		if (setter == null)
			throw new IllegalArgumentException(//
					"Descriptor: " + descriptor.getTitle() + //
							" is not applicable to item: " + item.getClass() + //
							", there are no setters with name : " + setterName + //
							" and parameter type : " + descriptor.getType());
		return setter;
	}

	public static String getSetterName(final GriddableItemDescriptor descriptor) {
		return "set" + capitalize(descriptor.getName());
	}

	/**
	 * Reflectively invokes setter associated with given
	 * {@link GriddableItemDescriptor} on given subject {@link TimeStampedDataItem}
	 * with given value (<code>null</code> value allowed).
	 *
	 * NOTE: this method is intentionally package local, all items modifications
	 * should go through the {@link SetDescriptorValueOperation}.
	 *
	 * @param item       subject item to invoke setter on
	 * @param descriptor meta-data describing the bean property
	 * @param theValue   new value to set
	 *
	 * @throws IllegalArgumentException if descriptor is not applicable to given
	 *                                  item or not configured properly (e.g, if
	 *                                  setter is not accessible, or does not
	 *                                  conform to Beans specification, etc)
	 * @throws ClassCastException       if actual value type is not compatible with
	 *                                  descriptor meta-data
	 * @throws NullPointerException     if given item is <code>null</code>
	 */
	static void setItemValue(final TimeStampedDataItem item, final GriddableItemDescriptor descriptor,
			final Object value) {
		if (item == null) {
			throw new NullPointerException();
		}

		// convert the value from text editor to target datat ype
		final Object theValue = descriptor.getEditor().translateFromSWT(value);

		// special case for GDataItems who do not have getter/setters
		if (item instanceof GDataItem) {
			// we don't use bean introspection for these types. They're schema free,
			// so we don't have getters and setters
			final GDataItem g = (GDataItem) item;
			g.setValue(descriptor.getName(), theValue);
			return;
		}

		if (theValue != null && !descriptor.getType().isAssignableFrom(theValue.getClass())) {
			// not that strong of a check, but we will fail later (on invocation) in
			// any case
			final boolean autoUnboxable = descriptor.getType().isPrimitive()
					&& Number.class.isAssignableFrom(theValue.getClass());
			if (!autoUnboxable) {
				throw new ClassCastException(//
						"Can not cast actual value of type: " + theValue.getClass() + //
								" to descriptor type " + descriptor.getType());
			}
		}

		final String setterName = getSetterName(descriptor);
		final Method setter = getSetter(item, descriptor, setterName);

		try {
			setter.invoke(item, theValue);
		} catch (final Exception e) {
			throw new IllegalArgumentException(//
					"Can't invoke setter " + setterName + //
							" for object " + item + //
							" of class " + item.getClass() + //
							" and actual parameter : " + theValue,
					e);
		}
	}

}
