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

package ASSET.Models.Decision.Conditions;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

public class Detection extends Condition.CoreCondition {
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////

	/**
	 * ************************************************* editor support
	 * *************************************************
	 */

	static public class DetectionInfo extends Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public DetectionInfo(final Detection data) {
			super(data, data.getName(), "Detection Condition");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this condition"),
						prop("TargetType", "the type of target we're looking for"),
						prop("RangeThreshold", "the minimum range at which to trigger detection"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * ************************************************* testing
	 * *************************************************
	 */
	static public class DetectionTest extends SupportTesting.EditableTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public DetectionTest(final String name) {
			super(name);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			final Detection theDet = new Detection(null, null);
			return theDet;
		}

		public void testIt() {
			// build it

			final Category firstTarget = new Category(Category.Force.RED, Category.Environment.SURFACE,
					Category.Type.CARRIER);
			final Category secondTarget = new Category(Category.Force.RED, Category.Environment.SURFACE,
					Category.Type.AV_MISSILE);
			final Category thirdTarget = new Category(Category.Force.RED, Category.Environment.SURFACE,
					Category.Type.AV_MISSILE);

			final ASSET.ParticipantType participant = new ASSET.Models.Vessels.Surface(12);
			participant.setName("un-named");
			final CoreSensor sensor = new ASSET.Models.Sensor.Initial.BroadbandSensor(12);
			final DetectionEvent da = new DetectionEvent(110, participant.getId(), null, sensor,
					new WorldDistance(4, WorldDistance.YARDS), new WorldDistance(4, WorldDistance.YARDS), null, null,
					new Float(50), firstTarget, null, null, participant);
			final DetectionEvent db = new DetectionEvent(120, participant.getId(), null, sensor,
					new WorldDistance(1, WorldDistance.YARDS), new WorldDistance(4, WorldDistance.YARDS), null, null,
					new Float(60), secondTarget, null, null, participant);
			final DetectionEvent dc = new DetectionEvent(130, participant.getId(), null, sensor,
					new WorldDistance(4, WorldDistance.YARDS), new WorldDistance(4, WorldDistance.YARDS), null, null,
					new Float(70), thirdTarget, null, null, participant);

			// build up the list
			DetectionList dl = new DetectionList();

			// try with no detections

			final TargetType tt = new TargetType();
			tt.addTargetType(Category.Type.AV_MISSILE);

			Detection detection = new Detection(tt, null);
			DetectionEvent de = (DetectionEvent) detection.test(null, dl, 0, null);
			assertNull("no success with empty list", de);

			// add the first (invalid item)
			dl.add(da);
			de = (DetectionEvent) detection.test(null, dl, 0, null);
			assertNull("no success with invalid contact", de);

			// add the second (valid item)
			dl.add(db);
			de = (DetectionEvent) detection.test(null, dl, 0, null);
			assertNotNull("success with valid contact", de);

			// add the third (valid, and closer item)
			dl.add(dc);
			de = (DetectionEvent) detection.test(null, dl, 0, null);
			assertNotNull("success with valid contact", de);
			assertEquals("strongest target found", de.getStrength().floatValue(), 70, 0.02);

			//////////////////////////////////////////////////////////////
			// now include a range threshold
			//////////////////////////////////////////////////////////////
			detection = new Detection(tt, new WorldDistance(2, WorldDistance.DEGS));
			dl = new DetectionList();
			de = (DetectionEvent) detection.test(null, dl, 0, null);
			assertNull("no success with empty list", de);

			// add the first (invalid item)
			dl.add(da);
			de = (DetectionEvent) detection.test(null, dl, 0, null);
			assertNull("no success with invalid contact", de);

			// add the second (valid item)
			dl.add(db);
			de = (DetectionEvent) detection.test(null, dl, 0, null);
			assertNotNull("success with valid contact", de);

			// add the third (valid, and closer item)
			dl.add(dc);
			de = (DetectionEvent) detection.test(null, dl, 0, null);
			assertNotNull("success with valid contact", de);
			assertEquals("target with detection in range", de.getStrength().floatValue(), 70, 0.02);

		}
	}

	////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////

	/**
	 * the TargetType of target we're looking for
	 */
	private TargetType _theTargetType = null;

	/**
	 * a minimum range to trigger
	 */
	private WorldDistance _rangeThreshold = null;

	/**
	 * @param theTargetType the type of target we're looking for
	 * @param theThreshold  the threshold range at which detections are valid (or
	 *                      null for all detections)
	 */
	public Detection(final TargetType theTargetType, final WorldDistance theThreshold) {
		super("Detection");

		this._theTargetType = theTargetType;
		_rangeThreshold = theThreshold;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new DetectionInfo(this);

		return _myEditor;
	}

	public WorldDistance getRangeThreshold() {
		return _rangeThreshold;
	}

	public TargetType getTargetType() {
		return _theTargetType;
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

	////////////////////////////////////////////////////
	// editor fields
	////////////////////////////////////////////////////

	@Override
	public void restart() {
		// don't bother, we don't react to it.
	}

	public void setRangeThreshold(final WorldDistance rangeThreshold) {
		this._rangeThreshold = rangeThreshold;
	}

	public void setTargetType(final TargetType theTargetType) {
		this._theTargetType = theTargetType;
	}

	////////////////////////////////////////////////////
	// condition fields
	////////////////////////////////////////////////////
	@Override
	public Object test(final Status status, final DetectionList detections, final long time,
			final ScenarioActivityMonitor monitor) {

		DetectionEvent res = null;

		double theStrength = -1;

		// loop through the detections
		final int len = detections.size();
		for (int i = 0; i < len; i++) {
			DetectionEvent de = detections.getDetection(i);
			final Category tt = de.getTargetType();
			// see if this matches our category
			if (_theTargetType.matches(tt)) {
				// are we checking range?
				if (_rangeThreshold != null) {
					// yes, checking range. Do we know his range?
					if (de.getRange() != null) {
						final double rng_degs = de.getRange().getValueIn(WorldDistance.DEGS);

						// is this within the threshold?
						if (rng_degs <= _rangeThreshold.getValueIn(WorldDistance.DEGS)) {
							// ok, continue as we are
						} else {
							// outside range, ignore this contact
							de = null;
						}
					} else {
						// we don't know his range, ignore this contact
						de = null;
					}
				}

				// do we still have a target? We may have ditched it if
				// it's out of range
				if (de != null) {
					// not checking range, just continue

					// what's the strength of this detection?
					final float thisStrength = de.getStrength().floatValue();

					// yes, it's ok, is this the strongest?
					if (theStrength == -1) {
						res = de;
						theStrength = thisStrength;
					} else if (thisStrength > theStrength) {
						res = de;
						theStrength = thisStrength;
					}
				}
			}
		}

		// ok, done now
		return res;
	}

}
