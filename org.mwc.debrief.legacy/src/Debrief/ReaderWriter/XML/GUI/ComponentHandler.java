
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

import Debrief.ReaderWriter.XML.GUIHandler;
import MWC.Utilities.ReaderWriter.XML.Util.PropertyHandler;

abstract public class ComponentHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	GUIHandler.ComponentDetails details = new GUIHandler.ComponentDetails();

	public ComponentHandler() {
		// inform our parent what type of class we are
		super("component");

		addHandler(new PropertyHandler() {
			@Override
			public void setProperty(final String name, final String val) {
				details.addProperty(name, val);
			}
		});

		super.addAttributeHandler(new HandleAttribute("Type") {
			@Override
			public void setValue(final String name, final String val) {
				details.type = val;
			}
		});
	}

	abstract public void addComponent(GUIHandler.ComponentDetails details1);

	@Override
	public final void elementClosed() {
		addComponent(details);
		details = null;
		details = new GUIHandler.ComponentDetails();
	}

	// the exporter for this component details item

}