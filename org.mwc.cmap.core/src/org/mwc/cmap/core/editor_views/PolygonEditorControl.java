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

package org.mwc.cmap.core.editor_views;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mwc.cmap.core.CorePlugin;

public abstract class PolygonEditorControl extends org.eclipse.swt.widgets.Composite implements SelectionListener {
	private Composite topHolder;
	private Composite btnHolder;
	public ListViewer pointList2;
	public Label editorPanel;
	public Button newBtn;
	public Button delBtn;
	public Button pasteBtn;
	public Button upBtn;
	public Label helpLbl;

	// /**
	// * Auto-generated main method to display this
	// * org.eclipse.swt.widgets.Composite inside a new Shell.
	// */
	// public static void main(String[] args) {
	// showGUI();
	// }

	// /**
	// * Auto-generated method to display this
	// * org.eclipse.swt.widgets.Composite inside a new Shell.
	// */
	// public static void showGUI() {
	// Display display = Display.getDefault();
	// Shell shell = new Shell(display);
	// PolygonEditorControl inst = new PolygonEditorControl(shell, SWT.NULL);
	// Point size = inst.getSize();
	// shell.setLayout(new FillLayout());
	// shell.layout();
	// if(size.x == 0 && size.y == 0) {
	// inst.pack();
	// shell.pack();
	// } else {
	// Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
	// shell.setSize(shellBounds.width, shellBounds.height);
	// }
	// shell.open();
	// while (!shell.isDisposed()) {
	// if (!display.readAndDispatch())
	// display.sleep();
	// }
	// }

	public PolygonEditorControl(final org.eclipse.swt.widgets.Composite parent, final int style) {
		super(parent, style);
		initGUI();
		{
		}
		{
			// ok, sort out the images
			pasteBtn.setImage(CorePlugin.getImageFromRegistry("paste.png"));
			upBtn.setImage(CorePlugin.getImageFromRegistry("Up.gif"));
			newBtn.setImage(CorePlugin.getImageFromRegistry("NewPin.gif"));
			delBtn.setImage(CorePlugin.getImageFromRegistry("DeletePin.gif"));
		}
	}

	private void initGUI() {
		try {
			this.setLayout(new GridLayout());
			{
				topHolder = new Composite(this, SWT.NONE);
				final FillLayout topHolderLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
				final GridData topHolderLData = new GridData();
				topHolderLData.horizontalAlignment = GridData.FILL;
				topHolderLData.grabExcessHorizontalSpace = true;
				topHolder.setLayoutData(topHolderLData);
				topHolder.setLayout(topHolderLayout);
				{
					helpLbl = new Label(topHolder, SWT.WRAP);

					helpLbl.setText("helpTxt");
				}
				{
					btnHolder = new Composite(topHolder, SWT.NONE);
					final GridLayout btnHolderLayout = new GridLayout();
					btnHolderLayout.makeColumnsEqualWidth = true;
					btnHolderLayout.numColumns = 2;
					btnHolder.setLayout(btnHolderLayout);
					{
						upBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						final GridData upBtnLData = new GridData();
						upBtnLData.horizontalAlignment = GridData.FILL;
						upBtnLData.grabExcessHorizontalSpace = true;
						upBtn.setLayoutData(upBtnLData);
						upBtn.setText("Up");
						upBtn.setToolTipText("Move point up order");
						upBtn.addSelectionListener(this);
					}
					{
						pasteBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						final GridData downBtnLData = new GridData();
						downBtnLData.horizontalAlignment = GridData.FILL;
						downBtnLData.grabExcessHorizontalSpace = true;
						pasteBtn.setLayoutData(downBtnLData);
						pasteBtn.setText("Paste");
						pasteBtn.setToolTipText("Paste location from clipboard");
						pasteBtn.addSelectionListener(this);
					}
					{
						newBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						final GridData newBtnLData = new GridData();
						newBtnLData.horizontalAlignment = GridData.FILL;
						newBtnLData.grabExcessHorizontalSpace = true;
						newBtn.setLayoutData(newBtnLData);
						newBtn.setText("New");
						newBtn.setToolTipText("Add new point");
						newBtn.addSelectionListener(this);
					}
					{
						delBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						final GridData DelBtnLData = new GridData();
						DelBtnLData.horizontalAlignment = GridData.FILL;
						DelBtnLData.grabExcessHorizontalSpace = true;
						delBtn.setLayoutData(DelBtnLData);
						delBtn.setText("Delete");
						delBtn.setToolTipText("Delete current point");
						delBtn.addSelectionListener(this);
					}
				}
			}
			{
				final GridData pointList2LData = new GridData();
				pointList2LData.grabExcessHorizontalSpace = true;
				pointList2LData.horizontalAlignment = GridData.FILL;
				pointList2LData.verticalAlignment = GridData.FILL;
				pointList2LData.grabExcessVerticalSpace = true;
				pointList2 = new ListViewer(this, SWT.SINGLE);
				pointList2.getControl().setLayoutData(pointList2LData);
			}
			{
				editorPanel = new Label(this, SWT.NONE);
				final GridData editorPanelLData = new GridData();
				editorPanelLData.horizontalAlignment = GridData.FILL;
				editorPanelLData.grabExcessHorizontalSpace = true;
				editorPanelLData.verticalAlignment = GridData.END;
				editorPanel.setLayoutData(editorPanelLData);
				editorPanel.setText("here goes the point editor details");
			}
			this.layout();
		} catch (final Exception e) {
			CorePlugin.logError(IStatus.ERROR, "Problem layout out Polygon editor gui", e);

		}
	}

}
