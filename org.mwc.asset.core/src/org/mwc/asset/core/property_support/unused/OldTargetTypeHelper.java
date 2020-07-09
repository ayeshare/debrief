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

package org.mwc.asset.core.property_support.unused;

/**
 *
 */

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.EditorHelper;

import ASSET.Models.Decision.TargetType;

public class OldTargetTypeHelper extends EditorHelper {

	/**
	 * constructor..
	 *
	 */
	public OldTargetTypeHelper() {
		super(TargetType.class);
	}

	/**
	 * create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	@Override
	public CellEditor getCellEditorFor(final Composite parent) {
		return new OldTargetTypeCellEditor(parent);
	}

	@Override
	public ILabelProvider getLabelFor(final Object currentValue) {
		final ILabelProvider label1 = new LabelProvider() {
			@Override
			public Image getImage(final Object element) {
				return null;
			}

			@Override
			public String getText(final Object element) {
				final TargetType val = (TargetType) element;
				return val.toString();
			}

		};
		return label1;
	}
	//
	//
	// public Control getEditorControlFor(Composite parent, final DebriefProperty
	// property)
	// {
	// final Button myCheckbox = new Button(parent, SWT.CHECK);
	// myCheckbox.addSelectionListener(new SelectionAdapter(){
	// public void widgetSelected(SelectionEvent e)
	// {
	// Boolean val = new Boolean(myCheckbox.getSelection());
	// property.setValue(val);
	// }});
	// return myCheckbox;
	// }
}