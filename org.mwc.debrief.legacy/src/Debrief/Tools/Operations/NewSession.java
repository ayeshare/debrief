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

// $RCSfile: NewSession.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: NewSession.java,v $
// Revision 1.4  2004/08/09 12:55:26  Ian.Mayo
// Overcome problems introduced with Default layers - we were trying to load the session twice.  Buger
//
// Revision 1.3  2004/08/09 09:35:24  Ian.Mayo
// Add option to allow default plot to open with new Plot request.  Pass message to user if filenot found.
//
// Revision 1.2  2004/08/09 08:55:03  Ian.Mayo
// Lots of tidying, plus provide option to create new plot based on default settings
//
// Revision 1.1.1.2  2003/07/21 14:48:32  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:00+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:27+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:55+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:59+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-10-08 17:12:12+01  administrator
// Rename button
//
// Revision 1.1  2001-08-17 08:04:53+01  administrator
// General tidying up
//
// Revision 1.0  2001-07-17 08:41:19+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:31+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:21  ianmayo
// initial import of files
//
// Revision 1.5  2000-08-07 12:24:31+01  ian_mayo
// tidy icon filename
//
// Revision 1.4  2000-04-19 11:26:12+01  ian_mayo
// tidy up, remove reference to Session
//
// Revision 1.3  2000-03-14 15:01:19+00  ian_mayo
// minor typo
//
// Revision 1.2  2000-03-14 09:49:15+00  ian_mayo
// assign icon names to tools
//
// Revision 1.1  1999-10-12 15:34:08+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:49+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:15+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:06+01  sm11td
// Initial revision
//
// Revision 1.4  1999-06-01 16:49:21+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.3  1999-02-04 08:02:26+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 14:25:03+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:33:09+00  sm11td
// Initial revision
//

package Debrief.Tools.Operations;

import java.io.File;
import java.io.FileInputStream;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.XML.PlotHandler;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter;

/**
 * command to create a new blank session in the current application
 */
public final class NewSession extends PlainTool {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the filename to read our default set of layers from
	 */
	public static final String DEFAULT_NAME = "default_plot.xml";

	///////////////////////////////////////////////////////////////
	// member variables
	///////////////////////////////////////////////////////////////
	private final Application _theApplication;

	// whether to read in default layers
	private final boolean _loadDefaultLayers;

	///////////////////////////////////////////////////////////////
	// constructor
	///////////////////////////////////////////////////////////////
	public NewSession(final Application theApplication) {
		this(theApplication, false);
	}

	public NewSession(final Application application, final boolean loadLayers) {
		super(application, "New Plot", "images/24/new.png");
		_theApplication = application;
		_loadDefaultLayers = loadLayers;
	}

	///////////////////////////////////////////////////////////////
	// member functions
	///////////////////////////////////////////////////////////////

	/**
	 * actually open the session
	 */
	@Override
	public final void execute() {
		// Ok, create the skeleton session
		Session theSess = _theApplication.createSession();

		// do we want to load default layers?
		if (_loadDefaultLayers) {
			try {
				final File defaultLayers = new File(DEFAULT_NAME);
				if (defaultLayers.exists()) {
					// ok. try to load the default layers
					final MWCXMLReader handler = new PlotHandler(_theApplication, theSess, DEFAULT_NAME);

					// note, the handler give the session to the application all on it's own
					MWCXMLReaderWriter.importThis(handler, DEFAULT_NAME, new FileInputStream(DEFAULT_NAME));
				} else {
					// file not found, report error to user (as if they don't know)
					MWC.GUI.Dialogs.DialogFactory.showMessage("Open default plot",
							"Sorry " + NewSession.DEFAULT_NAME + " not found in Debrief startup directory."
									+ System.getProperty("line.separator")
									+ "Please create this file if required (as described in Debrief User Guide).");
				}
			} catch (final Exception e) {
				MWC.Utilities.Errors.Trace.trace(e, "Problem occured whilst trying to load default layers");
			}
		} else {
			// give it to the parent ourselves
			_theApplication.newSession(theSess);

			//
			theSess = null;
		}

	}

	/**
	 * we don't return an action, since this isn't an undoable operation anyway
	 */
	@Override
	public final Action getData() {
		return null;
	}
}
