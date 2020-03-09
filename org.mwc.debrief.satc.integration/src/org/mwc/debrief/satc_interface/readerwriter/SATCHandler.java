
package org.mwc.debrief.satc_interface.readerwriter;

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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IStatus;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO.XStreamReader;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO.XStreamWriter;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

public class SATCHandler extends MWCXMLReader implements LayerHandlerExtension {
	private static final String MY_TYPE = "satc_solution";

	private static final String NAME = "NAME";
	private static final String SHOW_BOUNDS = "ShowBounds";
	private static final String ONLY_ENDS = "OnlyPlotEnds";
	private static final String SHOW_SOLUTIONS = "ShowSolutions";
	private static final String SHOW_ALTERATIONS = "ShowAlterationBounds";
	private static final String LIVE_RUNNING = "LiveRunning";

	protected String _myContents;

	private String _name;

	private Layers _theLayers;

	private Color _myColor = Color.green;

	private CharArrayWriter _cdataCharacters;

	protected boolean _showSolutions = false;

	protected boolean _showBounds = false;
	protected boolean _showAlterations = false;
	protected boolean _liveRunning = true;

	protected boolean _onlyPlotEnds = false;

	public SATCHandler() {
		this(MY_TYPE);
	}

	public SATCHandler(final String theType) {
		// inform our parent what type of class we are
		super(theType);

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_BOUNDS) {
			@Override
			public void setValue(final String name, final boolean value) {
				_showBounds = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_ALTERATIONS) {
			@Override
			public void setValue(final String name, final boolean value) {
				_showAlterations = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ONLY_ENDS) {
			@Override
			public void setValue(final String name, final boolean value) {
				_onlyPlotEnds = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(LIVE_RUNNING) {
			@Override
			public void setValue(final String name, final boolean value) {
				_liveRunning = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_SOLUTIONS) {
			@Override
			public void setValue(final String name, final boolean value) {
				_showSolutions = value;
			}
		});
		addHandler(new ColourHandler() {

			@Override
			public void setColour(final Color res) {
				_myColor = res;
			}
		});
	}

	@Override
	public boolean canExportThis(final Layer subject) {
		return (subject instanceof SATC_Solution);
	}

	/**
	 * the data is in a CDATA element. The only way to catch this is to use the
	 * characters handler
	 *
	 */
	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		super.characters(ch, start, length);

		_cdataCharacters.write(ch, start, length);
	}

	@Override
	public void elementClosed() {
		// create the solver
		final SATC_Activator activator = SATC_Activator.getDefault();

		if (activator != null) {

			final ISolversManager solvMgr = activator.getService(ISolversManager.class, true);
			final ISolver newSolvr = solvMgr.createSolver(_name);
			final SATC_Solution solution = new SATC_Solution(newSolvr);

			// and the preferences
			solution.setShowLocationConstraints(_showBounds);
			solution.setOnlyPlotLegEnds(_onlyPlotEnds);
			solution.setShowAlterationStates(_showAlterations);
			solution.setShowSolutions(_showSolutions);
			solution.setColor(_myColor);
			solution.getSolver().setLiveRunning(_liveRunning);

			// ok, repopulate the solver from the contents
			if (_cdataCharacters.size() > 0) {
				try {
					final InputStream inputStream = new ByteArrayInputStream(
							_cdataCharacters.toString().getBytes("utf-8"));
					final XStreamReader reader = XStreamIO.newReader(inputStream, "Unknown");
					if (reader.isLoaded()) {
						solution.getSolver().load(reader);
					}
				} catch (final UnsupportedEncodingException e) {
					SATC_Activator.log(IStatus.ERROR, "Problem laoding SATC Solution from XML", e);
				}
			}

			// ok, the solver knows it's contributions, but we've got update to
			// Debrief one.
			solution.selfScan();

			// put it into the solution.

			// and save it.
			_theLayers.addThisLayer(solution);
		} else {
			System.err.println("SATC Activator not initialised, SATC will not work");
		}
	}

	@Override
	public void exportThis(final Layer theLayer, final Element parent, final Document doc) {
		final SATC_Solution solution = (SATC_Solution) theLayer;

		// ok, marshall it into a String

		final XStreamWriter writer = XStreamIO.newWriter();
		solution.getSolver().save(writer);

		// and get the writer as a string
		final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		writer.process(oStream);
		try {
			final String res = oStream.toString("utf-8");

			// ok, now convert it to an object
			final Element newI = doc.createElement(MY_TYPE);

			// store the name
			newI.setAttribute(NAME, solution.getName());
			newI.setAttribute(SHOW_BOUNDS, writeThis(solution.getShowLocationConstraints()));
			newI.setAttribute(ONLY_ENDS, writeThis(solution.getOnlyPlotLegEnds()));
			newI.setAttribute(SHOW_SOLUTIONS, writeThis(solution.getShowSolutions()));
			newI.setAttribute(SHOW_ALTERATIONS, writeThis(solution.getShowAlterationStates()));
			newI.setAttribute(LIVE_RUNNING, writeThis(solution.getSolver().isLiveRunning()));
			ColourHandler.exportColour(solution.getColor(), newI, doc);

			// insert the CDATA child node
			final CDATASection cd = doc.createCDATASection(res);

			newI.appendChild(cd);

			// and store it
			parent.appendChild(newI);

		} catch (final UnsupportedEncodingException e) {
			SATC_Activator.log(IStatus.ERROR, "Problem reading in stored SATC Solution", e);
		}

	}

	@Override
	public void setLayers(final Layers theLayers) {
		_theLayers = theLayers;
	}

	@Override
	public void startElement(final String nameSpace, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		super.startElement(nameSpace, localName, qName, attributes);

		// clear the characters buffer
		_cdataCharacters = new CharArrayWriter();
	}

}