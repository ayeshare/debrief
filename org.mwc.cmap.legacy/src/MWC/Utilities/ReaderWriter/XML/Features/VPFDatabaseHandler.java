
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
import MWC.GUI.ToolParent;
import MWC.GUI.VPF.CoverageLayer;
import MWC.GUI.VPF.DebriefFeatureWarehouse;
import MWC.GUI.VPF.LibraryLayer;
import MWC.GUI.VPF.VPFDatabase;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;

abstract public class VPFDatabaseHandler extends MWCXMLReader implements PlottableExporter {

	private static final String _myType = "vpf_database";

	private static ToolParent _myParent;

	public static void initialise(final ToolParent toolParent) {
		_myParent = toolParent;
	}

	boolean _isVisible;

	VPFDatabase _myDatabase;

	public VPFDatabaseHandler() {
		// inform our parent what type of class we are
		super(_myType);

		addAttributeHandler(new HandleBooleanAttribute("Visible") {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});
		addHandler(new VPFLibraryHandler() {
			@Override
			public void addLibrary(final String name, final boolean visible,
					final java.util.Vector<CoverageLayer> coverages) {
				addThisLibrary(name, visible, coverages);
			}

			@Override
			public com.bbn.openmap.layer.vpf.LibrarySelectionTable getLST(final String name) {
				LibrarySelectionTable res = null;

				checkDatabase();

				if (_myDatabase != null)
					res = _myDatabase.getLST(name);

				return res;
			}

			@Override
			public DebriefFeatureWarehouse getWarehouse() {
				checkDatabase();
				return _myDatabase.getWarehouse();
			}
		});

	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	public void addThisLibrary(final String name, final boolean visible,
			final java.util.Vector<CoverageLayer> coverages) {
		checkDatabase();

		// did we manage to load the library?
		if (_myDatabase != null) {

			// do we know about this library?
			final LibraryLayer lib = _myDatabase.getLibrary(name);

			if (lib == null) {
				if (_myParent != null)
					_myParent.logError(ToolParent.ERROR, "Unable to find VPF library for:" + name, null);
				return;
			}

			// set the visibility
			lib.setVisible(visible);

			// check that there are coverages in this layer
			if (coverages != null) {

				// now add the coverages to the library
				final java.util.Enumeration<CoverageLayer> enumer = coverages.elements();
				while (enumer.hasMoreElements()) {
					final CoverageLayer cl = enumer.nextElement();
					lib.add(cl);
				}
			}
		} else {
			if (_myParent != null)
				_myParent.logError(ToolParent.ERROR, "VPF Library paths not set", null);
		}
	}

	void checkDatabase() {
		if (_myDatabase == null) {
			// create the database, but don't populate it
			_myDatabase = MWC.GUI.Tools.Palette.CreateVPFLayers.createMyLibrary(false);
		}
	}

	@Override
	public void elementClosed() {
		// have we created any data?
		checkDatabase();

		if (_myDatabase == null)
			return;

		// update the object
		_myDatabase.setVisible(_isVisible);

		// add ourselves to the parent layer
		addPlottable(_myDatabase);

		// reset the data
		_myDatabase = null;

	}

	@Override
	public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final MWC.GUI.VPF.VPFDatabase ll = (MWC.GUI.VPF.VPFDatabase) plottable;
		final Element coast = doc.createElement(_myType);

		// do the visibility
		coast.setAttribute("Visible", writeThis(ll.getVisible()));

		// now pass throuth the coverages, outputting each one
		final java.util.Enumeration<Editable> enumer = ll.elements();
		while (enumer.hasMoreElements()) {
			final LibraryLayer cl = (LibraryLayer) enumer.nextElement();
			VPFLibraryHandler.exportThisPlottable(cl, coast, doc);
		}

		parent.appendChild(coast);
	}

}