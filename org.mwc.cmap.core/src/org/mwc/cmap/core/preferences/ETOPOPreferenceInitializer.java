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

package org.mwc.cmap.core.preferences;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.CorePlugin;
import org.osgi.framework.Bundle;

public class ETOPOPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences ()
	 */
	@Override
	public void initializeDefaultPreferences() {

		// hmm, set the default location
		final Path etopoPath = new Path("data/ETOPO2.raw");

		URL fileURL = null;
		final Bundle staticBundle = Platform.getBundle("org.mwc.cmap.static_resources");
		if (staticBundle != null) {
			// and get the relative path compared to the Core Plugin
			fileURL = FileLocator.find(staticBundle, etopoPath, null);
		}

		if (fileURL != null) {
			// right, that's the relative location, switch to absolute path
			try {
				fileURL = FileLocator.toFileURL(fileURL);
			} catch (final IOException e) {
				CorePlugin.logError(IStatus.ERROR, "Unable to find ETOPO data-file", e);
			}

			// and store the default location
			final IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
			store.setDefault(ETOPOPrefsPage.PreferenceConstants.ETOPO_FILE, fileURL.getFile());
			store.setDefault(ETOPOPrefsPage.PreferenceConstants.TRANSPARENCY, "200");
		}
	}

}