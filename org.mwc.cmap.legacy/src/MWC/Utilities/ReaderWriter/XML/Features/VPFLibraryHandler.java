
package MWC.Utilities.ReaderWriter.XML.Features;

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

import com.bbn.openmap.layer.vpf.LibrarySelectionTable;

import MWC.GUI.Editable;
import MWC.GUI.VPF.CoverageLayer;
import MWC.GUI.VPF.DebriefFeatureWarehouse;
import MWC.GUI.VPF.FeaturePainter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class VPFLibraryHandler extends MWCXMLReader {

	private static final String _myType = "vpf_library";

	public static void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final MWC.GUI.VPF.LibraryLayer ll = (MWC.GUI.VPF.LibraryLayer) plottable;
		final Element coast = doc.createElement(_myType);

		// do the visibility
		coast.setAttribute("Visible", writeThis(ll.getVisible()));

		// do the name
		coast.setAttribute("Name", ll.getName());

		// now pass throuth the coverages, outputting each one
		final java.util.Enumeration<Editable> enumer = ll.elements();
		while (enumer.hasMoreElements()) {
			final CoverageLayer cl = (CoverageLayer) enumer.nextElement();
			VPFCoverageHandler.exportThisCoverage(cl, coast, doc);
		}

		parent.appendChild(coast);
	}

	boolean _isVisible;

	String _myName;

	private java.util.Vector<CoverageLayer> _myCoverages = null;

	public VPFLibraryHandler() {
		// inform our parent what type of class we are
		super(_myType);

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String value) {
				_myName = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible") {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});
		addHandler(new VPFCoverageHandler() {
			@Override
			public void addCoverage(final String type, final String description, final boolean visible,
					final java.util.Enumeration<FeaturePainter> features) {
				addThisCoverage(type, description, visible, features);
			}
		});

	}

	abstract public void addLibrary(String name, boolean visible, java.util.Vector<CoverageLayer> coverages);

	void addThisCoverage(final String type, final String description, final boolean visible,
			final java.util.Enumeration<FeaturePainter> features) {
		// do we have our list?
		if (_myCoverages == null)
			_myCoverages = new java.util.Vector<CoverageLayer>(0, 1);

		// create this coverage
		final LibrarySelectionTable lsTable = getLST(_myName);
		if (lsTable != null) {
			final CoverageLayer cl = new CoverageLayer(lsTable, getWarehouse(), type);

			// format the coverage
			cl.setVisible(visible);
			cl.setDescription(description);

			// are there any features?
			if (features != null) {
				// add the features
				while (features.hasMoreElements()) {
					final FeaturePainter fp = features.nextElement();
					cl.add(fp);
				}
			}

			_myCoverages.add(cl);
		}
	}

	@Override
	public void elementClosed() {

		// add ourselves to the parent layer
		addLibrary(_myName, _isVisible, _myCoverages);

		// reset the data
		_myCoverages = null;
		_myName = null;
	}

	abstract public com.bbn.openmap.layer.vpf.LibrarySelectionTable getLST(String name);

	abstract public DebriefFeatureWarehouse getWarehouse();

}