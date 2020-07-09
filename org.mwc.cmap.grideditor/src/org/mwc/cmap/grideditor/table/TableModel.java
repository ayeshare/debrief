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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TableColumn;
import org.mwc.cmap.grideditor.GridEditorUndoSupport;
import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.gridharness.data.FormatDateTime;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptorExtension;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;

public class TableModel {

	public abstract static class ColumnBase {

		private final TableModel myModel;

		public ColumnBase(final TableModel model) {
			myModel = model;
		}

		protected TableViewerColumn createColumn(final String title) {
			return createColumn(title, title);
		}

		protected TableViewerColumn createColumn(final String title, final String sampleTextHint) {
			final TableViewerColumn result = new TableViewerColumn(getViewer(), SWT.NONE);
			final TableColumn column = result.getColumn();
			column.setText(title);
			final Point hintWidth = hintMeasureText(sampleTextHint);
			hintWidth.x += 2 * COLUMN_WIDTH_PADDING;
			column.setWidth(hintWidth.x);
			column.setResizable(true);
			column.setMoveable(false);
			return result;
		}

		/**
		 * @return associated {@link GriddableItemDescriptor} for this column or
		 *         <code>null</code> if there are no (i.p., for an implicit date/time
		 *         column)
		 */
		public abstract GriddableItemDescriptor getDescriptor();

		public abstract ILabelProvider getLabelProvider(Object element);

		protected TableModel getModel() {
			return myModel;
		}

		public abstract TableViewerColumn getTableViewerColumn();

		protected TableViewer getViewer() {
			return myModel.getViewer();
		}

		protected final Point hintMeasureText(final String text) {
			final GC gc = new GC(getViewer().getControl().getDisplay());
			gc.setFont(getViewer().getControl().getFont());
			try {
				return gc.textExtent(text);
			} finally {
				gc.dispose();
			}
		}

		public abstract boolean isFixed();
	}

	private static class DateTimeColumn extends ColumnBase {

		/**
		 * The first column is a table row selector, so the first meaningful index is 1
		 */
		public static final int DATE_TIME_COLUMN_INDEX = 1;

		/**
		 * Name of the implicit first column (that shows the {@link HiResDate})
		 */
		private static final String DATE_TIME_COLUMN_TITLE = "Date Time";

		/**
		 * Label text for any object that is not a {@link HiResDate}
		 */
		private static final String NOT_A_DATE = "<not a date>";

		private LabelProvider myDateTimeLabelProvider;

		private final TableViewerColumn myTableViewerColumn;

		private final boolean myAppendMilliseconds;

		public DateTimeColumn(final TableModel model, final boolean appendMilliseconds) {
			super(model);
			myAppendMilliseconds = appendMilliseconds;
			final String SAMPLE_DATE = getSampleDateTimeLabel();
			myTableViewerColumn = createColumn(DATE_TIME_COLUMN_TITLE, SAMPLE_DATE);
			final DateTimeEditingSupportRich editingSupport = new DateTimeEditingSupportRich(model);
			editingSupport.setTabTraverseTarget(
					new SameElementDifferentColumnTarget(getViewer(), DATE_TIME_COLUMN_INDEX + 1));
			myTableViewerColumn.setEditingSupport(editingSupport);

			final DateTimeCellEditor shouldFit = new DateTimeCellEditor(getViewer().getTable());
			shouldFit.setValue(new Date());
			final Point shouldFitSize = shouldFit.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			myTableViewerColumn.getColumn()
					.setWidth(Math.max(myTableViewerColumn.getColumn().getWidth(), shouldFitSize.x));
			shouldFit.dispose();
		}

		/**
		 * @return <code>null</code> because this column had no associated descriptor.
		 */
		@Override
		public GriddableItemDescriptor getDescriptor() {
			return null;
		}

		@Override
		public ILabelProvider getLabelProvider(final Object element) {
			if (myDateTimeLabelProvider == null) {
				myDateTimeLabelProvider = new LabelProvider() {

					@Override
					public String getText(final Object element) {
						HiResDate date = null;
						if (element instanceof HiResDate) {
							date = date;
						}
						if (element instanceof TimeStampedDataItem) {
							date = ((TimeStampedDataItem) element).getDTG();
						}
						if (date == null) {
							return NOT_A_DATE;
						}
						final StringBuffer result = new StringBuffer();
						result.append(FormatDateTime.toString(date.getDate().getTime()));
						if (myAppendMilliseconds) {
							result.append(" (");
							result.append(date.getDate().getTime());
							result.append(" ms)");
						}
						return result.toString();
					}
				};
			}
			return myDateTimeLabelProvider;
		}

		private String getSampleDateTimeLabel() {
			final HiResDate now = new HiResDate(new Date());
			return getLabelProvider(now).getText(now);
		}

		@Override
		public TableViewerColumn getTableViewerColumn() {
			return myTableViewerColumn;
		}

		@Override
		public boolean isFixed() {
			return true;
		}

	}

	/**
	 * Couples the {@link TableViewerColumn} with underlying
	 * {@link GriddableItemDescriptor}.
	 */
	private static class DescriptorBasedColumn extends ColumnBase {

		/**
		 * Label text for any object that is not a {@link TimeStampedDataItem}
		 */
		private static final String UNKNOWN = "<unknown>";

		private final GriddableItemDescriptor myDescriptor;

		private final TableViewerColumn myTableViewerColumn;

		private LabelProvider myLabelProvider;

		public DescriptorBasedColumn(final TableModel model, final GriddableItemDescriptor descriptor,
				final int columnIndex, final boolean isLastColumn) {
			super(model);
			myDescriptor = descriptor;
			String widthEstimator = descriptor.getTitle();
			if (descriptor instanceof GriddableItemDescriptorExtension) {
				final String dataSample = ((GriddableItemDescriptorExtension) descriptor).getSampleString();
				if (dataSample != null && dataSample.length() > widthEstimator.length()) {
					widthEstimator = dataSample;
				}
			}
			myTableViewerColumn = createColumn(descriptor.getTitle(), widthEstimator);
			final DescriptorEditingSupport editingSupport = new DescriptorEditingSupport(model, myDescriptor);
			if (isLastColumn) {
				editingSupport.setTabTraverseTarget(new NextElementFirstColumnTarget(getViewer(), true));
			} else {
				editingSupport.setTabTraverseTarget(new SameElementDifferentColumnTarget(getViewer(), columnIndex + 1));
			}
			myTableViewerColumn.setEditingSupport(editingSupport);
		}

		private LabelProvider createChartableLabelProvider() {
			return new LabelProvider() {

				@Override
				public String getText(final Object element) {
					final String res;
					if (false == element instanceof TimeStampedDataItem) {
						res = UNKNOWN;
					} else {
						final TimeStampedDataItem dataItem = (TimeStampedDataItem) element;
						final Object value = BeanUtil.getItemValue(dataItem, getDescriptor());
						res = String.valueOf(value);
					}
					return res;
				}
			};
		}

		@Override
		public GriddableItemDescriptor getDescriptor() {
			return myDescriptor;
		}

		@Override
		public ILabelProvider getLabelProvider(final Object element) {
			// XXX: the line below does not work -- there are no meaningful
			// implementation of the label providers in actual descriptors
			// return getDescriptor().getEditor().getLabelFor(element);

			// So, for now we will use the double value which is already used for
			// charting
			if (myLabelProvider == null) {
				myLabelProvider = createChartableLabelProvider();
			}

			// if chart data is not available, just give up
			return myLabelProvider != null ? myLabelProvider : getDescriptor().getEditor().getLabelFor(element);
		}

		@Override
		public TableViewerColumn getTableViewerColumn() {
			return myTableViewerColumn;
		}

		@Override
		public boolean isFixed() {
			return false;
		}

	}

	private static class NextElementFirstColumnTarget implements EditableTarget {

		private final ColumnViewer myViewer;

		private final boolean myCreateOnDemand;

		public NextElementFirstColumnTarget(final ColumnViewer viewer, final boolean createOnDemand) {
			myViewer = viewer;
			myCreateOnDemand = createOnDemand;
		}

		private Object cloneActualItem(final GriddableSeries series, final TimeStampedDataItem lastItem) {
			final TimeStampedDataItem copy = series.makeCopy(lastItem);
			series.insertItem(copy);
			return copy;
		}

		@Override
		public int getColumnIndex() {
			return DateTimeColumn.DATE_TIME_COLUMN_INDEX;
		}

		@Override
		public ColumnViewer getColumnViewer() {
			return myViewer;
		}

		@Override
		public Object getElementToEdit(final Object actuallyEdited) {
			if (getColumnViewer().getInput() instanceof GriddableSeries
					&& actuallyEdited instanceof TimeStampedDataItem) {
				final GriddableSeries series = (GriddableSeries) getColumnViewer().getInput();
				final TimeStampedDataItem actualItem = (TimeStampedDataItem) actuallyEdited;
				if (isLastItem(series, actualItem)) {
					return myCreateOnDemand ? cloneActualItem(series, actualItem) : null;
				} else {
					final ListIterator<TimeStampedDataItem> items = series.getItems().listIterator();
					while (items.hasNext()) {
						final TimeStampedDataItem next = items.next();
						if (next == actualItem) {
							break;
						}
					}
					return items.hasNext() ? items.next() : null;
				}
			}
			return null;
		}

		private boolean isLastItem(final GriddableSeries series, final TimeStampedDataItem item) {
			if (series.getItems().isEmpty()) {
				return false;
			}
			final List<TimeStampedDataItem> allItems = series.getItems();
			return allItems.get(allItems.size() - 1) == item;
		}

	}

	private static class RowSelectorColumn extends ColumnBase {

		private final TableViewerColumn myEmptyColumn;

		private final LabelProvider myLabelProvider;

		public RowSelectorColumn(final TableModel tableModel) {
			super(tableModel);
			myEmptyColumn = createColumn("");
			myLabelProvider = new LabelProvider() {

				@Override
				public String getText(final Object element) {
					return "";
				}
			};
		}

		@Override
		public GriddableItemDescriptor getDescriptor() {
			return null;
		}

		@Override
		public ILabelProvider getLabelProvider(final Object arg0) {
			return myLabelProvider;
		}

		@Override
		public TableViewerColumn getTableViewerColumn() {
			return myEmptyColumn;
		}

		@Override
		public boolean isFixed() {
			return true;
		}
	}

	private static class SameElementDifferentColumnTarget implements EditableTarget {

		private final int myColumnIndex;

		private final ColumnViewer myViewer;

		public SameElementDifferentColumnTarget(final ColumnViewer viewer, final int columnIndex) {
			myViewer = viewer;
			myColumnIndex = columnIndex;
		}

		@Override
		public int getColumnIndex() {
			return myColumnIndex;
		}

		@Override
		public ColumnViewer getColumnViewer() {
			return myViewer;
		}

		@Override
		public Object getElementToEdit(final Object actuallyEdited) {
			return actuallyEdited;
		}
	}

	/**
	 * This constant is used to determine the padding (both left and right) for the
	 * table column title, while determining the default column width.
	 */
	private static final int COLUMN_WIDTH_PADDING = 15;

	private GriddableSeries mySeries;

	private ColumnBase myDateTimeColumn;

	private ColumnBase myRowSelectorColumn;

	private final List<ColumnBase> myAllColumns = new ArrayList<ColumnBase>(10);

	private final TableViewer myViewer;

	private final GridEditorUndoSupport myUndoSupport;

	public TableModel(final TableViewer viewer, final GridEditorUndoSupport undoSupport) {
		myViewer = viewer;
		myUndoSupport = undoSupport;
	}

	private void createColumns(final boolean createFixedCOlumns) {
		getViewer().getTable().setRedraw(false);
		try {
			if (createFixedCOlumns) {
				myAllColumns.add(myRowSelectorColumn = new RowSelectorColumn(this));
				myAllColumns.add(myDateTimeColumn = new DateTimeColumn(this, false));
			}
			final GriddableItemDescriptor[] allAttributes = mySeries.getAttributes();
			if (allAttributes != null)
				for (int i = 0; i < allAttributes.length; i++) {
					final boolean isLast = (i == allAttributes.length - 1);
					myAllColumns.add(new DescriptorBasedColumn(this, allAttributes[i], myAllColumns.size(), isLast));
				}
		} finally {
			getViewer().getTable().setRedraw(true);
		}
	}

	public ColumnBase findColumnData(final TableColumn tableColumn) {
		for (final ColumnBase next : myAllColumns) {
			if (next.getTableViewerColumn().getColumn() == tableColumn) {
				return next;
			}
		}
		return null;
	}

	public ColumnBase getColumnData(final int columnIndex) {
		if (columnIndex >= myAllColumns.size()) {
			return null;
		}
		return myAllColumns.get(columnIndex);
	}

	public GridEditorUndoSupport getUndoSupport() {
		return myUndoSupport;
	}

	public TableViewer getViewer() {
		return myViewer;
	}

	private void removeColumns(final boolean removeFixedColumns) {
		getViewer().getTable().setRedraw(false);
		try {
			for (final Iterator<ColumnBase> columns = myAllColumns.iterator(); columns.hasNext();) {
				final ColumnBase next = columns.next();
				if (!removeFixedColumns && next.isFixed()) {
					continue;
				}
				next.getTableViewerColumn().getColumn().dispose();
				if (next == myDateTimeColumn) {
					myDateTimeColumn = null;
				}
				if (next == myRowSelectorColumn) {
					myRowSelectorColumn = null;
				}
				columns.remove();
			}
		} finally {
			getViewer().getTable().setRedraw(true);
		}
	}

	public void setInputSeries(final GriddableSeries newSeries) {
		removeColumns(newSeries == null);
		mySeries = newSeries;
		if (newSeries != null) {
			createColumns(myDateTimeColumn == null);
		}
	}

}
