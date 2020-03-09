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

package MWC.GUI.Properties.Swing;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TableModelDecorator extends AbstractTableModel {
	/**
	*
	*/
	private static final long serialVersionUID = 1L;
//implements TableModel {
	protected TableModel model;

	public TableModelDecorator(final TableModel model) {
		if (model == null)
			throw new IllegalArgumentException("null models are not allowed");
		this.model = model;
	}

	@Override
	public void addTableModelListener(final TableModelListener l) {
		model.addTableModelListener(l);
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return model.getColumnClass(columnIndex);
	}

	@Override
	public int getColumnCount() {
		return model.getColumnCount();
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return model.getColumnName(columnIndex);
	}

	@Override
	public int getRowCount() {
		return model.getRowCount();
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		return model.getValueAt(rowIndex, columnIndex);
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return model.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public void removeTableModelListener(final TableModelListener l) {
		model.removeTableModelListener(l);
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		model.setValueAt(aValue, rowIndex, columnIndex);
	}
}
