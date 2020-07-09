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
package Debrief.Wrappers.Extensions;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.Wrappers.DataItemWrapper;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.HasEditables;

/**
 * make a list of additional data items suitable for showing in Debrief's
 * Outline View
 *
 * @author ian
 *
 */
public class AdditionalProviderWrapper implements Editable, HasEditables, Serializable {
	protected static final class IteratorWrapper implements java.util.Enumeration<Editable> {
		private final java.util.Iterator<Editable> _val;

		public IteratorWrapper(final java.util.Iterator<Editable> iterator) {
			_val = iterator;
		}

		@Override
		public final boolean hasMoreElements() {
			return _val.hasNext();

		}

		@Override
		public final Editable nextElement() {
			return _val.next();
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the list of additional data items
	 *
	 */
	private final AdditionalData _provider;

	/**
	 * set of helpers that are able to wrap additional data items
	 *
	 */
	private final List<ExtensionContentProvider> _contentProviderExtensions;

	public AdditionalProviderWrapper(final AdditionalData additionalData,
			final List<ExtensionContentProvider> providers) {
		_provider = additionalData;
		_contentProviderExtensions = providers;
	}

	@Override
	@FireExtended
	public void add(final Editable point) {
		if (point instanceof DataItemWrapper) {
			final DataItemWrapper itemW = (DataItemWrapper) point;
			final DataFolder additionalData = (DataFolder) _provider.get(0);
			additionalData.add(itemW.getDataItem());
		} else {
			System.err.println("Can't add this data object to measured data:" + point);
		}
	}

	@Override
	public Enumeration<Editable> elements() {
		final Vector<Editable> res = new Vector<Editable>();

		// ok, are there any data items in there?
		for (final Object item : _provider) {
			// see if we have a content provider for this data type
			final List<ExtensionContentProvider> cp = _contentProviderExtensions;
			for (final ExtensionContentProvider provider : cp) {
				final List<Editable> items = provider.itemsFor(item);
				if (items != null) {
					res.addAll(items);
				}
			}
		}

		// ok, we have to wrap the data items

		return res.elements();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AdditionalProviderWrapper other = (AdditionalProviderWrapper) obj;
		if (_provider == null) {
			if (other._provider != null)
				return false;
		} else if (!_provider.equals(other._provider))
			return false;
		return true;
	}

	@Override
	public EditorType getInfo() {
		return null;
	}

	@Override
	public String getName() {
		return DataFolder.DEFAULT_NAME;
	}

	@Override
	public boolean hasEditor() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_provider == null) ? 0 : _provider.hashCode());
		return result;
	}

	@Override
	public boolean hasOrderedChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@FireExtended
	public void removeElement(final Editable point) {
		if (point instanceof DataItemWrapper) {
			final DataItemWrapper itemW = (DataItemWrapper) point;
			final DataFolder additionalData = (DataFolder) _provider.get(0);
			additionalData.remove(itemW.getDataItem());
		} else {
			System.err.println("Can't remove this data object to measured data:" + point);
		}
	}

	@Override
	public String toString() {
		// do a sophisticated count
		int size = 0;

		// loop through our providers
		for (final Object item : _provider) {
			// see if we have a content provider for this data type
			final List<ExtensionContentProvider> cp = _contentProviderExtensions;
			for (final ExtensionContentProvider provider : cp) {
				final List<Editable> items = provider.itemsFor(item);
				if (items != null) {
					size += items.size();
				}
			}
		}

		// produce suitable string decoration
		final String numStr;
		if (size == 0) {
			numStr = "Empty";
		} else {
			numStr = "" + size + " items";
		}

		return getName() + " (" + numStr + ")";
	}

}
