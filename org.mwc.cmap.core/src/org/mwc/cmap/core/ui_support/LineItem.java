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

package org.mwc.cmap.core.ui_support;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.mwc.cmap.core.CorePlugin;

/**
 * class representing line of text which may be plased on the status bar
 *
 * @author ian.mayo
 *
 */
public class LineItem extends ControlContribution {
	Label label;

	final String _lastText;

	// " 00" + BriefFormatLocation.DEGREE_SYMBOL
	// + "00\'00.00\"N 000" + BriefFormatLocation.DEGREE_SYMBOL
	// + "00\'00.00\"W ";

	/**
	 * tooltip to show when hovering over panel
	 *
	 */
	private final String _tooltip;

	/**
	 * preferences dialog id to open when user double-clicks
	 *
	 */
	final String _prefId;

	/**
	 * constructor - get going
	 *
	 * @param id
	 */
	public LineItem(final String id, final String template, final String tooltip, final String prefId) {
		super(id);
		_prefId = prefId;
		_tooltip = tooltip;
		_lastText = template;
	}

	/**
	 * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createControl(final Composite parent) {
		final Composite wrapper = new Composite(parent, SWT.NONE);
		final RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = true;
		rowLayout.type = SWT.HORIZONTAL;
		wrapper.setLayout(rowLayout);

		if (label != null) {
			label.dispose();
			label = null;
		}

		label = new Label(wrapper, SWT.RIGHT | SWT.BORDER);
		label.setText(_lastText);
		label.setToolTipText(_tooltip);
		label.setSize(550, 20);
		if (_prefId != null) {
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(final MouseEvent e) {
					// do the parent bits
					super.mouseDoubleClick(e);

					// do our bits
					final Display dis = Display.getCurrent();
					final PreferenceDialog dial = PreferencesUtil.createPreferenceDialogOn(dis.getActiveShell(),
							_prefId, null, null);
					dial.open();
				}
			});

			// and the button to edit properties
			final Button button = new Button(wrapper, SWT.NONE);
			button.setSize(40, 20);
			button.setImage(CorePlugin.getImageFromRegistry("properties.png"));
			button.setToolTipText("Change formatting of this text");
			button.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
				}

				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					// do our bits
					final Display dis = Display.getCurrent();
					final PreferenceDialog dial = PreferencesUtil.createPreferenceDialogOn(dis.getActiveShell(),
							_prefId, null, null);
					dial.open();
				}

			});
		}

		return wrapper;
	}

	public boolean isDisposed() {
		boolean res = true;
		if (label != null)
			res = label.isDisposed();

		return res;
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

	/**
	 * put the duff text back into the label
	 *
	 */
	public void reset() {
		setText(_lastText);
	}

	public void setText(final String val) {
		if (label == null) {
		} else if (label.isDisposed()) {
		} else {
			label.setText(val);
		}

	}

}
