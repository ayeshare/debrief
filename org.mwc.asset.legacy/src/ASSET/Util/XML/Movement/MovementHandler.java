
package ASSET.Util.XML.Movement;

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

abstract public class MovementHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "Movement";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// output it's attributes
		parent.appendChild(thisPart);

	}

	public MovementHandler() {
		super(type);

	}

	@Override
	public void elementClosed() {
		setModel(new ASSET.Models.Movement.CoreMovement());
	}

	abstract public void setModel(ASSET.Models.MovementType dec);

}