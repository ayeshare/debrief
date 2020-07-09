
package Debrief.ReaderWriter.XML.Formatters;

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

import Debrief.Wrappers.Formatters.CoreFormatItemListener;
import MWC.GUI.Properties.AttributeTypePropertyEditor;

public abstract class CoreFormatHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	private static final String MY_TYPE = "CoreFormatter";

	/**
	 * and define the strings used to describe the shape
	 *
	 */
	private static final String NAME = "Name";
	private static final String ACTIVE = "Active";
	private static final String LAYER_NAME = "LayerName";
	private static final String SYMBOLOGY = "Symbology";
	private static final String INTERVAL = "Interval";
	private static final String REGULAR_INTERVALS = "RegularIntervals";
	private static final String A_TYPE = "A_Style";

	static public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final Element theFormatter = doc.createElement(MY_TYPE);
		parent.appendChild(theFormatter);

		final CoreFormatItemListener theShape = (CoreFormatItemListener) plottable;

		final AttributeTypePropertyEditor pe = new AttributeTypePropertyEditor();
		pe.setValue(theShape.getAttributeType());

		// put the parameters into the parent
		theFormatter.setAttribute(NAME, theShape.getName());
		theFormatter.setAttribute(LAYER_NAME, theShape.getLayerName());
		theFormatter.setAttribute(SYMBOLOGY, theShape.getSymbology());
		theFormatter.setAttribute(INTERVAL, writeThis(theShape.getInterval().getDate().getTime()));
		theFormatter.setAttribute(A_TYPE, pe.getAsText());
		theFormatter.setAttribute(REGULAR_INTERVALS, writeThis(theShape.getRegularIntervals()));
		theFormatter.setAttribute(ACTIVE, writeThis(theShape.getVisible()));
	}

	private String fName;
	private String layerName;
	private String symbology;
	private int interval;
	private boolean regularIntervals;

	private int attrType;

	protected boolean active;

	public CoreFormatHandler() {
		super(MY_TYPE);

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String value) {
				fName = value;
			}
		});
		addAttributeHandler(new HandleAttribute(LAYER_NAME) {
			@Override
			public void setValue(final String name, final String value) {
				layerName = value;
			}
		});
		addAttributeHandler(new HandleAttribute(SYMBOLOGY) {
			@Override
			public void setValue(final String name, final String value) {
				symbology = value;
			}
		});

		addAttributeHandler(new HandleAttribute(A_TYPE) {
			@Override
			public void setValue(final String name, final String value) {
				final AttributeTypePropertyEditor pe = new AttributeTypePropertyEditor();
				pe.setAsText(value);
				attrType = (int) pe.getValue();
			}
		});

		addAttributeHandler(new HandleIntegerAttribute(INTERVAL) {
			@Override
			public void setValue(final String name, final int value) {
				interval = value;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(REGULAR_INTERVALS) {
			@Override
			public void setValue(final String name, final boolean value) {
				regularIntervals = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ACTIVE) {
			@Override
			public void setValue(final String name, final boolean value) {
				active = value;
			}
		});

	}

	abstract public void addFormatter(MWC.GUI.Editable editable);

	@Override
	public void elementClosed() {
		// create the object
		final CoreFormatItemListener listener = new CoreFormatItemListener(fName, layerName, symbology, interval,
				regularIntervals, attrType, active);

		addFormatter(listener);

		// reset the local parameters
		fName = null;
		layerName = null;
		symbology = null;
		interval = 10000000;
		regularIntervals = false;
		attrType = 1;
		active = true;
	}

}