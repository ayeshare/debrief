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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Tools.Palette.CreateTOPO;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ETOPOPrefsPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constant definitions for plug-in preferences
	 */
	public static class PreferenceConstants {
		public static final String ETOPO_FILE = CreateTOPO.ETOPO_PATH;
		public static final String TRANSPARENCY = CreateTOPO.ETOPO_TRANSPARENCY;
	}

	public ETOPOPrefsPage() {
		super("ETOPO library data", CorePlugin.getImageDescriptor("icons/large_vpf.gif"), GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Location of ETOPO data-file");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		addField(new FileFieldEditor(PreferenceConstants.ETOPO_FILE, "&ETOPO data file location:", true,
				getFieldEditorParent()));
		addField(new ScaleFieldEditor(PreferenceConstants.TRANSPARENCY,
				"ETOPO backdrop transparency (0=transparent, 255=solid):", getFieldEditorParent(), 0, 255, 1, 10));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		//
	}

}