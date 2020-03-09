
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

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.Shapes.ChartFolio;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

public class ChartFolioHandler extends LayerHandler {

	private static final String MY_TYPE = "ChartFolio";
	private static final String SHOW_NAMES = "SHOW_NAMES";
	/**
	 * class which contains list of textual representations of scale locations
	 */
	static ETOPOPainter.KeyLocationPropertyEditor lp = new ETOPOPainter.KeyLocationPropertyEditor();

	public static void exportThisFolio(final ChartFolio folio, final Element parent, final Document doc) {
		// check our exporters
		checkExporters();

		final org.w3c.dom.Element eLayer = doc.createElement(MY_TYPE);

		eLayer.setAttribute("Name", folio.getName());
		eLayer.setAttribute("Visible", writeThis(folio.getVisible()));
		eLayer.setAttribute("LineThickness", writeThis(folio.getLineThickness()));

		// step through the components of the layer
		final java.util.Enumeration<Editable> enumer = folio.elements();
		while (enumer.hasMoreElements()) {
			final MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) enumer.nextElement();

			exportThisItem(nextPlottable, eLayer, doc);
		}

		// and our specific attributes
		eLayer.setAttribute(SHOW_NAMES, writeThis(folio.isShowNames()));
		ColourHandler.exportColour(folio.getLineColor(), eLayer, doc);

		parent.appendChild(eLayer);

	}

	java.awt.Color _theColor;

	boolean _showNames;

	public ChartFolioHandler(final Layers theLayers) {
		// inform our parent what type of class we are
		super(theLayers, MY_TYPE);

		addAttributeHandler(new HandleBooleanAttribute(SHOW_NAMES) {
			@Override
			public void setValue(final String name, final boolean value) {
				_showNames = value;
			}
		});

		addHandler(new ColourHandler() {
			@Override
			public void setColour(final java.awt.Color color) {
				_theColor = color;
			}
		});

	}

	@Override
	public void elementClosed() {
		// set our specific attributes
		final ChartFolio wrapper = (ChartFolio) _myLayer;
		wrapper.setShowNames(_showNames);
		wrapper.setLineColor(_theColor);

		// and store it, just like our parent does
		super.elementClosed();
	}

	@Override
	protected BaseLayer getLayer() {
		return new ChartFolio(_showNames, _theColor);
	}

}