
package org.mwc.cmap.naturalearth.readerwriter;

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

import org.mwc.cmap.naturalearth.view.NEFeatureRoot;
import org.mwc.cmap.naturalearth.wrapper.NELayer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;

public class NELayerHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader implements LayerHandlerExtension {

	public static final String TYPE = "NaturalEarth";
	public static final String NAME = "Name";
	public static final String VIS = "Visible";

	private NEFeatureRoot _myStore;
	private Layers _theLayers;

	private boolean _isVis;
	private String _myName;

	public NELayerHandler() {
		this(TYPE);
	}

	public NELayerHandler(final String theType) {
		// inform our parent what type of class we are
		super(theType);

		// _theLayers = theLayers;

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_myName = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(VIS) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isVis = val;
			}
		});

		addHandler(new NEFeatureRootHandler() {
			@Override
			public void addStore(final NEFeatureRoot store) {
				_myStore = store;
			}
		});

	}

	@Override
	public boolean canExportThis(final Layer subject) {
		return subject instanceof NELayer;
	}

	@Override
	public final void elementClosed() {
		// _myStore.setName(_myName);
		final NELayer nel = new NELayer(_myStore);
		nel.setVisible(_myStore.getVisible());
		_theLayers.addThisLayer(nel);
		_myStore = null;
		_myName = null;
	}

//	public static void exportLayer(final NELayer layer,
//			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
//	{
//			final Element eStore = doc.createElement(TYPE);
//			NEFeatureRoot store = layer.getStore();
//			eStore.appendChild(NEFeatureRootHandler.exportStore(store, doc));
//			parent.appendChild(eStore);
//	}

	@Override
	public void exportThis(final Layer theLayer, final Element parent, final Document doc) {
		final NELayer neLayer = (NELayer) theLayer;
		final Element neStyle = doc.createElement(TYPE);
		// neStyle.setAttribute(NAME, neLayer.getName());
		// neStyle.setAttribute(NELayerHandler.VIS, writeThis(neLayer.getVisible()));
		parent.appendChild(neStyle);

		final NEFeatureRoot store = neLayer.getStore();
		final Element neStoreElement = NEFeatureRootHandler.exportStore(store, doc);
		neStyle.appendChild(neStoreElement);
	}

	// this is one of ours, so get on with it!
	@Override
	protected final void handleOurselves(final String name, final Attributes attributes) {
		super.handleOurselves(name, attributes);
	}

	@Override
	public void setLayers(final Layers theLayers) {
		_theLayers = theLayers;
	}

}