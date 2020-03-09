
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

import MWC.GUI.Properties.LabelLocationPropertyEditor;
import MWC.GUI.Shapes.FurthestOnCircleShape;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class FurthestOnCircleHandler extends ShapeHandler implements PlottableExporter {

	private static final String SPEED = "Speed";
	private static final String ARC_CENTRE = "ArcCentre";
	private static final String ARC_WIDTH = "ArcWidth";
	private static final String INTERVAL = "Interval";
	private static final String NUM_RINGS = "NumRings";
	private static final String CENTRE = "centre";
	private static final String RANGE_LABEL_LOC = "RangeLabelLoc";

	MWC.GenericData.WorldLocation _centre;
	protected String _labelLoc;
	protected Integer _numRings;
	protected WorldSpeed _speed;
	protected int _arcWidth;
	protected int _arcCentre;
	protected int _timeInterval;

	public FurthestOnCircleHandler() {
		// inform our parent what type of class we are
		super("furthest_on_circle");

		addHandler(new LocationHandler(CENTRE) {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_centre = res;
			}
		});

		addHandler(new WorldSpeedHandler(SPEED) {

			@Override
			public void setSpeed(final WorldSpeed res) {
				_speed = res;
			}
		});

		addAttributeHandler(new HandleIntegerAttribute(NUM_RINGS) {
			@Override
			public void setValue(final String name, final int val) {
				_numRings = val;
			}
		});

		addAttributeHandler(new HandleIntegerAttribute(ARC_CENTRE) {
			@Override
			public void setValue(final String name, final int val) {
				_arcCentre = val;
			}
		});

		addAttributeHandler(new HandleIntegerAttribute(ARC_WIDTH) {
			@Override
			public void setValue(final String name, final int val) {
				_arcWidth = val;
			}
		});

		addAttributeHandler(new HandleIntegerAttribute(INTERVAL) {
			@Override
			public void setValue(final String name, final int val) {
				_timeInterval = val;
			}
		});

		addAttributeHandler(new HandleAttribute(RANGE_LABEL_LOC) {
			@Override
			public void setValue(final String name, final String val) {
				_labelLoc = val;
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
		if (ps instanceof MWC.GUI.Shapes.FurthestOnCircleShape) {
			// export the attributes
			final FurthestOnCircleShape cs = (MWC.GUI.Shapes.FurthestOnCircleShape) ps;

			final LabelLocationPropertyEditor lpe = new LabelLocationPropertyEditor();
			lpe.setValue(cs.getRangeLabelLocation());
			ePlottable.setAttribute(RANGE_LABEL_LOC, lpe.getAsText());
			ePlottable.setAttribute(NUM_RINGS, writeThis(cs.getNumRings().getCurrent()));
			ePlottable.setAttribute(ARC_CENTRE, writeThis(cs.getArcCentre().getCurrent()));
			ePlottable.setAttribute(ARC_WIDTH, writeThis(cs.getArcWidth().getCurrent()));
			ePlottable.setAttribute(INTERVAL, writeThis(cs.getTimeInterval()));

			LocationHandler.exportLocation(cs.getCentre(), CENTRE, ePlottable, doc);
			WorldSpeedHandler.exportSpeed(SPEED, cs.getSpeed(), ePlottable, doc);
		} else {
			throw new java.lang.RuntimeException("wrong shape passed to furthest on circe ring exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

	@Override
	public final MWC.GUI.Shapes.PlainShape getShape() {
		final FurthestOnCircleShape ls = new FurthestOnCircleShape(_centre, _numRings, _speed, _timeInterval,
				_arcCentre, _arcWidth);

		if (_labelLoc != null) {
			final LabelLocationPropertyEditor lp = new LabelLocationPropertyEditor();
			lp.setValue(_labelLoc);
			ls.setRangeLabelLocation((Integer) lp.getValue());
		}

		return ls;
	}

}