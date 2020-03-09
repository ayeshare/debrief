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

package org.mwc.debrief.gndmanager;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.debrief.GNDManager"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
		final Activator singleton = getDefault();
		if (singleton != null) {
			final Status stat = new Status(severity, "org.mwc.debrief.GNDManager", IStatus.OK, message, exception);
			singleton.getLog().log(stat);
		}

		// also throw it to the console
		if (exception != null)
			exception.printStackTrace();
	}

	/**
	 * show a message to the user
	 *
	 * @param title
	 * @param message
	 */
	public static void showMessage(final String title, final String message) {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), title, message);
	}

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}
