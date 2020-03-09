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
package org.mwc.debrief.core.contenttype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.content.IContentDescription;
import org.mwc.cmap.core.CorePlugin;

public class AISContentDescriber extends TextContentDescriber {

	@Override
	public int describe(final InputStream contents, final IContentDescription description) throws IOException {
		BufferedReader r = null;
		int res = INVALID;
		try {
			r = new BufferedReader(new InputStreamReader(contents));
			final String firstLine = r.readLine();
			if ((firstLine != null) && (firstLine.contains("!AIVDM"))) {
				res = VALID;
			} else {
				// just double-check that it's invalid
				res = INVALID;
			}
		} catch (final Exception e) {
			CorePlugin.logError(IStatus.ERROR, "AIS content type error", e);
		} finally {
			try {
				if (r != null)
					r.close();
			} catch (final IOException e) {
				CorePlugin.logError(IStatus.ERROR, "Couldn't close file file", e);
			}
		}
		return res;
	}

}
