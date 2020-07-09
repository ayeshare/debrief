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

package org.mwc.debrief.core.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;
import org.mwc.debrief.core.DebriefPlugin;

import MWC.GUI.Editable;

public class DebriefImageHelper implements ViewLabelImageHelper {

	@Override
	public ImageDescriptor getImageFor(final Editable editable) {
		ImageDescriptor res = null;
		final Debrief.GUI.DebriefImageHelper helper = new Debrief.GUI.DebriefImageHelper();
		final String icon = helper.getImageFor(editable);
		if (icon != null) {
			res = DebriefPlugin.getImageDescriptor(icon);
		}
		return res;
	}

}
