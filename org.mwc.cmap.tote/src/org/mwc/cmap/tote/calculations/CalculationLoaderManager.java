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

package org.mwc.cmap.tote.calculations;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.mwc.cmap.tote.TotePlugin;

import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Tools.Tote.toteCalculation;

/**
 * convenience class which handles loading/creating extensions
 *
 * @author ian.mayo
 */
public abstract class CalculationLoaderManager {
	public static class DeferredCalculation implements toteCalculation {
		toteCalculation _myCalc = null;

		final private IConfigurationElement _config;

		/**
		 * constructor - stores the information necessary to load the data
		 *
		 * @param configElement
		 * @param name
		 * @param icon
		 * @param fileTypes
		 */
		public DeferredCalculation(final IConfigurationElement configElement, final String name, final String icon) {
			_config = configElement;
		}

		@Override
		public double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime) {
			checkMe();
			return _myCalc.calculate(primary, secondary, thisTime);
		}

		private void checkMe() {
			if (_myCalc == null) {
				try {
					// and create the loader
					_myCalc = (toteCalculation) _config.createExecutableExtension("class");
				} catch (final CoreException e) {
					TotePlugin.logError(IStatus.ERROR, "Failed to create instance of loader:" + _config, e);
				}
			}
		}

		@Override
		public String getTitle() {
			checkMe();
			return _myCalc.getTitle();
		}

		@Override
		public String getUnits() {
			checkMe();
			return _myCalc.getUnits();
		}

		@Override
		public boolean isWrappableData() {
			checkMe();
			return _myCalc.isWrappableData();
		}

		@Override
		public void setPattern(final NumberFormat format) {
			checkMe();
			_myCalc.setPattern(format);
		}

		@Override
		public String update(final Watchable primary, final Watchable secondary, final HiResDate thisTime) {
			checkMe();
			return _myCalc.update(primary, secondary, thisTime);
		}
	}

	private ArrayList<toteCalculation> _loaders;

	// Extension point tag and attributes in plugin.xml
	private final String EXTENSION_POINT_ID;

	private final String EXTENSION_TAG;

	private final String PLUGIN_ID;

	private final String EXTENSION_TAG_LABEL_ATTRIB = "name";

	public CalculationLoaderManager(final String extensionId, final String extensionTag, final String pluginId) {
		EXTENSION_POINT_ID = extensionId;
		EXTENSION_TAG = extensionTag;
		PLUGIN_ID = pluginId;

		getCalculations();
	}

	private void addToolActionDescriptor(final IConfigurationElement configElement) {
		final String label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);

		// get menu item label
		// search for double entries
		boolean doubleEntry = false;
		for (int i = 0; i < getToolActionDescriptors().size(); i++) {
			final String l = getToolActionDescriptors().get(i).getTitle();
			if (l.equals(label))
				doubleEntry = true;
		}

		// we take the first matching label
		if (!doubleEntry) {
			final toteCalculation newInstance = createInstance(configElement, label);
			getToolActionDescriptors().add(newInstance);
		} else {
			System.out.println("...failed! Reason: Label '" + label + "' already exists.  Check your plugin.xml");
		}

	}

	/**
	 * create one of our objects from the details supplied
	 *
	 * @param configElement
	 * @param label
	 * @return
	 */
	abstract public toteCalculation createInstance(IConfigurationElement configElement, String label);

	public toteCalculation[] findCalculations() {
		final toteCalculation[] template = new toteCalculation[] {};
		return _loaders.toArray(template);
	}

	private void getCalculations() {

		_loaders = new ArrayList<toteCalculation>();
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
			System.out.println("* No configuration found!");
		}

	}

	private ArrayList<toteCalculation> getToolActionDescriptors() {
		return _loaders;
	}

}
