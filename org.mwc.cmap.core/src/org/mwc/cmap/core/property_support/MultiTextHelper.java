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
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MultiTextHelper extends EditorHelper {

	static class MultiLineInputDialog extends Dialog {

		public Text mContentText;
		public String mContent;

		public MultiLineInputDialog(final Shell parentShell, final String content) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.NO_TRIM);
			setBlockOnOpen(true);
			this.mContent = content;
		}

		@Override
		protected void buttonPressed(final int buttonId) {
			if (buttonId == IDialogConstants.OK_ID) {
				this.mContent = mContentText.getText();
			}
			super.buttonPressed(buttonId);
		}

		@Override
		protected Control createDialogArea(final Composite parent) {
			final Composite container = (Composite) super.createDialogArea(parent);
			container.setLayout(new FillLayout(SWT.HORIZONTAL));

			mContentText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			mContentText.setText(mContent);

			// hide scrollbars when not necessary
			final Listener scrollBarListener = new Listener() {
				@Override
				public void handleEvent(final Event event) {
					final Text t = (Text) event.widget;
					final Rectangle r1 = t.getClientArea();
					final Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
					final Point p = t.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
					t.getHorizontalBar().setVisible(r2.width <= p.x);
					t.getVerticalBar().setVisible(r2.height <= p.y);
					if (event.type == SWT.Modify) {
						t.getParent().layout(true);
						t.showSelection();
					}
				}
			};
			mContentText.addListener(SWT.Resize, scrollBarListener);
			mContentText.addListener(SWT.Modify, scrollBarListener);

			return container;
		}

		@Override
		protected Point getInitialSize() {
			return new Point(300, 150);
		}
	}

	public MultiTextHelper() {
		super(String.class);
	}

	@Override
	public CellEditor getCellEditorFor(final Composite parent) {
		return new DialogCellEditor(parent) {

			private Label text;

			@Override
			protected Control createContents(final Composite cell) {
				text = (Label) super.createContents(cell);
				return text;
			}

			@Override
			protected Object openDialogBox(final Control cellEditorWindow) {
				final MultiLineInputDialog dialog = new MultiLineInputDialog(text.getShell(), text.getText());
				dialog.create();
				final Point location = text.toDisplay(text.getLocation());
				location.x -= 2;
				// location.y +=text.getBounds().height;
				dialog.getShell().setLocation(location);
				dialog.open();
				return dialog.mContent;
			}
		};
	}
}
