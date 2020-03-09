
package org.mwc.debrief.core.loaders.xml_handlers;

import Debrief.ReaderWriter.XML.GUI.PrimarySecondaryHandler;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.TrackDataProvider;

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

public abstract class ToteHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	public static void exportTote(final TrackDataProvider tracks, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create the element to put it in
		final org.w3c.dom.Element tote = doc.createElement("tote");

		// now output the parts of the tote
		// find the primary
		final MWC.GenericData.WatchableList primary = tracks.getPrimaryTrack();
		final WatchableList[] secondaries = tracks.getSecondaryTracks();

		if (primary != null) {
			final org.w3c.dom.Element pri = doc.createElement("primary");
			pri.setAttribute("Name", primary.getName());
			tote.appendChild(pri);
		}

		if (secondaries != null) {
			if (secondaries.length > 0) {
				for (int i = 0; i < secondaries.length; i++) {
					final WatchableList thisSec = secondaries[i];
					if (thisSec != null) {
						final org.w3c.dom.Element sec = doc.createElement("secondary");
						sec.setAttribute("Name", thisSec.getName());
						tote.appendChild(sec);
					}
				}
			}
		}

		// ////////////////////////////
		// and finally add ourselves to the parent
		parent.appendChild(tote);
	}

	public ToteHandler() {
		// inform our parent what type of class we are
		super("tote");

		addHandler(new PrimarySecondaryHandler("primary") {
			@Override
			public void setTrack(final String name) {
				setPrimarySecondary(true, name);
			}
		});

		addHandler(new PrimarySecondaryHandler("secondary") {
			@Override
			public void setTrack(final String name) {
				setPrimarySecondary(false, name);
			}
		});

	}

	// private Debrief.Tools.Tote.WatchableList getTrack(String name)
	// {
	// Debrief.Tools.Tote.WatchableList res = null;
	//
	// // look at the data
	// MWC.GUI.Plottable ly = _theData.findLayer(name);
	//
	// if (ly == null)
	// {
	// // no, this isn't a top level layer, maybe it's an element
	//
	// // find the nearest editable item
	// int num = _theData.size();
	// for (int i = 0; i < num; i++)
	// {
	// MWC.GUI.Layer thisL = _theData.elementAt(i);
	// // go through this layer
	// java.util.Enumeration iter = thisL.elements();
	// while (iter.hasMoreElements())
	// {
	// MWC.GUI.Plottable p = (MWC.GUI.Plottable) iter.nextElement();
	// String nm = p.getName();
	// if (nm.equals(name))
	// {
	// ly = p;
	// break;
	// }
	// }
	// }
	//
	// }
	//
	// if (ly instanceof Debrief.Tools.Tote.WatchableList)
	// {
	// res = (Debrief.Tools.Tote.WatchableList) ly;
	// }
	//
	// return res;
	// }

	abstract public void setPrimarySecondary(boolean isPrimary, String trackName);

}