
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

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

abstract public class VPFFeatureHandler extends MWCXMLReader {

	private static final String _myType = "vpf_feature";

	public static void exportThisFeature(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final MWC.GUI.VPF.FeaturePainter fp = (MWC.GUI.VPF.FeaturePainter) plottable;
		final Element feature = doc.createElement(_myType);

		// do the visibility
		feature.setAttribute("Visible", writeThis(fp.getVisible()));

		// do the feature type
		feature.setAttribute("Type", fp.getFeatureType());

		// do the name
		feature.setAttribute("Description", fp.getName());

		// do the colour
		ColourHandler.exportColour(fp.getColor(), feature, doc);

		// and store the data
		parent.appendChild(feature);
	}

	java.awt.Color _theColor;
	String _theType;
	String _theDescription;

	boolean _isVisible;

	public VPFFeatureHandler() {
		// inform our parent what type of class we are
		super(_myType);

		addAttributeHandler(new HandleBooleanAttribute("Visible") {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});
		addAttributeHandler(new HandleAttribute("Type") {
			@Override
			public void setValue(final String name, final String value) {
				_theType = value;
			}
		});
		addAttributeHandler(new HandleAttribute("Description") {
			@Override
			public void setValue(final String name, final String value) {
				_theDescription = value;
			}
		});
		addHandler(new ColourHandler() {
			@Override
			public void setColour(final java.awt.Color color) {
				_theColor = color;
			}
		});

	}

	abstract public void addFeature(String type, String description, java.awt.Color color, boolean isVisible);

	@Override
	public void elementClosed() {
		// pass the data to the parent
		addFeature(_theType, _theDescription, _theColor, _isVisible);

		// reset the parameters
		_theType = null;
		_theDescription = null;
		_theColor = null;
		_isVisible = false;
	}

}