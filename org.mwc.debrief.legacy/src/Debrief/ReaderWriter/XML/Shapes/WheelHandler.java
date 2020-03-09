
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

import org.xml.sax.Attributes;

import MWC.GUI.Properties.SteppingBoundedInteger;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class WheelHandler extends ShapeHandler implements PlottableExporter {

	private static final String OUTER_RADIUS = "Outer";

	private static final String INNER_RADIUS = "Inner";
	private static final String EMPTY_INNER = "EmptyInner";

	private static final String ORIENTATION = "Orientation";

	MWC.GenericData.WorldLocation _centre;

	WorldDistance _innerDist;
	WorldDistance _outerDist;

	Integer _spokeSize = null; // degs
	Integer _orientation = null; // degs
	Boolean _emptyInner = null;

	public WheelHandler() {
		// inform our parent what type of class we are
		super("wheel");

		addHandler(new LocationHandler("centre") {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_centre = res;
			}
		});

		addHandler(new WorldDistanceHandler(INNER_RADIUS) {

			@Override
			public void setWorldDistance(final WorldDistance res) {
				_innerDist = res;
			}
		});
		addHandler(new WorldDistanceHandler(OUTER_RADIUS) {

			@Override
			public void setWorldDistance(final WorldDistance res) {
				_outerDist = res;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(EMPTY_INNER) {

			@Override
			public void setValue(final String name, final boolean value) {
				_emptyInner = value;
			}
		});

		addAttributeHandler(new HandleAttribute(INNER_RADIUS) {
			@Override
			public void setValue(final String name, final String val) {
				try {
					final double _inner = readThisDouble(val);
					_innerDist = new WorldDistance(_inner, WorldDistance.YARDS);
				} catch (final java.text.ParseException pe) {
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:" + val);
				}

			}
		});
		addAttributeHandler(new HandleAttribute(OUTER_RADIUS) {
			@Override
			public void setValue(final String name, final String val) {
				try {
					final double _outer = readThisDouble(val);
					_outerDist = new WorldDistance(_outer, WorldDistance.YARDS);
				} catch (final java.text.ParseException pe) {
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:" + val);
				}

			}
		});
		addAttributeHandler(new HandleAttribute("SpokeSize") {
			@Override
			public void setValue(final String name, final String val) {
				_spokeSize = Integer.valueOf(val);
			}
		});
		addAttributeHandler(new HandleAttribute(ORIENTATION) {
			@Override
			public void setValue(final String name, final String val) {
				_orientation = Integer.valueOf(val);
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
		if (ps instanceof MWC.GUI.Shapes.WheelShape) {
			// export the attributes
			final MWC.GUI.Shapes.WheelShape cs = (MWC.GUI.Shapes.WheelShape) ps;
			ePlottable.setAttribute(INNER_RADIUS, writeThis(cs.getRadiusInner().getValueIn(WorldDistance.YARDS)));
			ePlottable.setAttribute(OUTER_RADIUS, writeThis(cs.getRadiusOuter().getValueIn(WorldDistance.YARDS)));
			ePlottable.setAttribute(EMPTY_INNER, writeThis(cs.isEmptyInner()));
			ePlottable.setAttribute("SpokeSize", writeThis(cs.getSpokeSize().getCurrent()));
			ePlottable.setAttribute(ORIENTATION, writeThis(cs.getOrientation()));
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getCentre(), "centre", ePlottable,
					doc);
			WorldDistanceHandler.exportDistance(INNER_RADIUS, cs.getRadiusInner(), ePlottable, doc);
			WorldDistanceHandler.exportDistance(OUTER_RADIUS, cs.getRadiusOuter(), ePlottable, doc);
		} else {
			throw new java.lang.RuntimeException("wrong shape passed to Wheel exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

	@Override
	public final MWC.GUI.Shapes.PlainShape getShape() {
		final MWC.GUI.Shapes.WheelShape ls = new MWC.GUI.Shapes.WheelShape(_centre, _innerDist, _outerDist);
		if (_spokeSize != null)
			ls.setSpokeSize(new SteppingBoundedInteger(_spokeSize.intValue(), 0, 10, 1));

		if (_orientation != null) {
			ls.setOrientation(_orientation);
			_orientation = null;
		}

		if (_emptyInner != null)
			ls.setEmptyInner(_emptyInner);

		return ls;
	}

	// this is one of ours, so get on with it!
	@Override
	protected final void handleOurselves(final String name, final Attributes attributes) {
		_centre = null;
		_innerDist = _outerDist = null;
		super.handleOurselves(name, attributes);
	}

}