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

package org.mwc.debrief.core.wizards;

import java.beans.PropertyDescriptor;
import java.text.ParseException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;
import org.osgi.service.prefs.Preferences;

import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class EnterSolutionPage extends CoreEditableWizardPage {
	public static class SolutionDataItem implements Editable {
		public WorldSpeed _speed = new WorldSpeed(0, WorldSpeed.Kts);
		public double _course = 0;

		public double getCourse() {
			return _course;
		}

		@Override
		public EditorType getInfo() {
			return null;
		}

		@Override
		public String getName() {
			return "Local solution";
		}

		public WorldSpeed getSpeed() {
			return _speed;
		}

		@Override
		public boolean hasEditor() {
			return false;
		}

		public void setCourse(final double course) {
			_course = course;
		}

		public void setSpeed(final WorldSpeed speed) {
			_speed = speed;
		}

	}

	private static final String COURSE = "COURSE";

	private static final String SPEED = "SPEED";

	public static String NAME = "Initial SOLUTION";

	SolutionDataItem _myWrapper;

	Text secondNameText;

	final private String NULL_SPEED = "5,6";

	final private WorldSpeed _defaultSpeed;

	final private double _defaultCourse;

	public EnterSolutionPage(final ISelection selection, final String pageTitle, final String pageDescription,
			final String imagePath, final String helpContext, final WorldSpeed defaultSpeed,
			final double defaultCourse) {
		super(selection, NAME, pageTitle, pageDescription, imagePath, helpContext, false, null);

		_myWrapper = new SolutionDataItem();
		_defaultSpeed = defaultSpeed;
		_defaultCourse = defaultCourse;

		setDefaults();
	}

	@Override
	public Editable createMe() {
		return _myWrapper;
	}

	@Override
	public void dispose() {
		// try to store some defaults
		final Preferences prefs = getPrefs();

		prefs.putDouble(COURSE, _myWrapper.getCourse());
		final String spdTxt = _myWrapper.getSpeed().getValue() + "," + _myWrapper.getSpeed().getUnits();
		prefs.put(SPEED, spdTxt);

		super.dispose();
	}

	@Override
	protected PropertyDescriptor[] getPropertyDescriptors() {
		final PropertyDescriptor[] descriptors = { prop("Course", "the initial estimate of course", getEditable()),
				prop("Speed", "the initial estimate of speed", getEditable()) };
		return descriptors;
	}

	private void setDefaults() {
		final Preferences prefs = getPrefs();

		boolean assigned = false;

		if (prefs != null) {
			final double course = prefs.getDouble(COURSE, Double.NaN);
			if (!Double.isNaN(course)) {
				final String speedStr = prefs.get(SPEED, NULL_SPEED);
				final String[] parts = speedStr.split(",");
				try {
					final double val = MWCXMLReader.readThisDouble(parts[0]);
					final int units = Integer.parseInt(parts[1]);
					final WorldSpeed speed = new WorldSpeed(val, units);
					_myWrapper.setCourse(course);
					_myWrapper.setSpeed(speed);
					assigned = true;
				} catch (final ParseException e) {
					MWC.Utilities.Errors.Trace.trace(e);
				}
			}
		}

		if (!assigned) {
			_myWrapper.setSpeed(_defaultSpeed);
			_myWrapper.setCourse(_defaultCourse);
		}
	}

}
