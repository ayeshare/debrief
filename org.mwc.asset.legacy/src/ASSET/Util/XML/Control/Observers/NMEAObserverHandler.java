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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Recording.DebriefFormatHelperHandler;
import ASSET.Scenario.Observers.Recording.NMEAObserver;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

/**
 * read in a debrief replay observer from file
 */
abstract class NMEAObserverHandler extends CoreFileObserverHandler {

	private final static String type = "NMEAObserver";

	private static final String RECORD_DETECTIONS = "record_detections";
	private static final String RECORD_POSITIONS = "record_positions";
	private static final String TARGET_TYPE = "SubjectToTrack";
	private static final String SUBJECT_SENSOR = "SubjectSensor";

	static public void exportThis(final Object toExport, final Element parent, final org.w3c.dom.Document doc) {
		// create ourselves
		final Element thisPart = doc.createElement(type);

		// get data item
		final NMEAObserver bb = (NMEAObserver) toExport;

		// output the parent ttributes
		CoreFileObserverHandler.exportThis(bb, thisPart);

		// output it's attributes
		thisPart.setAttribute(RECORD_DETECTIONS, writeThis(bb.getRecordDetections()));
		thisPart.setAttribute(RECORD_POSITIONS, writeThis(bb.getRecordPositions()));
		if (bb.getSubjectToTrack() != null) {
			TargetTypeHandler.exportThis(TARGET_TYPE, bb.getSubjectToTrack(), thisPart, doc);
		}

		// output it's attributes
		parent.appendChild(thisPart);

	}

	private boolean _recordDetections = false;
	private boolean _recordPositions = false;
	private TargetType _targetType = null;
	final private List<String> _formatHelpers = new ArrayList<String>();

	private String _subjectSensor = null;

	public NMEAObserverHandler() {
		this(type);
	}

	public NMEAObserverHandler(final String type) {
		super(type);

		addAttributeHandler(new HandleBooleanAttribute(RECORD_DETECTIONS) {
			@Override
			public void setValue(final String name, final boolean val) {
				_recordDetections = val;
			}
		});
		addAttributeHandler(new HandleAttribute(SUBJECT_SENSOR) {
			@Override
			public void setValue(final String name, final String val) {
				_subjectSensor = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(RECORD_POSITIONS) {
			@Override
			public void setValue(final String name, final boolean val) {
				_recordPositions = val;
			}
		});

		addHandler(new TargetTypeHandler(TARGET_TYPE) {
			@Override
			public void setTargetType(final TargetType type1) {
				_targetType = type1;
			}
		});
		addHandler(new DebriefFormatHelperHandler() {
			@Override
			public void storeMe(final String text) {
				_formatHelpers.add(text);
			}
		});
	}

	@Override
	public void elementClosed() {
		// create ourselves
		final NMEAObserver debriefObserver = getObserver(_name, _isActive, _recordDetections, _recordPositions,
				_targetType, _formatHelpers, _subjectSensor);

		setObserver(debriefObserver);

		// close the parent
		super.elementClosed();

		// and clear the data
		_recordDetections = false;
		_recordPositions = true;
		_targetType = null;
		_subjectSensor = null;

		// and clear the format helpers
		_formatHelpers.clear();
	}

	protected NMEAObserver getObserver(final String name, final boolean isActive, final boolean recordDetections,
			final boolean recordPositions, final TargetType subject, final List<String> formatHelpers,
			final String subjectSensor) {
		return new NMEAObserver(_directory, _fileName, recordDetections, recordPositions, subject, name, isActive,
				subjectSensor);
	}

	@Override
	abstract public void setObserver(ScenarioObserver obs);

}