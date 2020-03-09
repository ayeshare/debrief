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
/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2016, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.cmap.naturalearth.preferences;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.mwc.cmap.naturalearth.Activator;

public class NaturalEarhPrefs extends PreferencePage implements IWorkbenchPreferencePage {

	class StylesLabelProvider extends LabelProvider {

		@Override
		public Image getImage(final Object element) {
			return null;
		}

	}

	public static final String ID = "org.mwc.cmap.naturalearth.preferences.NaturalEarhPrefs";
	private Text dataFolderText;

	private Button memoryMapButton;

	public NaturalEarhPrefs() {
		super("Natural Earth");
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		composite.setLayoutData(gd);
		final GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		final Label dataFolderLabel = new Label(composite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		dataFolderLabel.setLayoutData(gd);
		dataFolderLabel.setText("Data Folder:");

		dataFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		dataFolderText.setLayoutData(gd);
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		final String dataFolderValue = store.getString(PreferenceConstants.DATA_FOLDER);
		dataFolderText.setText(dataFolderValue == null ? "" : dataFolderValue);

		final Button dataFolderBrowse = new Button(composite, SWT.PUSH);
		dataFolderBrowse.setText("Browse...");
		dataFolderBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SINGLE);
				String value = dataFolderText.getText();
				if (value.trim().length() == 0) {
					value = Platform.getLocation().toOSString();
				}
				dialog.setFilterPath(value);

				final String result = dialog.open();
				if (result == null || result.trim().length() == 0) {
					return;
				}
				dataFolderText.setText(result);

			}

		});

		memoryMapButton = new Button(composite, SWT.CHECK);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 3;
		memoryMapButton.setLayoutData(gd);
		memoryMapButton.setText("Use memory map file");
		final boolean selected = store.getBoolean(PreferenceConstants.MEMORY_MAPPED);
		memoryMapButton.setSelection(selected);

		final Link link = new Link(composite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 3;
		link.setLayoutData(gd);
		link.setText("Natural Earth data trimmed to suit Debrief's Mercator projection is available online from here:\n"
				+ "<a>https://github.com/debrief/NaturalEarth</a>");
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				try {
					final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
					final IWebBrowser browser = support.getExternalBrowser();
					browser.openURL(new URL("https://github.com/debrief/NaturalEarth"));
				} catch (final Exception e) {
					Activator.logError(IStatus.WARNING, "Cannot open an external browser", e);
				}
			}
		});

		return composite;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}

	@Override
	protected void performDefaults() {
		dataFolderText.setText(""); //$NON-NLS-1$
		memoryMapButton.setSelection(false);
		storePreferences();
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		storePreferences();
		return super.performOk();
	}

	private void storePreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		final String value = dataFolderText.getText();
		if (value != null) {
			store.setValue(PreferenceConstants.DATA_FOLDER, value);
		}
		final boolean selected = memoryMapButton.getSelection();
		store.setValue(PreferenceConstants.MEMORY_MAPPED, selected);
	}

}