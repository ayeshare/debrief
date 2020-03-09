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

package org.mwc.cmap.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import MWC.GUI.Editable;
import MWC.GUI.Chart.Painters.SpatialRasterPainter.KeyLocationPropertyEditor;
import MWC.GUI.Tools.Palette.CreateTOPO;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class ETOPOWizardPage extends CoreEditableWizardPage {

	/**
	 * Constructor for SampleNewWizardPage.
	 *
	 * @param pageName
	 */
	public ETOPOWizardPage(final ISelection selection) {
		super(selection, "etopoPage", "Add Gridded depth data",
				"This page adds a 2-minute resolution gridded depth layer to your plot", "images/etopo_wizard.gif",
				null);
	}

	@Override
	protected Editable createMe() {
		if (_editable == null)
			_editable = CreateTOPO.load2MinBathyData();

		return _editable;
	}

	/**
	 * @return
	 */
	@Override
	protected PropertyDescriptor[] getPropertyDescriptors() {
		final java.beans.PropertyDescriptor[] descriptors = {
				longProp("KeyLocation", "the current location of the color-key", getEditable(),
						KeyLocationPropertyEditor.class),
				prop("ShowLand", "whether to shade land-data", getEditable()),
				prop("BathyVisible", "whether to show the gridded contours", getEditable()),
				prop("ContoursVisible", "whether to show the contours", getEditable()), };
		return descriptors;
	}

	/**
	 * do we have valid data?
	 *
	 * @return
	 */
	public boolean isAvailable() {
		return CreateTOPO.check2MinBathyData();
	}

}