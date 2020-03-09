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

package org.mwc.cmap.tote;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class TotePlugin extends AbstractUIPlugin {

	public static final String IMG_LOCATION = "icons/";
	public static final String IMG_SHOW_UNIT_COLUMN = IMG_LOCATION + "show_units.png";

	// The shared instance.
	private static TotePlugin plugin;

	/**
	 * Returns the shared instance.
	 */
	public static TotePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.cmap.tote", path);
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(final String key) {
		final ResourceBundle bundle = TotePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (final MissingResourceException e) {
			return key;
		}
	}

	/**
	 * error logging utility
	 *
	 * @param severity  the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *                  <code>INFO</code>, <code>WARNING</code>, or
	 *                  <code>CANCEL</code>
	 * @param message   a human-readable message, localized to the current locale
	 * @param exception a low-level exception, or <code>null</code> if not
	 *                  applicable
	 */
	public static void logError(final int severity, final String message, final Throwable exception) {
		final Status stat = new Status(severity, "org.mwc.cmap.tote", IStatus.OK, message, exception);
		getDefault().getLog().log(stat);
	}

	// Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public TotePlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.mwc.cmap.tote.TotePluginResources");
		} catch (final MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}
}
