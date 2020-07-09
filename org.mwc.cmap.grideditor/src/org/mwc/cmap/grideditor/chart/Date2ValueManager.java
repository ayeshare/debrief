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

package org.mwc.cmap.grideditor.chart;

import java.util.Date;

import org.eclipse.core.commands.ExecutionException;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.mwc.cmap.grideditor.GridEditorUndoSupport;
import org.mwc.cmap.grideditor.command.CompositeOperation;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.grideditor.command.SetDescriptorValueOperation;
import org.mwc.cmap.grideditor.command.SetTimeStampOperation;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;

public class Date2ValueManager implements ChartDataManager {

	private final String myTitle;

	private final GriddableItemDescriptor myDescriptor;

	private final GriddableItemChartComponent myValuesComponent;

	private final TimeSeriesWithDuplicates myDuplicatesWorkaround;

	private GriddableSeries myInput;

	private DataPointsDragTracker myDragTracker;

	public Date2ValueManager(final GriddableItemDescriptor descriptor,
			final GriddableItemChartComponent valuesComponent) {
		myDescriptor = descriptor;
		myValuesComponent = valuesComponent;
		myTitle = descriptor.getTitle();
		myDuplicatesWorkaround = new TimeSeriesWithDuplicates("the-only");
	}

	@Override
	public void attach(final JFreeChartComposite chartPanel) {
		final GridEditorUndoSupport undoSupport = chartPanel.getActionContext().getUndoSupport();
		if (undoSupport != null) {
			myDragTracker = new DataPointsDragTracker(chartPanel, true) {

				@Override
				protected void dragCompleted(final BackedChartItem item, final double finalX, final double finalY) {
					final OperationEnvironment environment = new OperationEnvironment(undoSupport.getUndoContext(),
							myInput, item.getDomainItem(), myDescriptor);
					final Date finalTime = new Date((long) finalX);
					final SetTimeStampOperation setTime = new SetTimeStampOperation(environment,
							new HiResDate(finalTime));
					final SetDescriptorValueOperation setValue = new SetDescriptorValueOperation(environment, finalY);
					final CompositeOperation update = new CompositeOperation("Applying changes from chart",
							undoSupport.getUndoContext());
					update.add(setTime);
					update.add(setValue);
					try {
						undoSupport.getOperationHistory().execute(update, null, null);
					} catch (final ExecutionException e) {
						throw new RuntimeException("[Chart]Can't set the timestamp of :" + finalTime + //
						" and/or value: " + finalY + //
						" for item " + item.getDomainItem(), e);
					}
				}
			};
			chartPanel.addChartMouseListener(myDragTracker);
		}
	}

	@Override
	public ValueAxis createXAxis() {
		final DateAxis result = new DateAxis(null);
		result.setTimeZone(TimeSeriesWithDuplicates.getDefaultTimeZone());
		return result;
	}

	@Override
	public ValueAxis createYAxis() {
		final NumberAxis result = new NumberAxis(null);
		result.setAutoRangeIncludesZero(false);
		return result;
	}

	@Override
	public void detach(final JFreeChartComposite chartPanel) {
		if (myDragTracker != null) {
			chartPanel.removeChartMouseListener(myDragTracker);
			myDragTracker = null;
		}
	}

	@Override
	public String getChartTitle() {
		return myTitle;
	}

	@Override
	public GriddableItemDescriptor getDescriptor() {
		return myDescriptor;
	}

	@Override
	public XYDataset getXYDataSet() {
		return myDuplicatesWorkaround.getDataSet();
	}

	@Override
	public void handleItemAdded(final int index, final TimeStampedDataItem addedItem) {
		myDuplicatesWorkaround.addDomainItem(addedItem, myValuesComponent.getDoubleValue(addedItem));
	}

	@Override
	public void handleItemChanged(final TimeStampedDataItem changedItem) {
		myDuplicatesWorkaround.updateDomainItem(changedItem, myValuesComponent.getDoubleValue(changedItem));
	}

	@Override
	public void handleItemDeleted(final TimeStampedDataItem deletedItem) {
		myDuplicatesWorkaround.removeDomainItem(deletedItem);
	}

	@Override
	public void setInput(final GriddableSeries input) {
		myInput = input;
		for (final TimeStampedDataItem nextItem : input.getItems()) {
			final double nextValue = myValuesComponent.getDoubleValue(nextItem);
			myDuplicatesWorkaround.addDomainItem(nextItem, nextValue);
		}
	}

}
