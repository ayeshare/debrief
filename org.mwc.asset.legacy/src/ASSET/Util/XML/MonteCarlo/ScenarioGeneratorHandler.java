
package ASSET.Util.XML.MonteCarlo;

import java.util.Date;

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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

abstract public class ScenarioGeneratorHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	static final private String type = "ScenarioGenerator";

	public static org.w3c.dom.Element exportScenario(final Object scenario, final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element scen = doc.createElement(type);
		scen.setAttribute("Created", new Date().toString());
		scen.setAttribute("Name", "ASSET Scenario");

		return scen;
	}

	public ScenarioGeneratorHandler() {
		// inform our parent what type of class we are
		super(type);

	}

	@Override
	public void elementClosed() {
		super.elementClosed();
	}

	// abstract public void setGenerator(ScenarioGenerator genny);

	@Override
	public void startElement(final String nameSpace, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		// don't bother...
	}

}