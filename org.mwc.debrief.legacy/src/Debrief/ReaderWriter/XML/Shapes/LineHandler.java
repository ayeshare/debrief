
package Debrief.ReaderWriter.XML.Shapes;

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

import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class LineHandler extends ShapeHandler implements PlottableExporter {

	private final static String ARROW_AT_END = "ArrowAtEnd";
	private final static String SHOW_AUTO_CALC = "ShowAutoCalc";

	MWC.GenericData.WorldLocation _start;
	MWC.GenericData.WorldLocation _end;

	protected boolean _arrowAtEnd = false;
	protected boolean _showAutoCalc = false;

	public LineHandler() {
		// inform our parent what type of class we are
		super("line");

		addHandler(new LocationHandler("tl") {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_start = res;
			}
		});
		addHandler(new LocationHandler("br") {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_end = res;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ARROW_AT_END) {
			@Override
			public void setValue(final String name, final boolean value) {
				_arrowAtEnd = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_AUTO_CALC) {
			@Override
			public void setValue(final String name, final boolean value) {
				_showAutoCalc = value;
			}
		});

	}

	@Override
	public final void elementClosed() {
		super.elementClosed();

		// reset the local parameters
		_start = _end = null;
		_arrowAtEnd = false;
		_showAutoCalc = false;
	}

	@Override
	public final void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// output the shape related stuff first
		final org.w3c.dom.Element ePlottable = doc.createElement(_myType);

		super.exportThisPlottable(plottable, ePlottable, doc);

		// now our circle related stuff

		// get the circle
		final Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper) plottable;
		final MWC.GUI.Shapes.PlainShape ps = sw.getShape();
		if (ps instanceof MWC.GUI.Shapes.LineShape) {
			// export the attributes
			final MWC.GUI.Shapes.LineShape cs = (MWC.GUI.Shapes.LineShape) ps;
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getLine_Start(), "tl", ePlottable,
					doc);
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getLineEnd(), "br", ePlottable, doc);
			ePlottable.setAttribute(ARROW_AT_END, writeThis(cs.getArrowAtEnd()));
			ePlottable.setAttribute(SHOW_AUTO_CALC, writeThis(cs.isShowAutoCalc()));
		} else {
			throw new java.lang.RuntimeException("wrong shape passed to line exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

	@Override
	public final MWC.GUI.Shapes.PlainShape getShape() {
		final MWC.GUI.Shapes.LineShape ls = new MWC.GUI.Shapes.LineShape(_start, _end);
		ls.setArrowAtEnd(_arrowAtEnd);
		ls.setShowAutoCalc(_showAutoCalc);
		return ls;
	}

}