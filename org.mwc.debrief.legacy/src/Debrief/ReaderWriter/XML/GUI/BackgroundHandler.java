
package Debrief.ReaderWriter.XML.GUI;

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

import java.awt.Color;

import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

abstract public class BackgroundHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	private static final String MY_TYPE = "Background";

	public static void exportThis(final Color color, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {
		// create the element to put it in
		final org.w3c.dom.Element tote = doc.createElement(MY_TYPE);
		ColourHandler.exportColour(color, tote, doc);

		//////////////////////////////
		// and finally add ourselves to the parent
		parent.appendChild(tote);
	}

	Color _theColor;

	public BackgroundHandler() {
		// inform our parent what type of class we are
		super(MY_TYPE);

		addHandler(new ColourHandler() {
			@Override
			public void setColour(final Color res) {
				_theColor = res;
			}
		});

	}

	@Override
	public final void elementClosed() {
		// pass on to the listener class
		setBackgroundColor(_theColor);
	}

	abstract public void setBackgroundColor(Color theColor);

}