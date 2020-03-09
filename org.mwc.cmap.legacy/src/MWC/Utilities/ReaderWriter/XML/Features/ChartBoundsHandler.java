
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

import java.awt.Color;

import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class ChartBoundsHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
		implements PlottableExporter {

	private static final String CHART_REFERENCE = "ChartReference";
	private static final String FILE_NAME = "FileName";
	private static final String BOTTOM_RIGHT = "BottomRight";
	private static final String TOP_LEFT = "TopLeft";
	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LocationPropertyEditor lp = new MWC.GUI.Properties.LocationPropertyEditor();
	/**
	 * and define the strings used to describe the shape
	 *
	 */
	private static final String LABEL_VISIBLE = "LabelVisible";
	private static final String SHAPE_VISIBLE = "Visible";
	private static final String LABEL_LOCATION = "LabelLocation";
	private static final String LABEL_TEXT = "Label";
	String _myType = null;
	String _label = null;
	java.awt.Font _font = null;
	Integer _theLocation = null;
	boolean _isVisible = false;

	boolean _labelVisible = true;

	private WorldLocation _tl;
	private WorldLocation _br;
	private Color _col;
	protected String _filename;

	public ChartBoundsHandler() {
		// inform our parent what type of class we are
		super(CHART_REFERENCE);

		addHandler(new LocationHandler(TOP_LEFT) {
			@Override
			public void setLocation(final WorldLocation res) {
				_tl = res;
			}
		});
		addHandler(new LocationHandler(BOTTOM_RIGHT) {
			@Override
			public void setLocation(final WorldLocation res) {
				_br = res;
			}
		});

		addHandler(new FontHandler() {
			@Override
			public void setFont(final java.awt.Font font) {
				_font = font;
			}
		});

		addAttributeHandler(new HandleAttribute(LABEL_TEXT) {
			@Override
			public void setValue(final String name, final String value) {
				_label = fromXML(value);
			}
		});

		addHandler(new ColourHandler() {
			@Override
			public void setColour(final Color res) {
				_col = res;
			}
		});

		addAttributeHandler(new HandleAttribute(LABEL_LOCATION) {
			@Override
			public void setValue(final String name, final String val) {
				lp.setAsText(val);
				_theLocation = (Integer) lp.getValue();
			}
		});

		addAttributeHandler(new HandleAttribute(FILE_NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_filename = val;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(SHAPE_VISIBLE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(LABEL_VISIBLE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_labelVisible = value;
			}
		});

	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	@Override
	public void elementClosed() {

		final MWC.GUI.Shapes.ChartBoundsWrapper sw = new MWC.GUI.Shapes.ChartBoundsWrapper(_label, _tl, _br, _col,
				_filename);

		if (_theLocation != null) {
			sw.setLabelLocation(_theLocation);
		}
		sw.setVisible(_isVisible);
		sw.setLabelVisible(_labelVisible);

		addPlottable(sw);

		// reset the local parameters
		_label = null;
		_theLocation = null;
		_isVisible = true;
		_filename = null;
	}

	@Override
	public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		// output the shape related stuff first
		final org.w3c.dom.Element theShape = doc.createElement(CHART_REFERENCE);

		final MWC.GUI.Shapes.ChartBoundsWrapper sw = (MWC.GUI.Shapes.ChartBoundsWrapper) plottable;

		// put the parameters into the parent
		theShape.setAttribute(LABEL_TEXT, toXML(sw.getName()));
		theShape.setAttribute(FILE_NAME, sw.getFileName());
		lp.setValue(sw.getLabelLocation());
		theShape.setAttribute(LABEL_LOCATION, lp.getAsText());
		theShape.setAttribute(SHAPE_VISIBLE, writeThis(sw.getVisible()));
		theShape.setAttribute(LABEL_VISIBLE, writeThis(sw.getLabelVisible()));

		// output the colour for the shape
		MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(sw.getShape().getColor(), theShape, doc);

		// and the rectangle corners
		LocationHandler.exportLocation(sw.getShape().getCorner_TopLeft(), TOP_LEFT, theShape, doc);
		LocationHandler.exportLocation(sw.getShape().getCornerBottomRight(), BOTTOM_RIGHT, theShape, doc);

		// add ourselves to the output
		parent.appendChild(theShape);
	}

}