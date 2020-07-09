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

package org.mwc.cmap.core.property_support;

import java.util.HashMap;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;

public class FontHelper extends EditorHelper {

	public static class FontDataDialogCellEditor extends DialogCellEditor {

		public FontDataDialogCellEditor(final Composite parent) {
			super(parent);
		}

		@Override
		protected Object openDialogBox(final Control cellEditorWindow) {
			Font res = null;
			final FontDialog ftDialog = new FontDialog(cellEditorWindow.getShell());
			final Font thisFont = (Font) getValue();
			if (thisFont != null) {
				final FontData[] list = thisFont.getFontData();
				ftDialog.setFontList(list);
			}
			final FontData fData = ftDialog.open();
			if (fData != null) {
				res = new Font(Display.getCurrent(), fData);

			}

			return res;
		}

		@Override
		protected void updateContents(final Object value) {
			getDefaultLabel().setText(fontToText((Font) value));
		}

	}

	/**
	 * a store of java fonts cross-referenced against swt ones
	 *
	 */
	private static HashMap<java.awt.Font, Font> _myFontList;

	/**
	 * a font registry - to reduce usage/creation of SWT fonts
	 *
	 */
	private static FontRegistry _fontRegistry;

	/**
	 * a store of java fonts, cross-referenced against the font description
	 *
	 */
	protected static HashMap<FontData, java.awt.Font> _awtFonts;

	/**
	 * create an swt font that looks like this java awt one
	 *
	 * @param javaFont
	 * @return
	 */
	public static org.eclipse.swt.graphics.Font convertFontFromAWT(final java.awt.Font javaFont) {

		// check we have our registry
		if (_fontRegistry == null)
			_fontRegistry = new FontRegistry(Display.getCurrent(), true);

		if (_myFontList == null)
			_myFontList = new HashMap<java.awt.Font, Font>();

		// see if we've got the font in our local list
		org.eclipse.swt.graphics.Font thisFont = _myFontList.get(javaFont);

		// did we find it?
		if (thisFont == null) {
			// nope, better go and get it then

			// get the font as a string - we use that as an index
			final String fontName = javaFont.toString();

			// do we have a font for this style?
			if (!_fontRegistry.hasValueFor(fontName)) {
				// bugger, we'll have to create it

				final int size = javaFont.getSize();
				final int style = javaFont.getStyle();
				String name = javaFont.getName();

				// WORKAROUND
				// - on windows, our 'sans serif' recorded in the xml file doesn't get
				// translated to Arial. So, we do it by hand
				if (name.equals("Sans Serif"))
					name = "Arial";

				// create an SWT font data entity to describe this font
				final FontData newF = new FontData(name, size, style);

				// and store the font in the registry
				_fontRegistry.put(fontName, new FontData[] { newF });
			}

			// ok, we can now try to retrieve this font (if the font data wasn't there
			// before,
			// it will be now. If we really don't have it, we'll just get a default one any
			// way. cool.
			thisFont = _fontRegistry.get(fontName);

			// and store the Java font against the SWT one
			_myFontList.put(javaFont, thisFont);
		}

		return thisFont;
	}

	public static java.awt.Font convertFontFromSWT(final org.eclipse.swt.graphics.Font swtFont) {
		// ok, convert the AWT color to SWT
		java.awt.Font res = null;
		final FontData fd = swtFont.getFontData()[0];

		if (_awtFonts == null)
			_awtFonts = new HashMap<FontData, java.awt.Font>();

		// do we already hold this font?
		res = _awtFonts.get(fd);
		if (res == null) {
			// nope, better create it
			res = new java.awt.Font(fd.getName(), fd.getStyle(), fd.getHeight());

			// and remember it
			_awtFonts.put(fd, res);
		}

		return res;
	}

	private static String fontToText(final Font font) {

		if (font == null) {
			return "";
		}
		final FontData[] datas = font.getFontData();

		final FontData data = datas[0];

		// sort out the font family
		String res = data.getName();

		// is it bold?
		if (SWT.BOLD == (data.getStyle() & SWT.BOLD))
			res += ", Bold";

		// is it italic?
		if (SWT.ITALIC == (data.getStyle() & SWT.ITALIC))
			res += ", Italic";

		// and finish the line off with the height
		res += ", " + data.getHeight();
		return res;
	}

	public static org.eclipse.swt.graphics.Font getFont(final FontData fd) {
		final org.eclipse.swt.graphics.Font res = null;

		return res;
	}

	public FontHelper() {
		super(java.awt.Font.class);
	}

	@Override
	public CellEditor getCellEditorFor(final Composite parent) {
		final CellEditor editor = new FontDataDialogCellEditor(parent);
		return editor;
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
				return fontToText((Font) element);
			}

		};
		return label1;
	}

	@Override
	public Object translateFromSWT(final Object value) {
		// ok, convert the AWT color to SWT
		final Font font = (Font) value;
		return convertFontFromSWT(font);
	}

	@Override
	public Object translateToSWT(final Object value) {
		// ok, convert the AWT color to SWT
		final java.awt.Font col = (java.awt.Font) value;
		return convertFontFromAWT(col);
	}

}