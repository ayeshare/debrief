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

package org.mwc.debrief.track_shift;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.DebriefToolParent;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class TrackShiftActivator extends AbstractUIPlugin {

	public static String PLUGIN_ID = "org.mwc.debrief.track_shift";

	// The shared instance.
	private static TrackShiftActivator plugin;

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static TrackShiftActivator getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.debrief.track_shift", path);
	}

	/**
	 * The constructor.
	 */
	public TrackShiftActivator() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		System.out.println("Started track shift");
		super.start(context);
		
		// register as a preference helper
		DebriefToolParent.registerPreferenceHelper(PLUGIN_ID);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		System.out.println("Stopping track shift");
		plugin = null;
		super.stop(context);
	}
}
