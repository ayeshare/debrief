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

package org.mwc.asset.core;

import java.io.InputStream;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.mwc.cmap.core.ui_support.DragDropSupport;
import org.mwc.cmap.core.ui_support.DragDropSupport.XMLFileDropHandler;
import org.osgi.framework.BundleContext;

import ASSET.ParticipantType;
import ASSET.GUI.Workbench.Plotters.BehavioursPlottable;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.GUI.Workbench.Plotters.SensorsPlottable;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Sensor.SensorList;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layers;
import MWC.GUI.LoggingService;

/**
 * The activator class controls the plug-in life cycle
 */
public class ASSETPlugin extends AbstractUIPlugin implements IStartup, ErrorLogger {

	protected static class XMLParticipantDropHandler extends XMLFileDropHandler {
		@SuppressWarnings("rawtypes")
		public XMLParticipantDropHandler(final String[] elementTypes, final Class[] targetTypes) {
			super(elementTypes, targetTypes);
		}

		@Override
		public void handleDrop(final InputStream source, final MWC.GUI.Editable targetElement, final Layers parent) {
			final ParticipantType part = ASSETReaderWriter.importParticipant("unknown", source);
			final ScenarioLayer layer = (ScenarioLayer) targetElement;
			final CoreScenario cs = (CoreScenario) layer.getScenario();
			cs.addParticipant(part.getId(), part);
			// fire modified on the layer
			parent.fireModified(layer);
		}
	}

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.asset.core";
	public static final String SCENARIO_CONTROLLER = "org.mwc.asset.ScenarioController";

	public static final String SCENARIO_CONTROLLER2 = "org.mwc.asset.ScenarioController2";

	public static final String VESSEL_MONITOR = "org.mwc.asset.VesselMonitor";

	public static final String STATIC_DATA = "org.mwc.asset.sample_data";

	public static final String SENSOR_MONITOR = "org.mwc.asset.SensorMonitor";

	/**
	 * and the context used to describe our undo list
	 */
	public final static IUndoContext ASSET_CONTEXT = new ObjectUndoContext("ASSET");

	// The shared instance
	private static ASSETPlugin plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ASSETPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path.
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.asset.core", path);
	}

	/**
	 * error logging utility
	 * 
	 * @param severity  the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *                  <code>INFO</code>, <code>WARNING</code>, or
	 *                  <code>CANCEL</code>
	 * @param message   a human-readable message, localized to the current locale
	 * @param exception a low-level exception, or <code>null</code> if not
	 *                  applicable
	 */
	public static void logThisError(final int severity, final String message, final Throwable exception) {
		final Status stat = new Status(severity, "org.mwc.asset.core", IStatus.OK, message, exception);

		if (getDefault() != null)
			getDefault().getLog().log(stat);
		else
			System.err.println("Logger called before initialisation. Msg:" + message + " Ex:" + exception);
	}

	/**
	 * somebody to help create images
	 */
	private ASSETImageHelper _myImageHelper;

	/**
	 * The constructor
	 */
	public ASSETPlugin() {
		plugin = this;
	}

	/**
	 * do our pre-startup processing
	 */
	@Override
	public void earlyStartup() {

		// move image support from early startup to here, so we can declare the
		// images
		// before the UI gets drawn
		_myImageHelper = new ASSETImageHelper();
		// give the LayerManager our image creator.
		CoreViewLabelProvider.addImageHelper(_myImageHelper);

		final XMLFileDropHandler parts = new XMLParticipantDropHandler(
				new String[] { "SSK", "FixedWing", "Torpedo", "SSN", "Helo", "Surface" },
				new Class[] { ScenarioLayer.class });

		final XMLFileDropHandler behaviours = new XMLFileDropHandler(new String[] { "Waterfall", "Sequence", "Switch" },
				new Class[] { BehavioursPlottable.class }) {
			@Override
			public void handleDrop(final InputStream source, final MWC.GUI.Editable targetElement,
					final Layers parent) {
				// get the model for this element
				final BehavioursPlottable bp = (BehavioursPlottable) targetElement;

				if (bp.getDecisionModel() instanceof BehaviourList) {
					final BehaviourList bl = (BehaviourList) bp.getDecisionModel();
					ASSETReaderWriter.importThis(bl, null, source);
					parent.fireModified(bp.getTopLevelLayer());
				}
			}
		};

		final XMLFileDropHandler sensors = new XMLFileDropHandler(new String[] { "BroadbandSensor",
				"ActiveBroadbandSensor", "DippingActiveBroadbandSensor", "NarrowbandSensor", "OpticLookupSensor",
				"RadarLookupSensor", "MADLookupSensor", "ActiveInterceptSensor", },
				new Class[] { SensorsPlottable.class }) {
			@Override
			public void handleDrop(final InputStream source, final MWC.GUI.Editable targetElement,
					final Layers parent) {
				// get the model for this element
				final SensorsPlottable bp = (SensorsPlottable) targetElement;

				final SensorList sl = bp.getSensorFit();
				ASSETReaderWriter.importThis(sl, null, source);
				parent.fireModified(bp.getTopLevelLayer());
			}
		};

		DragDropSupport.addDropHelper(parts);
		DragDropSupport.addDropHelper(behaviours);
		DragDropSupport.addDropHelper(sensors);
	}

	@Override
	public void logError(final int severity, final String text, final Exception e) {
		final Status stat = new Status(severity, "org.mwc.cmap.core", IStatus.OK, text, e);
		getLog().log(stat);
	}

	@Override
	public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
		logError(status, text, e);
	}

	@Override
	public void logStack(final int status, final String text) {
		logError(status, "Stack trace requestesd:" + text, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		LoggingService.initialise(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}
