
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.xml.sax.Attributes;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.WorldPathHandler;

abstract public class PolygonHandler extends ShapeHandler implements PlottableExporter {

	private static final String TYPE = "polygon";
	private static final String FILLED_STR = "Filled";
	private static final String SEMI_TRANSPARENT_STR = "SemiTransparent";
	private static final String CLOSED_STR = "Closed";
	private static final String LABEL_NODES = "LabelNodes";
	WorldPath _polygon;
	Boolean _filled = null;
	Boolean _semiTransparent = null;
	Boolean _closed = null;
	Boolean _labelNodes = null;

	public PolygonHandler() {
		this(TYPE);
	}

	public PolygonHandler(final String type) {
		// inform our parent what type of class we are
		super(type);

		addHandler(new WorldPathHandler() {
			@Override
			public void setPath(final WorldPath path) {
				_polygon = path;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(FILLED_STR) {
			@Override
			public void setValue(final String name, final boolean value) {
				_filled = new Boolean(value);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(SEMI_TRANSPARENT_STR) {
			@Override
			public void setValue(final String name, final boolean value) {
				_semiTransparent = new Boolean(value);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(LABEL_NODES) {
			@Override
			public void setValue(final String name, final boolean value) {
				_labelNodes = new Boolean(value);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(CLOSED_STR) {
			@Override
			public void setValue(final String name, final boolean value) {
				_closed = new Boolean(value);
			}
		});
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
		if (ps instanceof MWC.GUI.Shapes.PolygonShape) {
			// export the attributes
			final MWC.GUI.Shapes.PolygonShape cs = (MWC.GUI.Shapes.PolygonShape) ps;
			ePlottable.setAttribute(FILLED_STR, writeThis(cs.getFilled()));
			ePlottable.setAttribute(SEMI_TRANSPARENT_STR, writeThis(cs.getSemiTransparent()));
			ePlottable.setAttribute(CLOSED_STR, writeThis(cs.getClosed()));
			ePlottable.setAttribute(LABEL_NODES, writeThis(cs.getShowNodeLabels()));

			MWC.Utilities.ReaderWriter.XML.Util.WorldPathHandler.exportThis(cs.getPoints(), ePlottable, doc);
		} else {
			throw new RuntimeException("wrong shape passed to Polygon exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

	@Override
	public final MWC.GUI.Shapes.PlainShape getShape() {
		final Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		final PolygonShape poly = new PolygonShape(nodes);
		final Collection<WorldLocation> iter = _polygon.getPoints();
		for (final Iterator<WorldLocation> iterator = iter.iterator(); iterator.hasNext();) {
			final WorldLocation worldLocation = iterator.next();
			nodes.add(new PolygonNode("" + (nodes.size() + 1), worldLocation, poly));
		}
		if (_filled != null)
			poly.setFilled(_filled.booleanValue());
		if (_semiTransparent != null)
			poly.setSemiTransparent(_semiTransparent.booleanValue());
		if (_closed != null)
			poly.setClosed(_closed.booleanValue());
		if (_labelNodes != null)
			poly.setShowNodeLabels(_labelNodes);

		// also reset those flags
		_filled = null;
		_semiTransparent = null;
		_closed = null;
		_labelNodes = null;

		// recalculate the points in the polygon
		poly.calcPoints();

		return poly;
	}

	@Override
	protected ShapeWrapper getWrapper() {
		final PolygonShape shape = (PolygonShape) getShape();
		shape.setColor(_col);
		final Debrief.Wrappers.PolygonWrapper sw = new Debrief.Wrappers.PolygonWrapper(this._label, shape, _col, null);
		sw.setName(_label);
		sw.setColor(_col);
		return sw;
	}

	// this is one of ours, so get on with it!
	@Override
	protected final void handleOurselves(final String name, final Attributes attributes) {
		super.handleOurselves(name, attributes);
	}
}