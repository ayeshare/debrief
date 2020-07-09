
package ASSET.Util.XML.Decisions;

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

import ASSET.Models.Decision.Tactical.PatternSearch_Core;
import ASSET.Models.Decision.Tactical.PatternSearch_Saw;

abstract public class PatternSaw_SearchHandler extends PatternSearchCore_Handler {

	final static String type = "SawSearch";

	public static void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		exportCore(toExport, parent, type, doc);
	}

	public PatternSaw_SearchHandler() {
		super(type);
	}

	@Override
	protected PatternSearch_Core getModel() {
		return new PatternSearch_Saw(_origin, _spacing, _searchHeight, _searchSpeed, null, _height, _height);
	}

}