
package Debrief.ReaderWriter.XML.Shapes;

import org.xml.sax.Attributes;

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

import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class CircleHandler extends ShapeHandler implements PlottableExporter {

	private static final String MY_TYPE = "circle";
	private static final String CENTRE = "centre";
	private static final String RADIUS = "Radius";
	private static final String FILLED = "Filled";
	private static final String SEMI_TRANSPARENT = "SemiTransparent";
	protected MWC.GenericData.WorldLocation _centre;
	protected double _radius; // in kiloyards
	protected Boolean _filled;
	protected Boolean _semiTransparent;
	protected WorldDistance _radiusDist = null;

	public CircleHandler() {
		this(MY_TYPE);
	}

	public CircleHandler(final String theType) {

		// inform our parent what type of class we are
		super(theType);

		addHandler(new LocationHandler(CENTRE) {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_centre = res;
			}
		});

		addHandler(new WorldDistanceHandler(RADIUS) {

			@Override
			public void setWorldDistance(final WorldDistance res) {
				_radiusDist = res;
			}
		});

		addAttributeHandler(new HandleAttribute(RADIUS) {
			@Override
			public void setValue(final String name, final String val) {
				try {
					_radius = readThisDouble(val);
				} catch (final java.text.ParseException pe) {
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:" + val);
				}

			}
		});

		addAttributeHandler(new HandleBooleanAttribute(FILLED) {
			@Override
			public void setValue(final String name, final boolean value) {
				_filled = new Boolean(value);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(SEMI_TRANSPARENT) {
			@Override
			public void setValue(final String name, final boolean value) {
				_semiTransparent = new Boolean(value);
			}
		});
	}

	// export the circle specific components
	protected void exportCircleAttributes(final org.w3c.dom.Element ePlottable, final MWC.GUI.Shapes.CircleShape cs,
			final org.w3c.dom.Document doc) {
		ePlottable.setAttribute(FILLED, writeThis(cs.getFilled()));
		ePlottable.setAttribute(SEMI_TRANSPARENT, writeThis(cs.getSemiTransparent()));
		LocationHandler.exportLocation(cs.getCentre(), CENTRE, ePlottable, doc);
		WorldDistanceHandler.exportDistance(RADIUS, cs.getRadius(), ePlottable, doc);
	}

	@Override
	public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// output the shape related stuff first
		final org.w3c.dom.Element ePlottable = doc.createElement(_myType);

		super.exportThisPlottable(plottable, ePlottable, doc);

		// now our circle related stuff

		// get the circle
		final Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper) plottable;
		final MWC.GUI.Shapes.PlainShape ps = sw.getShape();
		// export the attributes
		final MWC.GUI.Shapes.CircleShape cs = (MWC.GUI.Shapes.CircleShape) ps;
		exportCircleAttributes(ePlottable, cs, doc);

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

	@Override
	public MWC.GUI.Shapes.PlainShape getShape() {
		MWC.GUI.Shapes.CircleShape ls = null;
		if (_radiusDist != null) {
			ls = new MWC.GUI.Shapes.CircleShape(_centre, _radiusDist);
		} else
			ls = new MWC.GUI.Shapes.CircleShape(_centre, new WorldDistance(_radius * 1000, WorldDistance.YARDS));

		if (_filled != null)
			ls.setFilled(_filled.booleanValue());
		if (_semiTransparent != null) {
			ls.setSemiTransparent(_semiTransparent.booleanValue());
		}

		return ls;
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(final String name, final Attributes attributes) {
		_radius = 0.0;
		_centre = null;
		_filled = null;
		_semiTransparent = null;
		_radiusDist = null;
		super.handleOurselves(name, attributes);
	}

}