
package MWC.Utilities.ReaderWriter.XML.Features;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class ExternallyManagedLayerHandler extends MWCXMLReader {
	private static final String MY_TYPE = "externalDatafile";
	private static final String DATA_TYPE = "DataType";
	private static final String LAYER_PATH = "LayerPath";
	private static final String LAYER_NAME = "LayerName";
	private static final String VISIBLE = "Visible";
	/**
	 * class which contains list of textual representations of scale locations
	 */
	static MWC.GUI.Chart.Painters.ETOPOPainter.KeyLocationPropertyEditor lp = new MWC.GUI.Chart.Painters.ETOPOPainter.KeyLocationPropertyEditor();

	public static void exportThisPlottable(final MWC.GUI.Plottable plottable, final Element parent,
			final Document doc) {

		final ExternallyManagedDataLayer csp = (ExternallyManagedDataLayer) plottable;
		final Element etopo = doc.createElement(MY_TYPE);

		// do the visibility
		etopo.setAttribute(VISIBLE, writeThis(csp.getVisible()));
		etopo.setAttribute(DATA_TYPE, csp.getDataType());
		etopo.setAttribute(LAYER_NAME, csp.getName());
		etopo.setAttribute(LAYER_PATH, csp.getFilename());

		parent.appendChild(etopo);
	}

	boolean _isVisible;
	private final Layers _theLayers;
	private String _layerName;

	private String _fileName;

	private String _dataType;

	public ExternallyManagedLayerHandler(final Layers theLayers) {
		// inform our parent what type of class we are
		super(MY_TYPE);

		_theLayers = theLayers;

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});
		addAttributeHandler(new HandleAttribute(LAYER_NAME) {
			@Override
			public void setValue(final String name, final String value) {
				_layerName = value;
			}
		});
		addAttributeHandler(new HandleAttribute(LAYER_PATH) {
			@Override
			public void setValue(final String name, final String value) {
				_fileName = value;
			}
		});
		addAttributeHandler(new HandleAttribute(DATA_TYPE) {
			@Override
			public void setValue(final String name, final String value) {
				_dataType = value;
			}
		});

	}

	@Override
	public void elementClosed() {
		final ExternallyManagedDataLayer res = new ExternallyManagedDataLayer(_dataType, _layerName, _fileName);
		res.setVisible(_isVisible);
		_theLayers.addThisLayer(res);

		_fileName = null;
		_layerName = null;
		_dataType = null;
		_isVisible = true;
	}

}