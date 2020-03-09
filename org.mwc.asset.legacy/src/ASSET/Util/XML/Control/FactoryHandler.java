
package ASSET.Util.XML.Control;

import java.util.Vector;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ASSET.Scenario.Observers.ScenarioObserver;

abstract public class FactoryHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	private final static String type = "Factory";
	private final static String STARS = "Stars";
	private final static String GENES = "Genes";
	private final static String LOW_SCORES_HIGH = "LowScoresHigh";

	public static void exportThis(final Vector<ScenarioObserver> list, final int stars, final int genes,
			final Element parent, final Document doc) {
		// create ourselves
		final Element sens = doc.createElement(type);

		//
		sens.setAttribute(GENES, writeThis(genes));
		sens.setAttribute(STARS, writeThis(stars));

		// and the list
		ASSET.Util.XML.Control.Observers.ObserverListHandler.exportThis(list, sens, doc);

		parent.appendChild(sens);

	}

	int _genes;
	int _stars;
	private Vector<ScenarioObserver> _myList;

	boolean _lowScoresHigh = false;

	public FactoryHandler() {
		// inform our parent what type of class we are
		super(type);

		addHandler(new ASSET.Util.XML.Control.Observers.ObserverListHandler() {
			@Override
			public void setObserverList(final Vector<ScenarioObserver> list) {
				_myList = new Vector<ScenarioObserver>(list);
			}
		});

		addAttributeHandler(new HandleIntegerAttribute(GENES) {
			@Override
			public void setValue(final String name, final int value) {
				_genes = value;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(LOW_SCORES_HIGH) {
			@Override
			public void setValue(final String name, final boolean value) {
				_lowScoresHigh = value;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(STARS) {
			@Override
			public void setValue(final String name, final int value) {
				_stars = value;
			}
		});

	}

	@Override
	public void elementClosed() {
		setFactory(_myList, _genes, _stars, _lowScoresHigh);

		_myList = null;
		_genes = -1;
		_stars = -1;
		_lowScoresHigh = false;
	}

	abstract public void setFactory(Vector<ScenarioObserver> list, int genes, int stars, boolean lowScoresHigh);

}