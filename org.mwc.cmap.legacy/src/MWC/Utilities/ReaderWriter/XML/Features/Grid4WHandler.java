
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
import java.awt.Font;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.Chart.Painters.Grid4WPainter;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class Grid4WHandler extends MWCXMLReader implements PlottableExporter {
	private static final String VISIBLE = "Visible";
	private static final String PLOT_LABELS = "PlotLabels";
	private static final String PLOT_LINES = "PlotLines";
	private static final String XDELTA = "xDelta";
	private static final String YDELTA = "yDelta";
	private static final String XMIN = "xMin";
	private static final String XMAX = "xMax";
	private static final String YMIN = "yMin";
	private static final String YMAX = "yMax";
	private static final String ORIGIN = "Origin";
	private static final String ORIENTATION = "Orientation";
	private static final String MY_TYPE = "Grid4W";
	private static final String NAME = "Name";
	private static final String LINE_STYLE = "LineStyle";
	private static final String FILL_GRID = "FillGrid";
	private static final String FILL_COLOR = "FillColor";
	private static final String FONT_COLOR = "FontColor";
	private static final String US_STANDARD = "US_Standard";

	/**
	 * utility class which appends the other grid attributes
	 *
	 * @param gridElement the element to put the grid into
	 * @param theGrid     the grid to export
	 * @param doc         the document it's all going into
	 */
	protected static void exportGridAttributes(final Element gridElement,
			final MWC.GUI.Chart.Painters.Grid4WPainter theGrid, final Document doc) {
		// do the visibility
		gridElement.setAttribute(VISIBLE, writeThis(theGrid.getVisible()));
		gridElement.setAttribute(XDELTA, writeThisLong(theGrid.getXDelta().getValueIn(WorldDistance.NM)));
		gridElement.setAttribute(YDELTA, writeThisLong(theGrid.getYDelta().getValueIn(WorldDistance.NM)));
		gridElement.setAttribute(XMIN, theGrid.getXMin());
		gridElement.setAttribute(XMAX, theGrid.getXMax());
		gridElement.setAttribute(YMIN, writeThis(theGrid.getYMin().intValue()));
		gridElement.setAttribute(YMAX, writeThis(theGrid.getYMax().intValue()));
		gridElement.setAttribute(ORIENTATION, writeThis(theGrid.getOrientation()));
		gridElement.setAttribute(PLOT_LABELS, writeThis(theGrid.getPlotLabels()));
		gridElement.setAttribute(PLOT_LINES, writeThis(theGrid.getPlotLines()));
		gridElement.setAttribute(FILL_GRID, writeThis(theGrid.getFillGrid()));
		gridElement.setAttribute(US_STANDARD, writeThis(theGrid.getOriginAtTopLeft()));
		gridElement.setAttribute(LINE_STYLE, writeThis(theGrid.getLineStyle()));

		// does it have a none-standard name?
		if (theGrid.getName() != GridPainter.GRID_TYPE_NAME) {
			gridElement.setAttribute(NAME, theGrid.getName());
		}

		// do the colour
		ColourHandler.exportColour(theGrid.getColor(), gridElement, doc);
		ColourHandler.exportColour(theGrid.getFillColor(), gridElement, doc, FILL_COLOR);
		ColourHandler.exportColour(theGrid.getFontColor(), gridElement, doc, FONT_COLOR);
		LocationHandler.exportLocation(theGrid.getOrigin(), ORIGIN, gridElement, doc);
		FontHandler.exportFont(theGrid.getFont(), gridElement, doc);
	}

	boolean _isVisible;
	boolean _plotLabels;
	boolean _plotLines;
	protected String _myName = null;
	double _xDelta;
	double _yDelta;
	String _xMin = "A";
	String _xMax = "D";
	int _yMin = 1;
	int _yMax = 4;
	double _orientation = 0;
	WorldLocation _origin;
	Font _font = null;
	Color _theColor = null;
	Color _fillColor = null;
	Color _fontColor = null;
	int _lineStyle = LineStylePropertyEditor.SOLID;
	boolean _fillGrid = false;

	boolean _usStandard = false;

	public Grid4WHandler() {
		this(MY_TYPE);
	}

	public Grid4WHandler(final String theType) {
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
		addAttributeHandler(new HandleBooleanAttribute(US_STANDARD) {
			@Override
			public void setValue(final String name, final boolean value) {
				_usStandard = value;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(XDELTA) {
			@Override
			public void setValue(final String name, final double value) {
				_xDelta = value;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(YDELTA) {
			@Override
			public void setValue(final String name, final double value) {
				_yDelta = value;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(ORIENTATION) {
			@Override
			public void setValue(final String name, final double value) {
				_orientation = value;
			}
		});
		addHandler(new LocationHandler(ORIGIN) {
			@Override
			public void setLocation(final WorldLocation value) {
				_origin = value;
			}
		});
		addAttributeHandler(new HandleAttribute(XMIN) {
			@Override
			public void setValue(final String name, final String val) {
				_xMin = val;
			}
		});
		addAttributeHandler(new HandleAttribute(XMAX) {
			@Override
			public void setValue(final String name, final String val) {
				_xMax = val;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(YMIN) {
			@Override
			public void setValue(final String name, final int val) {
				_yMin = val;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(YMAX) {
			@Override
			public void setValue(final String name, final int val) {
				_yMax = val;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(LINE_STYLE) {
			@Override
			public void setValue(final String name, final int val) {
				_lineStyle = val;
			}
		});

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_myName = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(FILL_GRID) {
			@Override
			public void setValue(final String name, final boolean val) {
				_fillGrid = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(PLOT_LINES) {
			@Override
			public void setValue(final String name, final boolean val) {
				_plotLines = val;
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
				_font = res;
			}
		});
		addHandler(new ColourHandler(FILL_COLOR) {
			@Override
			public void setColour(final java.awt.Color color) {
				_fillColor = color;
			}
		});
		addHandler(new ColourHandler(FONT_COLOR) {
			@Override
			public void setColour(final java.awt.Color color) {
				_fontColor = color;
			}
		});

	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	@Override
	public void elementClosed() {
		// create a Grid from this data
		final MWC.GUI.Chart.Painters.Grid4WPainter csp = new Grid4WPainter(_origin);
		csp.setColor(_theColor);
		csp.setVisible(_isVisible);
		csp.setPlotLabels(_plotLabels);
		csp.setName(_myName);

		// set the other bits
		csp.setXMin(_xMin);
		csp.setXMax(_xMax);
		csp.setYMin(_yMin);
		csp.setYMax(_yMax);
		csp.setOrientation(_orientation);
		csp.setXDelta(new WorldDistance(_xDelta, WorldDistance.NM));
		csp.setYDelta(new WorldDistance(_yDelta, WorldDistance.NM));
		csp.setFillGrid(_fillGrid);
		csp.setPlotLines(_plotLines);
		csp.setOriginAtTopLeft(_usStandard);
		if (_fillColor != null)
			csp.setFillColor(_fillColor);
		if (_font != null)
			csp.setFont(_font);
		if (_fontColor != null) {
			csp.setFontColor(_fontColor);
		} else {
			// just use the default shape colro
			csp.setFontColor(_theColor);
		}
		csp.setLineStyle(_lineStyle);

		addPlottable(csp);

		// reset our variables
		_font = null;
		_origin = null;
		_fillColor = null;
		_fontColor = null;
		_theColor = null;
		_plotLabels = false;
		_isVisible = false;
		_myName = null;
		_lineStyle = LineStylePropertyEditor.SOLID;
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

		final MWC.GUI.Chart.Painters.Grid4WPainter theGrid = (MWC.GUI.Chart.Painters.Grid4WPainter) plottable;
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