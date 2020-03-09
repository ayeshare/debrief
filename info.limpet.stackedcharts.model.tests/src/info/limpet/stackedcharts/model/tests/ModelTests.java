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
package info.limpet.stackedcharts.model.tests;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Assert;
import org.junit.Test;

import info.limpet.stackedcharts.model.AxisDirection;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Zone;

/**
 * A JUnit Plug-in Test to demonstrate basic EMF operations, such as model
 * manipulaton, persistnce and event handling
 */
public class ModelTests {

	/**
	 * Helper class to test notifications
	 */
	private static class ChartNameChangeListener implements Adapter {

		private Notifier notifier;
		private boolean notified;

		@Override
		public Notifier getTarget() {
			return notifier;
		}

		@Override
		public boolean isAdapterForType(final Object type) {
			return ChartSet.class.equals(type);
		}

		public boolean isNotified() {
			return notified;
		}

		@Override
		public void notifyChanged(final Notification notification) {
			final int featureId = notification.getFeatureID(StackedchartsPackage.class);
			switch (featureId) {
			case StackedchartsPackage.CHART__NAME:
				notified = true;
			}
		}

		@Override
		public void setTarget(final Notifier newTarget) {
			this.notifier = newTarget;
		}

	}

	private ChartSet createModel() {
		final StackedchartsFactory factory = StackedchartsFactory.eINSTANCE;

		final ChartSet chartsSet = factory.createChartSet();

		// set the common x axis
		final IndependentAxis depthAxis = factory.createIndependentAxis();
		depthAxis.setName("Depth");
		chartsSet.setSharedAxis(depthAxis);

		// first chart
		final Chart tempChart = factory.createChart();
		tempChart.setName("Temperature & Salinity");
		chartsSet.getCharts().add(tempChart);

		final DependentAxis yAxis1 = factory.createDependentAxis();
		yAxis1.setName("Temperature");
		tempChart.getMinAxes().add(yAxis1);

		final Dataset temperatureVsDepth1 = factory.createDataset();
		temperatureVsDepth1.setName("Temp vs Depth");
		yAxis1.getDatasets().add(temperatureVsDepth1);

		DataItem item1 = factory.createDataItem();
		item1.setIndependentVal(1000);
		item1.setDependentVal(30);
		temperatureVsDepth1.getMeasurements().add(item1);

		item1 = factory.createDataItem();
		item1.setIndependentVal(2000);
		item1.setDependentVal(50);
		temperatureVsDepth1.getMeasurements().add(item1);

		item1 = factory.createDataItem();
		item1.setIndependentVal(3000);
		item1.setDependentVal(60);
		temperatureVsDepth1.getMeasurements().add(item1);

		// second axis/dataset on this first graph
		final DependentAxis yAxis2 = factory.createDependentAxis();
		yAxis2.setName("Salinity");
		tempChart.getMaxAxes().add(yAxis2);

		final Dataset salinityVsDepth1 = factory.createDataset();
		salinityVsDepth1.setName("Salinity Vs Depth");
		yAxis2.getDatasets().add(salinityVsDepth1);

		item1 = factory.createDataItem();
		item1.setIndependentVal(1000);
		item1.setDependentVal(3000);
		salinityVsDepth1.getMeasurements().add(item1);

		item1 = factory.createDataItem();
		item1.setIndependentVal(2000);
		item1.setDependentVal(5000);
		salinityVsDepth1.getMeasurements().add(item1);

		item1 = factory.createDataItem();
		item1.setIndependentVal(3000);
		item1.setDependentVal(9000);
		salinityVsDepth1.getMeasurements().add(item1);

		// create a second chart
		// first chart
		final Chart pressureChart = factory.createChart();
		pressureChart.setName("Pressure Gradient");
		chartsSet.getCharts().add(pressureChart);

		// have a go at an annotation on the x axis
		final IndependentAxis shared = chartsSet.getSharedAxis();
		final Marker marker = factory.createMarker();
		marker.setValue(1200);
		marker.setName("A marker");
		SelectiveAnnotation sel = factory.createSelectiveAnnotation();
		sel.setAnnotation(marker);
		shared.getAnnotations().add(sel);
		final Zone zone = factory.createZone();
		zone.setStart(2100);
		zone.setEnd(2500);
		zone.setName("A Zone");
		sel = factory.createSelectiveAnnotation();
		sel.setAnnotation(zone);
		shared.getAnnotations().add(sel);

		final DependentAxis pressureAxis = factory.createDependentAxis();
		pressureAxis.setName("Pressure");
		pressureChart.getMinAxes().add(pressureAxis);

		final Dataset pressureVsDepth = factory.createDataset();
		pressureVsDepth.setName("Pressure vs Depth");
		pressureAxis.getDatasets().add(pressureVsDepth);

		DataItem item = factory.createDataItem();
		item.setIndependentVal(1000);
		item.setDependentVal(400);
		pressureVsDepth.getMeasurements().add(item);

		item = factory.createDataItem();
		item.setIndependentVal(2000);
		item.setDependentVal(500);
		pressureVsDepth.getMeasurements().add(item);

		item = factory.createDataItem();
		item.setIndependentVal(3000);
		item.setDependentVal(100);
		pressureVsDepth.getMeasurements().add(item);

		// have a go at a scatter set
		ScatterSet scatter = factory.createScatterSet();
		scatter.setName("Pressure Markers");
		Datum datum = factory.createDatum();
		datum.setVal(1650d);
		scatter.getDatums().add(datum);
		datum = factory.createDatum();
		datum.setVal(1700d);
		scatter.getDatums().add(datum);
		datum = factory.createDatum();
		datum.setVal(1720d);
		scatter.getDatums().add(datum);
		datum = factory.createDatum();
		datum.setVal(1790d);
		scatter.getDatums().add(datum);
		SelectiveAnnotation sa = factory.createSelectiveAnnotation();
		sa.setAnnotation(scatter);
		sa.getAppearsIn().add(pressureChart);
		chartsSet.getSharedAxis().getAnnotations().add(sa);

		// and another one
		scatter = factory.createScatterSet();
		scatter.setName("Common Markers");
		datum = factory.createDatum();
		datum.setVal(650d);
		scatter.getDatums().add(datum);
		datum = factory.createDatum();
		datum.setVal(700d);
		scatter.getDatums().add(datum);
		datum = factory.createDatum();
		datum.setVal(720d);
		scatter.getDatums().add(datum);
		datum = factory.createDatum();
		datum.setVal(790d);
		scatter.getDatums().add(datum);
		sa = factory.createSelectiveAnnotation();
		sa.setAnnotation(scatter);
		chartsSet.getSharedAxis().getAnnotations().add(sa);

		// oh, try markers on the dependent axis
		final ScatterSet pScatter = factory.createScatterSet();
		datum = factory.createDatum();
		datum.setVal(100d);
		pScatter.getDatums().add(datum);
		datum = factory.createDatum();
		datum.setVal(500d);
		pScatter.getDatums().add(datum);
		pressureAxis.getAnnotations().add(pScatter);

		// hey, how about a zone on the dependent axis?
		final Zone pZone = factory.createZone();
		pZone.setStart(380);
		pZone.setEnd(450);
		pZone.setColor(Color.yellow);
		pZone.setName("Pixel Zone");
		pressureAxis.getAnnotations().add(pZone);

		return chartsSet;
	}

	@Test
	public void testNotifications() {
		final Chart chart = createModel().getCharts().get(0);

		final ChartNameChangeListener listener = new ChartNameChangeListener();
		chart.eAdapters().add(listener);

		Assert.assertFalse(listener.isNotified());

		chart.setName("Changed");
		Assert.assertTrue(listener.isNotified());
	}

	@Test
	public void testReadModel() {
		final URI resourceURI = URI.createFileURI("testRead.stackedcharts");
		final Resource resource = new ResourceSetImpl().createResource(resourceURI);
		try {
			resource.load(new HashMap<>());
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail("Could not read model: " + e.getMessage());
		}
		final ChartSet chartsSet = (ChartSet) resource.getContents().get(0);

		Assert.assertNotNull(chartsSet);
		Assert.assertEquals(2, chartsSet.getCharts().size());

		final Chart chart = chartsSet.getCharts().get(0);
		Assert.assertEquals("Temperature & Salinity", chart.getName());

		// have a look at the innads
		EList<DependentAxis> axes = chart.getMinAxes();
		Assert.assertEquals("Correct number", 1, axes.size());
		final DependentAxis axis1 = axes.get(0);
		Assert.assertEquals("correct name", "Temperature", axis1.getName());
		Assert.assertEquals("correct direction", AxisDirection.ASCENDING, axis1.getDirection());
		axes = chart.getMaxAxes();
		final DependentAxis axis2 = axes.get(0);
		Assert.assertEquals("correct name", "Salinity", axis2.getName());
	}

	@Test
	public void testWriteModel() {
		final ChartSet chartsSet = createModel();
		final URI resourceURI = URI.createFileURI("testWrite.stackedcharts");
		final Resource resource = new ResourceSetImpl().createResource(resourceURI);
		resource.getContents().add(chartsSet);
		try {
			resource.save(null);
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail("Could not write model: " + e.getMessage());
		}
	}

}
