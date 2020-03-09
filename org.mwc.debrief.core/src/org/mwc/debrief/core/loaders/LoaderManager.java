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

package org.mwc.debrief.core.loaders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.debrief.core.interfaces.IPlotLoader;

/**
 * convenience class which handles loading/creating extensions
 *
 * @author ian.mayo
 *
 */
public abstract class LoaderManager {
	private ArrayList<INamedItem> _loaders;

	// Extension point tag and attributes in plugin.xml
	private final String EXTENSION_POINT_ID;
	private final String EXTENSION_TAG;
	private final String PLUGIN_ID;
	private final String EXTENSION_TAG_LABEL_ATTRIB = "name";

	public LoaderManager(final String extensionId, final String extensionTag, final String pluginId) {
		EXTENSION_POINT_ID = extensionId;
		EXTENSION_TAG = extensionTag;
		PLUGIN_ID = pluginId;

		getDataLoaders();
	}

	private void addToolActionDescriptor(final IConfigurationElement configElement) {
		final String label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);

		// get menu item label
		// search for double entries
		boolean doubleEntry = false;
		for (int i = 0; i < getToolActionDescriptors().size(); i++) {
			final String l = getToolActionDescriptors().get(i).getName();
			if (l.equals(label))
				doubleEntry = true;
		}

		// we take the first matching label
		if (!doubleEntry) {
			final INamedItem newInstance = createInstance(configElement, label);
			getToolActionDescriptors().add(newInstance);
		} else {
			CorePlugin.logError(IStatus.ERROR, "Tag:" + configElement.getName() + " failed to load. " + label
					+ " already. Check for double-entry in plugin.xml", null);
		}

	}

	/**
	 * create one of our objects from the details supplied
	 *
	 * @param configElement
	 * @param label
	 * @return
	 */
	abstract public INamedItem createInstance(IConfigurationElement configElement, String label);

	public IPlotLoader[] findLoadersFor(final String fileName) {
		final Vector<IPlotLoader> list = new Vector<IPlotLoader>(0, 1);

		for (final Iterator<INamedItem> iter = _loaders.iterator(); iter.hasNext();) {
			final IPlotLoader element = (IPlotLoader) iter.next();

			// can it do it?
			if (element.canLoad(fileName)) {
				// cool, add to the list
				list.add(element);
			}
		}

		IPlotLoader[] res = {};

		if (list.size() > 0) {
			final Object[] tmp = list.toArray(res);
			res = (IPlotLoader[]) tmp;
		}

		return res;
	}

	private void getDataLoaders() {
		_loaders = new ArrayList<INamedItem>();
		final IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);

		// check: Any <extension> tags for our extension-point?
		if (point != null) {
			final IExtension[] extensions = point.getExtensions();

			for (int i = 0; i < extensions.length; i++) {
				final IConfigurationElement[] ces = extensions[i].getConfigurationElements();

				for (int j = 0; j < ces.length; j++) {
					// if this is the tag we want ("tool") create a descriptor
					// for it
					if (ces[j].getName().equals(EXTENSION_TAG))
						addToolActionDescriptor(ces[j]);
				}
			}
		}

		// Check if no extensions or empty extensions
		if (point == null || getToolActionDescriptors().size() == 0) {
			CorePlugin.logError(IStatus.WARNING, "No data loaders found", null);
		}
	}

	private ArrayList<INamedItem> getToolActionDescriptors() {
		return _loaders;
	}

}
