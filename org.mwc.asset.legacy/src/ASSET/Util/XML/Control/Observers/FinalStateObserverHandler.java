
package ASSET.Util.XML.Control.Observers;

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

import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Summary.EndOfRunBatchObserver;
import ASSET.Scenario.Observers.Summary.FinalStateObserver;

abstract class FinalStateObserverHandler extends CoreFileObserverHandler {

	private final static String type = "FinalStateObserver";

	/**
	 * export this observer
	 *
	 * @param toExport
	 * @param parent
	 * @param doc
	 */
	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		final EndOfRunBatchObserver bb = (EndOfRunBatchObserver) toExport;

		// export the parent fields
		CoreFileObserverHandler.exportThis(bb, thisPart);

		// get data item

		// do we have batch data?
		if (bb.getBatchHelper() != null)
			BatchCollatorHandler.exportCollator(bb.getBatchHelper(), bb.getBatchOnly(), thisPart, doc);

		// output it's attributes
		parent.appendChild(thisPart);

	}

	private final BatchCollatorHandler _myBatcher = new BatchCollatorHandler();

	public FinalStateObserverHandler() {
		super(type);

		addHandler(_myBatcher);
	}

	@Override
	public void elementClosed() {
		// create ourselves
		final EndOfRunBatchObserver timeO = new FinalStateObserver(super._directory, super._fileName, super._name,
				super._isActive);

		// (possibly) set batch collation results
		_myBatcher.setData(timeO);

		// and return the results
		setObserver(timeO);

		// and reset our parent data
		super.elementClosed();
	}

	/**
	 * all done, somebody store the observer
	 *
	 * @param obs
	 */
	@Override
	abstract public void setObserver(ScenarioObserver obs);

}