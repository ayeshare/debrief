
package Debrief.ReaderWriter.XML;

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

import org.w3c.dom.Element;

import MWC.Algorithms.PlainProjection;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

final class ProjectionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	public static void exportProjection(final MWC.Algorithms.PlainProjection projection,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {

		/*
		 * <!ELEMENT projection (tl, br)> <!ATTLIST projection type CDATA #REQUIRED
		 * border CDATA "1.0" relative (TRUE|FALSE) "FALSE" >
		 */

		final Element proj = doc.createElement("projection");

		// see which type of projection it is
		if (projection instanceof MWC.Algorithms.Projections.FlatProjection) {
			final MWC.Algorithms.Projections.FlatProjection flat = (MWC.Algorithms.Projections.FlatProjection) projection;

			// first the attributes for the projection
			proj.setAttribute("Type", "Flat");
			proj.setAttribute("Border", writeThis(flat.getDataBorder()));
			proj.setAttribute("Relative", writeThis(flat.getPrimaryOriented()));

			// and now the corners
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(flat.getDataArea().getTopLeft(), "tl",
					proj, doc);
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(flat.getDataArea().getBottomRight(),
					"br", proj, doc);

			parent.appendChild(proj);
		} else {
			// Hmm, we don't really know how tor handle this.
			final java.lang.RuntimeException duffer = new java.lang.RuntimeException(
					"Unable to store this projection type");
			throw duffer;
		}

	}

	MWC.GenericData.WorldLocation _tl;
	MWC.GenericData.WorldLocation _br;
	String _type;
	double _border;
	boolean _relative;
	private final Debrief.GUI.Frames.Session _session;

	private Debrief.GUI.Tote.AnalysisTote _theTote = null;

	public ProjectionHandler(final Debrief.GUI.Frames.Session destination) {
		// inform our parent what type of class we are
		super("projection");

		// store the session
		_session = destination;

		final Debrief.GUI.Views.PlainView pv = _session.getCurrentView();
		if (pv instanceof Debrief.GUI.Views.AnalysisView) {
			final Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView) pv;
			_theTote = av.getTote();
		}

		// handlers for the corners
		addHandler(new LocationHandler("tl") {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_tl = res;
			}
		});
		addHandler(new LocationHandler("br") {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_br = res;
			}
		});

		addAttributeHandler(new HandleAttribute("Type") {
			@Override
			public void setValue(final String name, final String val) {
				_type = val;
			}
		});
		addAttributeHandler(new HandleAttribute("Border") {
			@Override
			public void setValue(final String name, final String val) {
				try {
					_border = readThisDouble(val);
				} catch (final java.text.ParseException pe) {
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in border size for projection:" + val);
				}
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Relative") {
			@Override
			public void setValue(final String name, final boolean value) {
				_relative = value;
			}
		});

	}

	@Override
	public final void elementClosed() {
		MWC.Algorithms.PlainProjection newProj = null;
		if (_type.equals("Flat")) {
			newProj = new MWC.Algorithms.Projections.FlatProjection();
			newProj.setDataBorder(_border);
			newProj.setDataArea(new MWC.GenericData.WorldArea(_tl, _br));
			newProj.setPrimaryOriented(_relative);
			if (_theTote != null)
				newProj.setRelativeProjectionParent(_theTote);
		}

		if (newProj != null) {
			final Debrief.GUI.Views.PlainView pv = _session.getCurrentView();
			if (pv instanceof Debrief.GUI.Views.AnalysisView) {
				final Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView) pv;

				// get any listeners for the current projection
				final PlainProjection oldProj = av.getChart().getCanvas().getProjection();

				// assign the new projection
				av.getChart().getCanvas().setProjection(newProj);

				// fire the updated event
				oldProj.firePropertyChange(PlainProjection.REPLACED_EVENT, oldProj, newProj);

			}
		}

	}

}