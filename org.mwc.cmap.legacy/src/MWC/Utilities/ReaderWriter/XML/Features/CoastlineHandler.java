
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

import org.w3c.dom.Element;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

abstract public class CoastlineHandler extends MWCXMLReader implements PlottableExporter {

	java.awt.Color _theColor;
	boolean _isVisible;

	public CoastlineHandler() {
		// inform our parent what type of class we are
		super("coastline");

		addAttributeHandler(new HandleBooleanAttribute("Visible") {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});
		addHandler(new ColourHandler() {
			@Override
			public void setColour(final java.awt.Color color) {
				_theColor = color;
			}
		});

	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	@Override
	public void elementClosed() {
		// create a coastline from this data
		final MWC.GUI.Chart.Painters.CoastPainter csp = new MWC.GUI.Chart.Painters.CoastPainter();
		csp.setColor(_theColor);
		csp.setVisible(_isVisible);

		addPlottable(csp);

		// reset our variables
		_theColor = null;
		_isVisible = false;
	}

	@Override
	public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final MWC.GUI.Chart.Painters.CoastPainter csp = (MWC.GUI.Chart.Painters.CoastPainter) plottable;
		final Element coast = doc.createElement("coastline");

		// do the visibility
		coast.setAttribute("Visible", writeThis(csp.getVisible()));

		// do the name
		coast.setAttribute("Name", "World Default");

		// do the colour
		ColourHandler.exportColour(csp.getColor(), coast, doc);

		parent.appendChild(coast);
	}

}