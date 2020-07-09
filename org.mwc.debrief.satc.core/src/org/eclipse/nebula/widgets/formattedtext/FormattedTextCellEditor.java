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

package org.eclipse.nebula.widgets.formattedtext;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * A CellEditor based on a FormattedText. It extends the TextCellEditor, adding
 * formatting capabilities based on an IFormatter. A formatter can be associated
 * with the editor with the setFormatter method. If not, a formatter is
 * automatically created at the first call of the setValue method, based on the
 * type of the value.
 */
public class FormattedTextCellEditor extends TextCellEditor {
	protected FormattedText formattedText;

	/**
	 * Creates a new formatted text cell editor parented under the given control.
	 *
	 * @param parent the parent control
	 */
	public FormattedTextCellEditor(final Composite parent) {
		super(parent);
	}

	/**
	 * Creates a new formatted text cell editor parented under the given control.
	 *
	 * @param parent the parent control
	 * @param style  the style bits
	 */
	public FormattedTextCellEditor(final Composite parent, final int style) {
		super(parent, style);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.TextCellEditor#createControl(org.eclipse.swt.
	 * widgets.Composite)
	 */
	@Override
	protected Control createControl(final Composite parent) {
		final Text text = (Text) super.createControl(parent);
		formattedText = new FormattedText(text);
		return text;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.TextCellEditor#doGetValue()
	 */
	@Override
	protected Object doGetValue() {
		return formattedText.getValue();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.TextCellEditor#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(final Object value) {
		formattedText.setValue(value);
	}

	/**
	 * Returns the FormattedText object used by this cell editor.
	 *
	 * @return FormattedText object
	 */
	public FormattedText getFormattedText() {
		return formattedText;
	}

	/**
	 * Sets the formatter that this cell editor must use to edit the value.
	 *
	 * @param formatter the formatter
	 */
	public void setFormatter(final ITextFormatter formatter) {
		formattedText.setFormatter(formatter);
	}
}
