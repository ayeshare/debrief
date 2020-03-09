
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

import java.awt.Font;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class GridHandler extends MWCXMLReader implements PlottableExporter {
	private static final String VISIBLE = "Visible";
	private static final String PLOT_LABELS = "PlotLabels";
	private static final String DELTA = "Delta";
	private static final String UNITS = "Units";
	private static final String MY_TYPE = "grid";
	private static final String NAME = "Name";

	/**
	 * utility class which appends the other grid attributes
	 *
	 * @param gridElement the element to put the grid into
	 * @param theGrid     the grid to export
	 * @param doc         the document it's all going into
	 */
	protected static void exportGridAttributes(final Element gridElement,
			final MWC.GUI.Chart.Painters.GridPainter theGrid, final Document doc) {
		// do the visibility
		gridElement.setAttribute(VISIBLE, writeThis(theGrid.getVisible()));
		gridElement.setAttribute(PLOT_LABELS, writeThis(theGrid.getPlotLabels()));

		// does it have a none-standard name?
		if (theGrid.getName() != GridPainter.GRID_TYPE_NAME) {
			gridElement.setAttribute(NAME, theGrid.getName());
		}

		FontHandler.exportFont(theGrid.getFont(), gridElement, doc);

		// and the delta (retaining the units
		WorldDistanceHandler.exportDistance(DELTA, theGrid.getDelta(), gridElement, doc);

		// do the colour
		ColourHandler.exportColour(theGrid.getColor(), gridElement, doc);
	}

	java.awt.Color _theColor;
	boolean _isVisible;
	WorldDistance _delta;
	double _deltaDegs;
	boolean _plotLabels;
	String _myUnits = null;
	protected String _myName = null;

	protected Font _myFont = null;

	public GridHandler() {
		this(MY_TYPE);
	}

	public GridHandler(final String theType) {
		// inform our parent what type of class we are
		super(theType);

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(PLOT_LABELS) {
			@Override
			public void setValue(final String name, final boolean value) {
				_plotLabels = value;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(DELTA) {
			@Override
			public void setValue(final String name, final double value) {
				_deltaDegs = value;
			}
		});
		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_myName = val;
			}
		});
		addAttributeHandler(new HandleAttribute(UNITS) {
			@Override
			public void setValue(final String name, final String value) {
				_myUnits = value;
			}
		});

		addHandler(new ColourHandler() {
			@Override
			public void setColour(final java.awt.Color color) {
				_theColor = color;
			}
		});

		addHandler(new FontHandler() {
			@Override
			public void setFont(final Font res) {
				_myFont = res;
			}
		});

		addHandler(new WorldDistanceHandler(DELTA) {

			@Override
			public void setWorldDistance(final WorldDistance res) {
				_delta = res;
			}
		});
	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	@Override
	public void elementClosed() {
		// create a Grid from this data
		final MWC.GUI.Chart.Painters.GridPainter csp = getGrid();
		csp.setColor(_theColor);
		csp.setVisible(_isVisible);

		if (_myFont != null)
			csp.setFont(_myFont);

		// do we have a new units value?
		if (_delta != null) {
			csp.setDelta(_delta);
		} else {
			// use the old value
			// do we know our units
			if (_myUnits == null)
				// no, we don't - assume they're in NM
				csp.setDelta(new MWC.GenericData.WorldDistance(_deltaDegs, WorldDistance.NM));
			else {
				// yes, we do - best use them
				csp.setDelta(new MWC.GenericData.WorldDistance(_deltaDegs, WorldDistance.getUnitIndexFor(_myUnits)));
			}
		}

		csp.setPlotLabels(_plotLabels);

		// is there a name?
		if (_myName != null)
			csp.setName(_myName);

		addPlottable(csp);

		// reset our variables
		_theColor = null;
		_plotLabels = false;
		_isVisible = false;
		_myUnits = null;
		_myName = null;
		_delta = null;
		_myFont = null;
	}

	/**
	 * export this grid
	 *
	 * @param plottable the grid we're going to export
	 * @param parent
	 * @param doc
	 */
	@Override
	public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final MWC.GUI.Chart.Painters.GridPainter theGrid = (MWC.GUI.Chart.Painters.GridPainter) plottable;
		final Element gridElement = doc.createElement(MY_TYPE);

		exportGridAttributes(gridElement, theGrid, doc);

		parent.appendChild(gridElement);
	}

	/**
	 * get the grid object itself (we supply this method so that it can be
	 * overwritten, by the LocalGrid painter for example
	 *
	 * @return
	 */
	protected MWC.GUI.Chart.Painters.GridPainter getGrid() {
		return new MWC.GUI.Chart.Painters.GridPainter();
	}

}