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

package org.mwc.cmap.grideditor.interpolation;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;

public interface ItemsInterpolatorFactory {

	public static final ItemsInterpolatorFactory DEFAULT = new ItemsInterpolatorFactory() {

		@Override
		public ItemsInterpolator createItemsInterpolator(final GriddableItemDescriptor descriptor,
				final TimeStampedDataItem... baseItems) {
			if (!AbstractItemsInterpolator.canInterpolate(descriptor)) {
				return null;
			}
			if (baseItems.length == 2) {
				return new LinearItemsInterpolator(baseItems[0], baseItems[1], descriptor);
			}
			if (baseItems.length > 2) {
				return new CubicItemsInterpolator(descriptor, baseItems);
			}
			return null;
		}
	};

	public ItemsInterpolator createItemsInterpolator(GriddableItemDescriptor descriptor,
			TimeStampedDataItem... baseItems);
}
