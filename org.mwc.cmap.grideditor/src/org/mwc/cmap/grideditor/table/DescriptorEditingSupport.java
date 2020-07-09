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

package org.mwc.cmap.grideditor.table;

import java.text.ParseException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.grideditor.command.SetDescriptorValueOperation;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;
import org.mwc.cmap.gridharness.views.MultiControlCellEditor;

import MWC.GUI.TimeStampedDataItem;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class DescriptorEditingSupport extends EditingSupport {

	private final GriddableItemDescriptor myDescriptor;

	private final TableModel myTableModel;

	private boolean myNeedCastValueToStringForCellEditor;

	private EditableTarget myTabTraverseTarget;

	public DescriptorEditingSupport(final TableModel tableModel, final GriddableItemDescriptor descriptor) {
		super(tableModel.getViewer());
		myDescriptor = descriptor;
		myTableModel = tableModel;
	}

	@Override
	protected boolean canEdit(final Object element) {
		return element instanceof TimeStampedDataItem;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		final CellEditor cellEditor = getDescriptor().getEditor().getCellEditorFor(getCellEditorParent());
		if (myTabTraverseTarget != null) {
			Control traverseSubject = null;
			if (cellEditor instanceof MultiControlCellEditor) {
				traverseSubject = ((MultiControlCellEditor) cellEditor).getLastControl();
			}
			if (traverseSubject == null) {
				traverseSubject = cellEditor.getControl();
			}
			if (traverseSubject != null)
				traverseSubject.addTraverseListener(new CellEditorTraverseHandler(myTabTraverseTarget, element));
		}
		myNeedCastValueToStringForCellEditor = cellEditor instanceof TextCellEditor;
		return cellEditor;
	}

	protected final Composite getCellEditorParent() {
		final TableViewer viewer = (TableViewer) getViewer();
		return viewer.getTable();
	}

	public GriddableItemDescriptor getDescriptor() {
		return myDescriptor;
	}

	private IOperationHistory getOperationHistory() {
		return myTableModel.getUndoSupport().getOperationHistory();
	}

	private IUndoContext getUndoContext() {
		return myTableModel.getUndoSupport().getUndoContext();
	}

	@Override
	protected Object getValue(final Object element) {
		if (false == element instanceof TimeStampedDataItem) {
			return null;
		}
		final TimeStampedDataItem item = (TimeStampedDataItem) element;
		final Object rawValue = BeanUtil.getItemValue(item, getDescriptor());
		return transformToCellEditor(rawValue);
	}

	public void setTabTraverseTarget(final EditableTarget tabTraverseTarget) {
		myTabTraverseTarget = tabTraverseTarget;
	}

	@Override
	protected void setValue(final Object element, final Object value) {
		Object theValue = value;
		if (element instanceof TimeStampedDataItem) {
			final TimeStampedDataItem item = (TimeStampedDataItem) element;
			theValue = transformFromCellEditor(theValue);
			if (theValue != null && !theValue.equals(BeanUtil.getItemValue(item, myDescriptor))) {
				final GriddableSeries series = (GriddableSeries) getViewer().getInput();
				final OperationEnvironment environment = new OperationEnvironment(getUndoContext(), series, item,
						myDescriptor);
				final SetDescriptorValueOperation update = new SetDescriptorValueOperation(environment, theValue);
				try {
					getOperationHistory().execute(update, null, null);
				} catch (final ExecutionException e) {
					throw new RuntimeException("Can't set the value of :" + theValue + //
							" for descriptor " + myDescriptor.getTitle() + //
							" of item: " + item, e);
				}
			}
		}
	}

	protected Object transformFromCellEditor(final Object cellEditorValue) {
		if (!myNeedCastValueToStringForCellEditor) {
			return cellEditorValue;
		}
		if (false == cellEditorValue instanceof String) {
			return cellEditorValue;
		}
		if (!getDescriptor().getType().isPrimitive()) {
			return cellEditorValue;
		}
		final String stringValue = (String) cellEditorValue;
		final Class<?> descriptorType = getDescriptor().getType();
		try {
			if (double.class.equals(descriptorType)) {
				return MWCXMLReader.readThisDouble(stringValue);
			}
			if (int.class.equals(descriptorType)) {
				return Integer.valueOf(stringValue);
			}
			if (long.class.equals(descriptorType)) {
				return Long.valueOf(stringValue);
			}
			if (float.class.equals(descriptorType)) {
				return Float.valueOf(stringValue);
			}
			if (short.class.equals(descriptorType)) {
				return Short.valueOf(stringValue);
			}
			if (byte.class.equals(descriptorType)) {
				return Byte.valueOf(stringValue);
			}
			throw new UnsupportedOperationException("Primitive type is not suported: " + descriptorType);
		} catch (final NumberFormatException e) {
			return null;
		} catch (final ParseException pe) {
			return null;
		}
	}

	protected Object transformToCellEditor(final Object value) {
		return (myNeedCastValueToStringForCellEditor) ? String.valueOf(value) : value;
	}
}
