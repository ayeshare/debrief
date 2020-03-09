
package org.mwc.debrief.core.loaders.xml_handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.PlotEditor;

import Debrief.ReaderWriter.XML.DebriefLayersHandler;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class SessionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private static final String EXTENSION_POINT_ID = "XMLLayerHandler";
	private static final String PLUGIN_ID = "org.mwc.debrief.core";
	private static ArrayList<LayerHandlerExtension> _extensionLoaders;

	public static void exportTheseLayers(final Layers theLayers, final PlotEditor thePlot,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element eSession = doc.createElement("session");

		// now the Layers
		DebriefLayersHandler.exportThis(theLayers, eSession, doc, loadLoaderExtensions(theLayers));

		// now the projection
		final PlainProjection proj;
		if (thePlot != null) {
			proj = (PlainProjection) thePlot.getAdapter(PlainProjection.class);
		} else {
			proj = new FlatProjection() {
				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public WorldArea getDataArea() {
					return Layers.getDebriefOrigin();
				}

				/**
				 * @return
				 */
				@Override
				public double getDataBorder() {
					return 1.1;
				}

				/**
				 * @return
				 */
				@Override
				public boolean getPrimaryOriented() {
					return false;
				}

			};
		}

		ProjectionHandler.exportProjection(proj, eSession, doc);

		// now the GUI
		// do we have a gui?
		if (thePlot != null)
			SWTGUIHandler.exportThis(thePlot, eSession, doc);

		// send out the data
		parent.appendChild(eSession);
	}

	public static void exportThis(final PlotEditor thePlot, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// ok, get the layers
		final Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);

		exportTheseLayers(theLayers, thePlot, parent, doc);
	}

	/**
	 * see if any extra right click handlers are defined
	 *
	 */
	private synchronized static ArrayList<LayerHandlerExtension> loadLoaderExtensions(final Layers theLayers) {
		if (_extensionLoaders == null) {
			_extensionLoaders = new ArrayList<LayerHandlerExtension>();

			final IExtensionRegistry registry = Platform.getExtensionRegistry();

			if (registry != null) {

				final IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID,
						EXTENSION_POINT_ID);

				final IExtension[] extensions = point.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					final IExtension iExtension = extensions[i];
					final IConfigurationElement[] confE = iExtension.getConfigurationElements();
					for (int j = 0; j < confE.length; j++) {
						final IConfigurationElement iConfigurationElement = confE[j];
						LayerHandlerExtension newInstance;
						try {
							newInstance = (LayerHandlerExtension) iConfigurationElement
									.createExecutableExtension("class");
							_extensionLoaders.add(newInstance);
						} catch (final CoreException e) {
							CorePlugin.logError(IStatus.ERROR, "Trouble whilst loading right-click handler extensions",
									e);
						}
					}
				}
			}
		}
		return _extensionLoaders;
	}

	public SessionHandler(final Layers _theLayers, final IControllableViewport view, final PlotEditor plot) {
		// inform our parent what type of class we are
		super("session");

		// define our handlers
		addHandler(new ProjectionHandler() {
			@Override
			public void setProjection(final PlainProjection proj) {
				view.setProjection(proj);
			}
		});
		addHandler(new SWTGUIHandler(plot) {
			@Override
			public void assignTracks(final String primaryTrack, final Vector<String> secondaryTracks) {
				// see if we have our track data listener
				if (view instanceof IAdaptable) {
					final IAdaptable ad = (IAdaptable) view;
					final Object adaptee = ad.getAdapter(org.mwc.cmap.core.DataTypes.TrackData.TrackManager.class);
					if (adaptee != null) {
						final TrackManager tl = (TrackManager) adaptee;
						tl.assignTracks(primaryTrack, secondaryTracks);
					}
				}
			}
		});

		final DebriefLayersHandler layersHandler = new DebriefLayersHandler(_theLayers);

		// ok, see if we have any extra layer handlers
		final ArrayList<LayerHandlerExtension> extraHandlers = loadLoaderExtensions(_theLayers);
		for (final Iterator<LayerHandlerExtension> iterator = extraHandlers.iterator(); iterator.hasNext();) {
			final LayerHandlerExtension thisE = iterator.next();

			// just double check taht it's an MWCXMLReader object
			if (thisE instanceof MWCXMLReader) {
				// tell it about the top level layers object
				thisE.setLayers(_theLayers);

				// and remmber it
				layersHandler.addHandler((MWCXMLReader) thisE);
			} else {
				DebriefPlugin.logError(IStatus.ERROR,
						"The layer handler we're read in is not of the corect type: " + thisE, null);
			}
		}

		// ok, we're now ready to register the layers handler
		addHandler(layersHandler);

	}

	@Override
	public final void elementClosed() {
		// and the GUI details
		// setGUIDetails(null);
	}

}