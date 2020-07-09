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

package ASSET.Util.XML.Control.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Recording.CSVExportStatusObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

/**
 * read in a debrief replay observer from file
 */
abstract class CSVExportStatusObserverHandler extends CoreFileObserverHandler {

	private final static String type = "CSVStatusObserver";

	private static final String SUBJECT_NAME = "SubjectName";
	private static final String TARGET_TYPE = "SubjectToTrack";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final DebriefReplayObserver bb = (DebriefReplayObserver) toExport;

		// output the parent ttributes
		CoreFileObserverHandler.exportThis(bb, thisPart);

		// output it's attributes
		if (bb.getSubjectToTrack() != null) {
			TargetTypeHandler.exportThis(TARGET_TYPE, bb.getSubjectToTrack(), thisPart, doc);
		}

		// output it's attributes
		parent.appendChild(thisPart);

	}

	TargetType _targetType = null;

	String _subjectName = null;

	public CSVExportStatusObserverHandler() {
		this(type);
	}

	public CSVExportStatusObserverHandler(final String type) {
		super(type);

		addHandler(new TargetTypeHandler(TARGET_TYPE) {
			@Override
			public void setTargetType(final TargetType type1) {
				_targetType = type1;
			}
		});
		addAttributeHandler(new HandleAttribute(SUBJECT_NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_subjectName = val;
			}
		});
	}

	@Override
	public void elementClosed() {
		// create ourselves
		final ScenarioObserver debriefObserver = getObserver(_name, _isActive, _targetType, _subjectName);

		setObserver(debriefObserver);

		// close the parenet
		super.elementClosed();

		// and clear the data
		_targetType = null;

	}

	protected ScenarioObserver getObserver(final String name, final boolean isActive, final TargetType subject,
			final String subjectName) {
		return new CSVExportStatusObserver(_directory, _fileName, subject, name, isActive, subjectName);
	}

	@Override
	abstract public void setObserver(ScenarioObserver obs);

}