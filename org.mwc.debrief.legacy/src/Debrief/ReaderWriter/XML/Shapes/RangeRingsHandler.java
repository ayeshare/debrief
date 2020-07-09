
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
import MWC.GUI.Shapes.RangeRingShape;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class RangeRingsHandler extends ShapeHandler implements PlottableExporter {

	private static final String RING_WIDTH = "RingWidth";
	private static final String NUM_RINGS = "NumRings";
	private static final String CENTRE = "centre";
	private static final String RANGE_LABEL_LOC = "RangeLabelLoc";

	MWC.GenericData.WorldLocation _centre;
	WorldDistance _ringWidth;
	protected String _labelLoc;
	protected Integer _numRings;

	public RangeRingsHandler() {
		// inform our parent what type of class we are
		super("range_rings");

		addHandler(new LocationHandler(CENTRE) {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_centre = res;
			}
		});

		addHandler(new WorldDistanceHandler(RING_WIDTH) {

			@Override
			public void setWorldDistance(final WorldDistance res) {
				_ringWidth = res;
			}
		});

		addAttributeHandler(new HandleAttribute(NUM_RINGS) {
			@Override
			public void setValue(final String name, final String val) {
				_numRings = Integer.valueOf(val);
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
		if (ps instanceof MWC.GUI.Shapes.RangeRingShape) {
			// export the attributes
			final RangeRingShape cs = (MWC.GUI.Shapes.RangeRingShape) ps;

			final LabelLocationPropertyEditor lpe = new LabelLocationPropertyEditor();
			lpe.setValue(cs.getRangeLabelLocation());
			ePlottable.setAttribute(RANGE_LABEL_LOC, lpe.getAsText());
			ePlottable.setAttribute(NUM_RINGS, writeThis(cs.getNumRings().getCurrent()));
			LocationHandler.exportLocation(cs.getCentre(), CENTRE, ePlottable, doc);
			WorldDistanceHandler.exportDistance(RING_WIDTH, cs.getRingWidth(), ePlottable, doc);
		} else {
			throw new java.lang.RuntimeException("wrong shape passed to range ring exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

	@Override
	public final MWC.GUI.Shapes.PlainShape getShape() {
		final MWC.GUI.Shapes.RangeRingShape ls = new MWC.GUI.Shapes.RangeRingShape(_centre, _numRings, _ringWidth);

		if (_labelLoc != null) {
			final LabelLocationPropertyEditor lp = new LabelLocationPropertyEditor();
			lp.setValue(_labelLoc);
			ls.setRangeLabelLocation((Integer) lp.getValue());
		}

		return ls;
	}

}