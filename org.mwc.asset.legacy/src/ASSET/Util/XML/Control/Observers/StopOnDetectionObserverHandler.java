
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

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.DetectionObserver;

abstract public class StopOnDetectionObserverHandler extends DetectionObserverHandler {

	private final static String type = "StopOnDetectionObserver";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(getType());

		// get data item
		final DetectionObserver.StopOnDetectionObserver bb = (DetectionObserver.StopOnDetectionObserver) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());
		thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));

		if (bb.getDetectionLevel() != null)
			thisPart.setAttribute(DETECTION_LEVEL, writeThis(bb.getDetectionLevel().intValue()));

		TargetHandler.exportThis(bb.getTargetType(), thisPart, doc, TARGET_TYPE);
		TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);

		// output it's attributes
		parent.appendChild(thisPart);

	}

	public StopOnDetectionObserverHandler() {
		this(type);
	}

	public StopOnDetectionObserverHandler(final String type) {
		super(type);
	}

	@Override
	protected DetectionObserver getObserver(final TargetType watch, final TargetType target, final String name,
			final Integer detectionLevel, final boolean isActive) {
		return new DetectionObserver.StopOnDetectionObserver(watch, target, name, detectionLevel, isActive);
	}
}