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

package ASSET.Util.XML.Control.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

abstract public class TargetHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc, final String myType) {
		// create ourselves
		final org.w3c.dom.Element thisElement = doc.createElement(myType);

		// get data item
		final TargetType bb = (TargetType) toExport;

		// output it's attributes

		// output the target type
		TargetTypeHandler.exportThis(bb, thisElement, doc);

		// output it's attributes
		parent.appendChild(thisElement);
	}

	TargetType _type = null;

	public TargetHandler(final String myType) {
		super(myType);
		addHandler(new TargetTypeHandler() {
			@Override
			public void setTargetType(final TargetType type) {
				_type = type;
			}
		});
	}

	@Override
	public void elementClosed() {
		setTargetType(_type);
		_type = null;
	}

	abstract public void setTargetType(TargetType type);
}