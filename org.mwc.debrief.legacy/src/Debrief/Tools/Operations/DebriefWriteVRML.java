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

// $RCSfile: DebriefWriteVRML.java,v $
// $Author: Ian.Mayo $
// $Log: DebriefWriteVRML.java,v $
// Revision 1.5  2005/12/13 09:04:45  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2004/11/25 10:24:32  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/22 13:41:03  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.2  2004/09/09 10:23:07  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:48:28  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:03+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:24+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:57+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:55+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:18+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-09 10:27:34+00  novatech
// use WatchableList instead of TrackWrapper
//
// Revision 1.1  2001-01-03 13:40:30+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:16  ianmayo
// initial import of files
//
// Revision 1.3  2000-11-02 16:45:48+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer, also changed TrackWrapper so that it implements Layer,  and as we read in files, we put them into track and add Track to Layers, not to Layer then Layers
//
// Revision 1.2  2000-09-28 12:09:39+01  ian_mayo
// switch to GMT time zone
//
// Revision 1.1  2000-08-23 09:35:23+01  ian_mayo
// Initial revision
//

package Debrief.Tools.Operations;

import java.text.ParseException;
import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * Code to plot the debrief-specific components of the VRML file
 *
 * @author IAN MAYO
 */
final class DebriefWriteVRML extends MWC.GUI.Tools.Operations.WriteVRML {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new DebriefWriteVRML
	 *
	 * @param theParent the parent application for the Action we are creating
	 * @param theLabel  the label for the plot button
	 * @param theData   the data we are going to plot
	 */
	public DebriefWriteVRML(final MWC.GUI.ToolParent theParent, final String theLabel, final MWC.GUI.Layers theData) {
		super(theParent, theLabel, theData);
	}

	/**
	 * over-ridden method, where we write out our data
	 *
	 * @param theData the data to plot
	 * @param out     the stream to write to
	 * @throws java.io.IOException file-related troubles
	 */
	@Override
	protected final void plotData(final MWC.GUI.Layers theData, final java.io.BufferedWriter out)
			throws java.io.IOException {
		// work through the layers
		int num = _theData.size();
		for (int i = 0; i < num; i++) {
			final Layer l = _theData.elementAt(i);
			final Enumeration<Editable> iter = l.elements();
			while (iter.hasMoreElements()) {
				final Object oj = iter.nextElement();
				if (oj instanceof Debrief.Wrappers.FixWrapper) {
					final Debrief.Wrappers.FixWrapper fw = (Debrief.Wrappers.FixWrapper) oj;
					final WorldLocation pos = fw.getLocation();

					if (fw.getSymbolShowing()) {
						final String lbl = fw.getName();
						try {
							writeBox(out, pos.getLong(), pos.getLat(), pos.getDepth(), fw.getColor(), lbl);
						} catch (final ParseException e) {
							MWC.Utilities.Errors.Trace.trace(e);
						}
					}

					if (fw.getLabelShowing()) {
						final String str = DebriefFormatDateTime.toStringHiRes(fw.getTime());
						writeText(out, pos.getLong(), pos.getLat(), pos.getDepth(), str, fw.getColor());
					}

				}
			}

		}

		// now draw the line connectors
		num = _theData.size();
		for (int i = 0; i < num; i++) {
			final Layer l = _theData.elementAt(i);
			final Enumeration<Editable> iter = l.elements();
			int len = 0;
			while (iter.hasMoreElements()) {
				final Object oj = iter.nextElement();

				if (oj instanceof MWC.GenericData.WatchableList) {
					// just check that we haven't got any dangling Fix lines
					// waiting to be finished
					if (len > 0) {
						// we have clearly written some fixes to the file, write the footer
						writeLineFooter(out, len);
						len = 0;
					}

					final MWC.GenericData.WatchableList tw = (MWC.GenericData.WatchableList) oj;
					final java.awt.Color col = tw.getColor();
					writeLineHeader(out, col);
				}

				if (oj instanceof Debrief.Wrappers.FixWrapper) {
					len++;
					final Debrief.Wrappers.FixWrapper fw = (Debrief.Wrappers.FixWrapper) oj;
					final WorldLocation pos = fw.getLocation();

					writeLineEntry(out, pos.getLong(), pos.getLat(), pos.getDepth());
				}
			}

			if (len > 0) {
				// we have clearly written some fixes to the file, write the footer
				writeLineFooter(out, len);
			}

		}
	}

}
