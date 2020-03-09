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

package ASSET.GUI.Painters.Detections;

import java.awt.Point;

import ASSET.ParticipantType;
import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Category;
import ASSET.Util.SupportTesting;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * class which takes the participants currently in the scenario, and paints any
 * detections which they have at their current status. It does not implement the
 * "DetectionListener" for particpants, but it does store the scenario object
 * from which it takes the current list of participants. At each update it
 * conducts a pass through the participants and draws in any detections.
 */

public class ScenarioDetectionPainter implements Plottable {

	/****************************************************
	 * member objects
	 ***************************************************/

	public static class PainterTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new ScenarioDetectionPainter(null);
		}
	}

	/**
	 * ************************************************* radiated noise levels
	 * *************************************************
	 */
	static public class ScenarioDetectionInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public ScenarioDetectionInfo(final ScenarioDetectionPainter data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Visible", "whether to paint detections"),
						prop("DefaultRange", "the default range to paint detections"),
						prop("PlotMedium", "whether to label with detection medium"),
						prop("PlotDetectionState", "whether to label with detection state"),
						prop("OnlyPlotOtherForce", "whether to only plot detections of opposing forces"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				System.err.println("PROBLEM WITH PROPERTY EDITOR FOR: " + super.getData());
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * the scenario we are watching
	 */
	private final ASSET.ScenarioType _myScenario;

	/**
	 * whether we are visible
	 */
	private boolean _isVisible = false;

	/**
	 * whether to plot the medium
	 */
	private boolean _plotMedium = true;

	/**
	 * plot level of detection
	 *
	 */
	private boolean _plotDetectionState = false;

	/**
	 * whether to only plot detections of opposing forces
	 */
	private boolean _onlyPlotOtherForce = true;

	/**
	 * the editor for radiated noise levels
	 */
	private MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * the default range to plot
	 */
	private WorldDistance _defaultRange = new WorldDistance(10, WorldDistance.NM);

	/**
	 * helper to convert detection state into text
	 *
	 */
	DetectionEvent.DetectionStatePropertyEditor _stateHelper = new DetectionEvent.DetectionStatePropertyEditor();

	/**
	 * ************************************************* constructor
	 * *************************************************
	 */
	public ScenarioDetectionPainter(final ASSET.ScenarioType scenario) {
		_myScenario = scenario;
	}

	@Override
	public int compareTo(final Plottable arg0) {
		final ScenarioDetectionInfo other = (ScenarioDetectionInfo) arg0;
		return getName().compareTo(other.getName());
	}

	/****************************************************
	 * member methods
	 ***************************************************/
	/**
	 * find the data area occupied by this item
	 */
	@Override
	public WorldArea getBounds() {
		return null;
	}

	/**
	 * the default range to plot bearing lines
	 */
	public WorldDistance getDefaultRange() {
		return _defaultRange;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new ScenarioDetectionInfo(this);

		return _myEditor;
	}

	/**
	 * the name of this object
	 *
	 * @return the name of this editable object
	 */
	@Override
	public String getName() {
		return "Detections Painter";
	}

	/**
	 * only plot the opposing forces
	 *
	 * @return
	 */
	public boolean getOnlyPlotOtherForce() {
		return _onlyPlotOtherForce;
	}

	/**
	 * whether to plot the detection state
	 *
	 * @return
	 */
	public boolean getPlotDetectionState() {
		return _plotDetectionState;
	}

	/**
	 * it this item currently visible?
	 */
	@Override
	public boolean getVisible() {
		return _isVisible;
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * whether to plot the medium
	 */
	public boolean isPlotMedium() {
		return _plotMedium;
	}

	/**
	 * paint this object to the specified canvas
	 */
	@Override
	public void paint(final CanvasType dest) {
		// are we visible?
		if (!_isVisible)
			return;

		// get the participants
		final Integer[] list = _myScenario.getListOfParticipants();
		for (int i = 0; i < list.length; i++) {
			final Integer integer = list[i];
			final ParticipantType pt = _myScenario.getThisParticipant(integer.intValue());

			// get any detections
			ASSET.Models.Detection.DetectionList partDetections = pt.getNewDetections();

			// strip out friendly detections if requested
			if (_onlyPlotOtherForce) {
				final ASSET.Models.Detection.DetectionList newList = new ASSET.Models.Detection.DetectionList();

				final int len = partDetections.size();

				for (int pp = 0; pp < len; pp++) {
					final DetectionEvent de = partDetections.getDetection(pp);
					if (de.getTargetType().getForce().equals(pt.getCategory().getForce())) {
						// same force, ditch it
					} else {
						// store it
						newList.add(de);
					}
				}

				// ok, we've built it up
				partDetections = newList;
			}

			// ok, we've finished for this participant, plot them
			paintDetectionsFor(dest, partDetections, pt);
		}
	}

	private void paintDetectionsFor(final CanvasType dest, final DetectionList detections,
			final ParticipantType thisPart) {
		final int size = detections.size();
		final WorldLocation origin = thisPart.getStatus().getLocation();

		if (origin == null) {
			System.out.println("NO ORIGIN FOR PARTICIPANT:" + thisPart);
			return;
		}

		for (int i = 0; i < size; i++) {
			final DetectionEvent de = detections.getDetection(i);
			// do we have bearing?
			if (de.getBearing() != null) {
				WorldDistance theRange = null;

				// yes, carry on!
				if (de.getRange() != null) {
					// ok, we can plot a full range
					theRange = new WorldDistance(de.getRange());
				} else {
					// plot with the default range
					theRange = _defaultRange;
				}

				// calculate the end point
				final float theBrg = de.getBearing().floatValue();
				final WorldVector vector = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(theBrg),
						theRange.getValueIn(WorldDistance.DEGS), 0);
				final WorldLocation otherEnd = origin.add(vector);

				// set the color
				dest.setColor(Category.getColorFor(thisPart.getCategory()));

				// plot the bearing line
				final Point theStart = new Point(dest.toScreen(origin));
				final Point theEnd = dest.toScreen(otherEnd);

				dest.drawLine(theStart.x, theStart.y, theEnd.x, theEnd.y);

				// work out half way along the line
				final Point midPoint = new Point(theStart.x + (theEnd.x - theStart.x) / 2,
						theStart.y + (theEnd.y - theStart.y) / 2);

				// create random X and Y offsets
				// int x_offset = -5 + (int)(Math.random() * 10d);
				final int x_offset = 0;
				int y_offset = 0;

				// do we plot the strength?
				if (_plotMedium || _plotDetectionState) {
					final int sensor = de.getSensor();
					final SensorType thisSensor = thisPart.getSensorAt(sensor);
					String desc = null;
					if (_plotMedium) {
						desc = thisSensor.getName();
						dest.drawText(desc, midPoint.x + 3 + x_offset, midPoint.y + y_offset);
					}
					if (_plotDetectionState) {

						if (_plotMedium) {
							y_offset += 10;
						}

						_stateHelper.setIndex(de.getDetectionState());
						desc = _stateHelper.getAsText();
						dest.drawText(desc, midPoint.x + 3 + x_offset, midPoint.y + y_offset);
					}

				}
			}
		}
	}

	/**
	 * how far away are we from this point? or return INVALID_RANGE if it can't be
	 * calculated
	 */
	@Override
	public double rangeFrom(final WorldLocation other) {
		return INVALID_RANGE;
	}

	/**
	 * the default range to plot bearing lines
	 */
	public void setDefaultRange(final WorldDistance defaultRange) {
		this._defaultRange = defaultRange;
	}

	/**
	 * only plot the opposing forces
	 *
	 * @param onlyPlotOtherForce yes/no
	 */
	public void setOnlyPlotOtherForce(final boolean onlyPlotOtherForce) {
		_onlyPlotOtherForce = onlyPlotOtherForce;
	}

	/**
	 * whether to plot the detection state
	 *
	 * @param plotDetectionState
	 */
	public void setPlotDetectionState(final boolean plotDetectionState) {
		this._plotDetectionState = plotDetectionState;
	}

	/**
	 * whether to plot the medium
	 */
	public void setPlotMedium(final boolean plotMedium) {
		this._plotMedium = plotMedium;
	}

	/**
	 * set the visibility of this item
	 */
	@Override
	public void setVisible(final boolean val) {
		_isVisible = val;
	}

	/**
	 * return this object as a string
	 */
	@Override
	public String toString() {
		return getName();
	}

}
