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

package ASSET.Models.Decision.Tactical;

import java.util.HashMap;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Decision.Movement.RectangleWander;
import ASSET.Models.Decision.Tactical.Investigate.InvestigatePatternFactory.InvestigatePattern;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * Our implementation of investigation. The host platform will close on a
 * candidate target until the required classification level is achieved, after
 * which it will break off.
 */

public class Investigate extends CoreDecision implements java.io.Serializable {
	static public class InvestigateInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public InvestigateInfo(final Investigate data) {
			super(data, data.getName(), "Investigate", true);
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("TargetType", "the type of vessel this model is evading"),
						prop("Name", "the name of this detonation model"),
						prop("DetectionLevel", "the name of this detonation model"), };
				res[2].setPropertyEditorClass(DetectionEvent.DetectionStatePropertyEditor.class);
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	static public class InvestigatePatternFactory {
		static public interface InvestigatePattern {

			SimpleDemandedStatus amendDemStatus(SimpleDemandedStatus res, Status status, Status tgtStat);

			boolean invComplete(DetectionEvent validDetection, int detectionLevel);

			void newTarget(int target);

		}

		static public class MatchDetectionLevel implements InvestigatePattern {

			@Override
			public SimpleDemandedStatus amendDemStatus(final SimpleDemandedStatus res, final Status status,
					final Status tgtStat) {
				// // are we close?
				// WorldVector dist =
				// tgtStat.getLocation().subtract(status.getLocation());
				// WorldDistance dd = new WorldDistance(dist.getRange(),
				// WorldDistance.DEGS);
				// if(dd.lessThan(new WorldDistance(2.5, WorldDistance.NM)))
				// {
				// // ok, match him for speed
				// res.setSpeed(tgtStat.getSpeed());
				// }
				//
				return res;
			}

			@Override
			public boolean invComplete(final DetectionEvent validDetection, final int detectionLevel) {
				// ok. have we reached the correct detection level?
				final int detState = validDetection.getDetectionState();
				final boolean res = (detState >= detectionLevel);
				return res;
			}

			@Override
			public void newTarget(final int target) {
			}

		}

		static InvestigatePattern getPatternFor(final String name) {
			final InvestigatePattern res = new MatchDetectionLevel();
			return res;
		}

	}

	/**
	 * convenience class for the per-scenario data storage
	 *
	 * @author ianmayo
	 *
	 */
	public static class InvestigateStore {
		static protected class MappedStores {
			private final HashMap<Integer, InvestigateStore> _myStore = new HashMap<Integer, InvestigateStore>();

			/**
			 * clear the list of stores
			 *
			 */
			public void clearStores() {
				_myStore.clear();
			}

			public InvestigateStore firstStore() {
				InvestigateStore res = null;
				if (_myStore.size() > 0)
					res = _myStore.values().iterator().next();
				return res;
			}

			/**
			 * find the right store for this type
			 *
			 * @param monitor
			 * @param id
			 * @param isCollab
			 * @return
			 */
			public InvestigateStore get(final ScenarioActivityMonitor monitor, final int id, final boolean isCollab) {
				InvestigateStore res = null;
				Integer index;
				if (isCollab)
					index = monitor.hashCode();
				else
					index = monitor.hashCode() * id;

				if (_myStore.get(index) != null) {
					res = _myStore.get(index);
				} else {
					res = new InvestigateStore();
					_myStore.put(index, res);
				}
				return res;
			}
		}

		public HashMap<Integer, Integer> currentTargets;
		public Vector<Integer> targetsDone;

		public InvestigateStore() {
			currentTargets = new HashMap<Integer, Integer>();
			targetsDone = new Vector<Integer>(5, 5);
		}

		/**
		 * remember we've spotted another target
		 *
		 * @param id
		 * @param myTarget
		 */
		public void addDoneTarget(final int id, final Integer myTarget) {
			targetsDone.add(myTarget);
		}

		public void clearCurrentTarget(final int id) {
			currentTargets.remove(id);
		}

		public Object countDoneTargets() {
			return targetsDone.size();
		}

		/**
		 * who are we after?
		 *
		 * @param id
		 * @return
		 */
		public Integer getCurrentTarget(final int id) {
			final Integer res = currentTargets.get(id);
			return res;
		}

		public boolean hasBeenDone(final Integer tgtId) {
			return targetsDone.contains(tgtId);
		}

		public boolean hasCurrentTarget(final Integer myId) {
			return currentTargets.get(myId) != null;
		}

		public boolean isACurrentTarget(final Integer tgtId) {
			final boolean res = currentTargets.containsValue(tgtId);
			// is anybody looking at this
			return res;
		}

		public void setCurrentTarget(final int myId, final int hisId) {
			if (currentTargets.get(myId) != null)
				currentTargets.remove(myId);

			currentTargets.put(myId, hisId);
		}
	}

	public static final class InvestigateTest extends SupportTesting.EditableTesting {
		boolean hasStopped = false;

		public InvestigateTest(final String val) {
			super(val);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			final TargetType theTarget = new TargetType(Category.Force.RED);
			final Investigate investigate = new Investigate("investigating red targets", theTarget,
					DetectionEvent.IDENTIFIED, null);
			return investigate;
		}

		// TODO FIX-TEST
		public void NtestCollaborative() {
			final TargetType theWatch1 = new TargetType(Category.Type.MPA);
			final TargetType theTarget1 = new TargetType(Category.Force.RED);
			final Investigate investigate1 = new Investigate("investigating red targets near blue", theTarget1,
					theWatch1, DetectionEvent.IDENTIFIED, null);
			investigate1.setCollaborativeSearch(true);

			final TargetType theWatch2 = new TargetType(Category.Type.MPA);
			final TargetType theTarget2 = new TargetType(Category.Force.RED);
			final Investigate investigate2 = new Investigate("other investigating red targets near blue", theTarget2,
					theWatch2, DetectionEvent.IDENTIFIED, null);
			investigate2.setCollaborativeSearch(true);

			final int TGT1_ID = 567;
			final int TGT2_ID = 568;
			final int BLUE_ID1 = 34;
			final int BLUE_ID2 = 46;
			final int NOT_TGT_ID = 123;

			final Status blueStat1 = new Status(BLUE_ID1, 0);
			blueStat1.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
			blueStat1.setLocation(new WorldLocation(4, 4, 2));
			final ParticipantType blueP1 = new CoreParticipant(BLUE_ID1);
			blueP1.setStatus(blueStat1);
			final Category blueCat1 = new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
					Category.Type.MPA);
			blueP1.setCategory(blueCat1);

			final Status blueStat2 = new Status(BLUE_ID2, 0);
			blueStat2.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
			blueStat2.setLocation(new WorldLocation(4, 4, 2));
			final ParticipantType blueP2 = new CoreParticipant(BLUE_ID2);
			blueP2.setStatus(blueStat2);
			final Category blueCat2 = new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
					Category.Type.MPA);
			blueP2.setCategory(blueCat2);

			final Status tgt1Stat = new Status(222, 0);
			tgt1Stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			tgt1Stat.setLocation(new WorldLocation(2, 2.5, 2));
			tgt1Stat.setCourse(22);

			final Status tgt2Stat = new Status(222, 0);
			tgt2Stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			tgt2Stat.setLocation(new WorldLocation(4, 2.5, 2));
			tgt2Stat.setCourse(22);

			final MovementCharacteristics theChars = HeloMovementCharacteristics.getSampleChars();
			final SimpleDemandedStatus theDemStat = new SimpleDemandedStatus(12, 12000);
			DetectionList theDetections = null;

			final LookupSensor duffSensor = new RadarLookupSensor(12, 0, 0, 0, 0, null, 0, null, 0);

			final Surface target1 = new Surface(TGT1_ID, null, null, "fisher");
			target1.setStatus(tgt1Stat);

			final Surface target2 = new Surface(TGT2_ID, null, null, "fisher");
			target2.setStatus(tgt2Stat);

			final ScenarioActivityMonitor theMonitor = new ScenarioActivityMonitor() {
				@Override
				public void createParticipant(final ParticipantType newPart) {
				}

				@Override
				public void detonationAt(final int id, final WorldLocation loc, final double power) {
				}

				@Override
				public Integer[] getListOfParticipants() {
					return new Integer[] { BLUE_ID1, TGT1_ID, TGT2_ID };
				}

				@Override
				public ParticipantType getThisParticipant(final int id)

				{
					ParticipantType res = null;
					switch (id) {
					case BLUE_ID1:
						res = blueP1;
						break;
					case TGT1_ID:
						res = target1;
						break;
					case TGT2_ID:
						res = target2;
						break;
					}
					return res;
				}
			};

			DemandedStatus res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			SimpleDemandedStatus simple = (SimpleDemandedStatus) res;
			assertNull("null dem stat when null detections", res);

			// ok. now use zero length detections
			theDetections = new DetectionList();

			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when empty detections", res);
			res = investigate2.decide(blueStat2, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when empty detections", res);

			// and put in an invalid detection
			final Category tgtCategory = new Category(Category.Force.BLUE, Category.Environment.SURFACE,
					Category.Type.FISHING_VESSEL);
			final double target_brg = 12;
			final DetectionEvent de = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
					null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target1,
					DetectionEvent.DETECTED);
			theDetections.add(de);
			final DetectionEvent de2 = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
					null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target2,
					DetectionEvent.DETECTED);
			theDetections.add(de2);

			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when invalid target", res);
			final InvestigateStore theStore1 = Investigate._invData.get(theMonitor, BLUE_ID1,
					investigate1.isCollaborativeSearch());
			assertNull("inv target still empty", theStore1.getCurrentTarget(BLUE_ID1));

			res = investigate2.decide(blueStat2, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when invalid target", res);
			final InvestigateStore theStore2 = Investigate._invData.get(theMonitor, BLUE_ID2,
					investigate2.isCollaborativeSearch());
			assertNull("inv target still empty", theStore2.getCurrentTarget(BLUE_ID2));

			// now try a valid target
			tgtCategory.setForce(Category.Force.RED);
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);
			assertNotNull("inv target not still empty", theStore1.getCurrentTarget(BLUE_ID1));
			assertEquals("inv target not still empty", theStore1.getCurrentTarget(BLUE_ID1).intValue(), TGT2_ID);

			// check that the other searcher spots the other target
			tgtCategory.setForce(Category.Force.RED);
			res = investigate2.decide(blueStat2, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);
			assertNotNull("inv target not still empty", theStore2.getCurrentTarget(BLUE_ID2));
			assertEquals("inv target not still empty", theStore2.getCurrentTarget(BLUE_ID2).intValue(), TGT1_ID);

			// now make it so there's no valid watch
			blueCat1.setType(Category.Type.MINISUB);
			theStore1.clearCurrentTarget(BLUE_ID1);
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("dem stat when valid target", res);
			assertNull("inv target not still empty", theStore1.getCurrentTarget(BLUE_ID1));

			// and put back our watched item
			blueCat1.setType(Category.Type.MPA);
			theStore1.clearCurrentTarget(12);
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);
			assertNotNull("inv target not still empty", theStore1.getCurrentTarget(BLUE_ID1));

			// ok. let's lose the target and see what happens
			theDetections.clear();
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when target lost", res);
			assertNotNull("remembered target", theStore1.getCurrentTarget(BLUE_ID1));
			assertEquals("got correct tgt id", TGT2_ID, theStore1.getCurrentTarget(BLUE_ID1).intValue());

			// and offer another target
			theDetections.add(de);
			de.setTarget(NOT_TGT_ID);
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("no dem stat when valid target", res);

			// back to our target
			de.setTarget(TGT1_ID);
			theDetections.add(de2);
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			simple = (SimpleDemandedStatus) res;
			assertNotNull("new dem stat when valid target", res);
			assertTrue("on a good bearing", simple.getCourse() > 0);
			assertEquals("got new tgt id", TGT2_ID, theStore1.getCurrentTarget(BLUE_ID1).intValue());

			// check that we can tick off target when found
			de2.setDetectionState(DetectionEvent.CLASSIFIED);
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);
			assertEquals("got new tgt id", TGT2_ID, theStore1.getCurrentTarget(BLUE_ID1).intValue());

			// check that we can tick off target when found
			de2.setDetectionState(DetectionEvent.IDENTIFIED);
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("no dem status", res);
			assertNull("ditched current target", theStore1.getCurrentTarget(BLUE_ID1));
			assertEquals("got something in found targets", 1, theStore1.countDoneTargets());

			// check that we can tick off target when found
			de.setDetectionState(DetectionEvent.IDENTIFIED);
			res = investigate2.decide(blueStat2, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("no dem status", res);
			assertNull("ditched current target", theStore2.getCurrentTarget(BLUE_ID2));
			assertEquals("got something in found targets", 2, theStore2.countDoneTargets());

			// check that we can tick off other target when found
			de2.setDetectionState(DetectionEvent.CLASSIFIED);
			res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("no dem status when only existing target found", res);
			assertNull("ditched current target", theStore1.getCurrentTarget(BLUE_ID1));
			assertEquals("got something in found targets", 2, theStore1.countDoneTargets());

		}

		// TODO FIX-TEST
		public void NtestRunning() {
			// note, for test purposes we need to clear the list of stores, since
			// our debug accessor just fetches the first store
			Investigate._invData.clearStores();

			final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
			topLeft.setDepth(-1000);
			final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
			bottomRight.setDepth(1000);
			final WorldArea theArea = new WorldArea(topLeft, bottomRight);
			final RectangleWander heloWander = new RectangleWander(theArea, "rect wander");
			heloWander.setSpeed(new WorldSpeed(20, WorldSpeed.Kts));
			final RectangleWander fishWander = new RectangleWander(theArea, "rect wander 2");

			RandomGenerator.seed(12);

			final Waterfall searchPattern = new Waterfall();
			searchPattern.setName("Searching");

			final TargetType theTarget = new TargetType(Category.Force.RED);
			final Investigate investigate = new Investigate("investigating red targets", theTarget,
					DetectionEvent.IDENTIFIED, null);
			searchPattern.insertAtHead(investigate);
			searchPattern.insertAtFoot(heloWander);

			final Status heloStat = new Status(1, 0);
			heloStat.setLocation(SupportTesting.createLocation(2000, 4000));
			heloStat.getLocation().setDepth(-200);
			heloStat.setCourse(270);
			heloStat.setSpeed(new WorldSpeed(100, WorldSpeed.Kts));

			final Status fisherStat = new Status(1, 0);
			fisherStat.setLocation(SupportTesting.createLocation(4000, 2000));
			fisherStat.setCourse(155);
			fisherStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

			final DemandedStatus dem = null;

			final SimpleDemandedStatus ds = (SimpleDemandedStatus) heloWander.decide(heloStat, null, dem, null, null,
					100);

			assertNotNull("dem returned", ds);

			// ok. the radar first
			final ASSET.Models.Sensor.Lookup.RadarLookupSensor radar = new RadarLookupSensor(12, 0.04, 11000, 1.2, 0,
					new Duration(0, Duration.SECONDS), 0, new Duration(0, Duration.SECONDS), 9200);

			final ASSET.Models.Sensor.Lookup.OpticLookupSensor optic = new OpticLookupSensor(333, 0.05, 10000, 1.05,
					0.8, new Duration(20, Duration.SECONDS), 0.2, new Duration(30, Duration.SECONDS));

			final Helo helo = new Helo(23);
			helo.setName("Merlin");
			helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
			helo.setStatus(heloStat);
			helo.setDecisionModel(searchPattern);
			helo.setMovementChars(HeloMovementCharacteristics.getSampleChars());
			helo.getSensorFit().add(radar);
			helo.getSensorFit().add(optic);

			final Surface fisher2 = new Surface(25);
			fisher2.setName("Fisher2");
			fisher2.setCategory(
					new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
			fisher2.setStatus(fisherStat);
			fisher2.setDecisionModel(fishWander);
			fisher2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

			final CoreScenario cs = new CoreScenario();
			cs.setScenarioStepTime(5000);
			cs.addParticipant(helo.getId(), helo);
			cs.addParticipant(fisher2.getId(), fisher2);

			// ok. just do a couple of steps, to check how things pan out.
			cs.step();

			System.out.println("started at:" + cs.getTime());

			final DebriefReplayObserver dro = new DebriefReplayObserver("./test_reports/", "investigate_search.rep",
					false, true, true, null, "plotter", true, null);
			final TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 300, 300, "investigate_search.png",
					null, false, true, false, "tester", true);
			dro.setup(cs);
			tpo.setup(cs);
			//
			dro.outputThisArea(theArea);

			final InvestigateStore theStore = Investigate._invData.firstStore();

			// now run through to completion
			int counter = 0;
			while ((cs.getTime() < 12000000) && (theStore.getCurrentTarget(23) == null)) {
				cs.step();
				counter++;
			}

			// so, we should have found our tartget
			assertNotNull("found target", theStore.getCurrentTarget(23));

			// ok. we've found it. check that we do transition to detected
			counter = 0;
			while ((counter++ < 100) && (theStore.getCurrentTarget(23) != null)) {
				cs.step();
			}

			dro.tearDown(cs);
			tpo.tearDown(cs);

			// so, we should have cleared our tartget
			assertNull("found target", theStore.getCurrentTarget(23));
			assertEquals("remembered contact", 1, theStore.countDoneTargets());

		}

		// TODO FIX-TEST
		public void NtestWithWatch() {
			final TargetType theWatch = new TargetType(Category.Type.MPA);
			final TargetType theTarget = new TargetType(Category.Force.RED);
			final Investigate investigate = new Investigate("investigating red targets near blue", theTarget, theWatch,
					DetectionEvent.IDENTIFIED, null);

			final int TGT1_ID = 567;
			final int TGT2_ID = 568;
			final int BLUE_ID = 34;
			final int NOT_TGT_ID = 123;

			final Status blueStat = new Status(BLUE_ID, 0);
			blueStat.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
			blueStat.setLocation(new WorldLocation(4, 4, 2));
			final ParticipantType blueP = new CoreParticipant(BLUE_ID);
			blueP.setStatus(blueStat);
			final Category blueCat = new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
					Category.Type.MPA);
			blueP.setCategory(blueCat);

			final Status myStat = new Status(12, 0);
			myStat.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
			myStat.setLocation(new WorldLocation(2, 2, 2));

			final Status tgt1Stat = new Status(222, 0);
			tgt1Stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			tgt1Stat.setLocation(new WorldLocation(2, 2.5, 2));
			tgt1Stat.setCourse(22);

			final Status tgt2Stat = new Status(222, 0);
			tgt2Stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			tgt2Stat.setLocation(new WorldLocation(4, 2.5, 2));
			tgt2Stat.setCourse(22);

			final MovementCharacteristics theChars = HeloMovementCharacteristics.getSampleChars();
			final SimpleDemandedStatus theDemStat = new SimpleDemandedStatus(12, 12000);
			DetectionList theDetections = null;

			final LookupSensor duffSensor = new RadarLookupSensor(12, 0, 0, 0, 0, null, 0, null, 0);

			final Surface target1 = new Surface(TGT1_ID, null, null, "fisher");
			target1.setStatus(tgt1Stat);

			final Surface target2 = new Surface(TGT2_ID, null, null, "fisher");
			target2.setStatus(tgt2Stat);

			final ScenarioActivityMonitor theMonitor = new ScenarioActivityMonitor() {
				@Override
				public void createParticipant(final ParticipantType newPart) {
				}

				@Override
				public void detonationAt(final int id, final WorldLocation loc, final double power) {
				}

				@Override
				public Integer[] getListOfParticipants() {
					return new Integer[] { BLUE_ID, TGT1_ID, TGT2_ID };
				}

				@Override
				public ParticipantType getThisParticipant(final int id)

				{
					ParticipantType res = null;
					switch (id) {
					case BLUE_ID:
						res = blueP;
						break;
					case TGT1_ID:
						res = target1;
						break;
					case TGT2_ID:
						res = target2;
						break;
					}
					return res;
				}
			};

			DemandedStatus res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			SimpleDemandedStatus simple = (SimpleDemandedStatus) res;
			assertNull("null dem stat when null detections", res);

			// ok. now use zero length detections
			theDetections = new DetectionList();

			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when empty detections", res);

			// and put in an invalid detection
			final Category tgtCategory = new Category(Category.Force.BLUE, Category.Environment.SURFACE,
					Category.Type.FISHING_VESSEL);
			final double target_brg = 12;
			final DetectionEvent de = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
					null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target1,
					DetectionEvent.DETECTED);
			theDetections.add(de);
			final DetectionEvent de2 = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
					null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target2,
					DetectionEvent.DETECTED);
			theDetections.add(de2);

			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when invalid target", res);
			final InvestigateStore theStore = Investigate._invData.get(theMonitor, 12,
					investigate.isCollaborativeSearch());
			assertNull("inv target still empty", theStore.getCurrentTarget(12));

			// now try a valid target
			tgtCategory.setForce(Category.Force.RED);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);
			assertNotNull("inv target not still empty", theStore.getCurrentTarget(12));
			assertEquals("inv target not still empty", theStore.getCurrentTarget(12).intValue(), TGT2_ID);

			// now make it so there's no valid watch
			blueCat.setType(Category.Type.MINISUB);
			theStore.clearCurrentTarget(12);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("dem stat when valid target", res);
			assertNull("inv target not still empty", theStore.getCurrentTarget(12));

			// and put back our watched item
			blueCat.setType(Category.Type.MPA);
			theStore.clearCurrentTarget(12);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);
			assertNotNull("inv target not still empty", theStore.getCurrentTarget(12));

			// ok. let's lose the target and see what happens
			theDetections.clear();
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when target lost", res);
			assertNotNull("remembered target", theStore.getCurrentTarget(12));
			assertEquals("got correct tgt id", TGT2_ID, theStore.getCurrentTarget(12).intValue());

			// and offer another target
			theDetections.add(de);
			de.setTarget(NOT_TGT_ID);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("new dem stat when valid target", res);

			// back to our target
			de.setTarget(TGT1_ID);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			simple = (SimpleDemandedStatus) res;
			assertNotNull("new dem stat when valid target", res);
			assertTrue("on a good bearing", simple.getCourse() > 0);
			assertEquals("got new tgt id", TGT1_ID, theStore.getCurrentTarget(12).intValue());

			// check that we can tick off target when found
			de.setDetectionState(DetectionEvent.CLASSIFIED);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);
			assertEquals("got new tgt id", TGT1_ID, theStore.getCurrentTarget(12).intValue());

			// check that we can tick off target when found
			de.setDetectionState(DetectionEvent.IDENTIFIED);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("no dem status", res);
			assertNull("ditched current target", theStore.getCurrentTarget(12));
			assertEquals("got something in found targets", 1, theStore.countDoneTargets());

			// check that we can tick off target when found
			de.setDetectionState(DetectionEvent.CLASSIFIED);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("no dem status when only existing target found", res);
			assertNull("ditched current target", theStore.getCurrentTarget(12));
			assertEquals("got something in found targets", 1, theStore.countDoneTargets());

		}

		public void testHashMap() {
			final HashMap<Integer, String> map = new HashMap<Integer, String>();
			map.put(12, "twelve");
			assertTrue(map.containsKey(12));
			assertTrue(map.containsKey(11 + 1));
			final Integer newVal = 33 - 21;
			assertTrue(map.containsKey(newVal));
		}

		public void testRunningMultipleTargets() {
			Investigate._invData.clearStores();

			// initialise the random genny
			ASSET.Util.RandomGenerator.seed(12);

			final WorldLocation topLeft = SupportTesting.createLocation(0, 0);
			topLeft.setDepth(-1000);
			final WorldLocation bottomRight = SupportTesting.createLocation(new WorldDistance(40, WorldDistance.NM),
					new WorldDistance(40, WorldDistance.NM));
			bottomRight.setDepth(1000);
			final WorldArea theArea = new WorldArea(topLeft, bottomRight);

			final PatternSearch_Core ladder = new PatternSearch_Ladder(90.0, new Integer(4), new WorldLocation(0, 0, 0),
					new WorldDistance(10, WorldDistance.NM), new WorldDistance(30, WorldDistance.NM),
					new WorldDistance(500, WorldDistance.FT), null, "Force Protection Ladder");

			final RectangleWander heloWander = new RectangleWander(theArea, "rect wander");
			heloWander.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));
			heloWander.setHeight(new WorldDistance(800, WorldDistance.METRES));
			final RectangleWander fishWander = new RectangleWander(theArea, "rect wander 2");

			final Waterfall searchPattern = new Waterfall();
			searchPattern.setName("Searching");

			final TargetType theTarget = new TargetType(Category.Force.RED);
			final WorldDistance detectionHeight = new WorldDistance(100, WorldDistance.FT);
			final Investigate investigate = new Investigate("investigating red targets", theTarget,
					DetectionEvent.IDENTIFIED, detectionHeight);

			searchPattern.insertAtHead(investigate);
			searchPattern.insertAtFoot(ladder);

			final Status heloStat = new Status(1, 10);
			heloStat.setLocation(SupportTesting.createLocation(2000, 4000));
			heloStat.getLocation().setDepth(-MWC.Algorithms.Conversions.ft2m(900));
			heloStat.setCourse(270);
			heloStat.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));

			// get the helo sensors ready
			// ok. the radar first
			final ASSET.Models.Sensor.Lookup.RadarLookupSensor radar = new RadarLookupSensor(12, 0.04, 5000, 1.2, 0,
					new Duration(0, Duration.SECONDS), 0, new Duration(0, Duration.SECONDS), 9200);

			final ASSET.Models.Sensor.Lookup.OpticLookupSensor optic = new OpticLookupSensor(333, 0.05, 5000, 1.05, 0.8,
					new Duration(20, Duration.SECONDS), 0.2, new Duration(30, Duration.SECONDS));

			final Helo helo = new Helo(93);
			helo.setName("Merlin");
			helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
			helo.setStatus(heloStat);
			helo.setDecisionModel(searchPattern);
			helo.setMovementChars(HeloMovementCharacteristics.getSampleChars());
			helo.getSensorFit().add(radar);
			helo.getSensorFit().add(optic);

			// ////////////////////////////////////////////////
			// scenario
			// ////////////////////////////////////////////////

			final CoreScenario cs = new CoreScenario();
			cs.setScenarioStepTime(5000);
			cs.setSeed(new Integer(251));
			cs.addParticipant(helo.getId(), helo);

			// ////////////////////////////////////////////////
			// fishing vessels
			// ////////////////////////////////////////////////

			// create the fishing vessels
			for (int i = 0; i < 6; i++) {
				final int my_id = 10 + i;
				final Surface fisher2 = new Surface(my_id);
				fisher2.setName("Fisher_" + my_id);
				fisher2.setCategory(
						new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
				final Status fisherStat = new Status(21, 10);
				fisherStat.setLocation(SupportTesting.createLocation(theArea));
				fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
				fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
				fisher2.setStatus(fisherStat);
				fisher2.setDecisionModel(fishWander);
				fisher2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
				// if(i==5)
				cs.addParticipant(fisher2.getId(), fisher2);
			}

			// also add some duff targets
			Surface fisher3 = new Surface(123);
			fisher3.setName("Safe_fisher_1");
			fisher3.setCategory(
					new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
			Status fisherStat = new Status(21, 10);
			fisherStat.setLocation(SupportTesting.createLocation(ASSET.Util.RandomGenerator.nextRandom() * 20000,
					ASSET.Util.RandomGenerator.nextRandom() * 20000));
			fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
			fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
			fisher3.setStatus(fisherStat);
			fisher3.setDecisionModel(fishWander);
			fisher3.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
			// cs.addParticipant(fisher3.getId(), fisher3);

			fisher3 = new Surface(124);
			fisher3.setName("Safe_fisher_2");
			fisher3.setCategory(
					new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
			fisherStat = new Status(21, 10);
			fisherStat.setLocation(SupportTesting.createLocation(ASSET.Util.RandomGenerator.nextRandom() * 20000,
					ASSET.Util.RandomGenerator.nextRandom() * 20000));
			fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
			fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
			fisher3.setStatus(fisherStat);
			fisher3.setDecisionModel(fishWander);
			fisher3.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
			// cs.addParticipant(fisher3.getId(), fisher3);

			// ok. just do a couple of steps, to check how things pan out.
			cs.step();

			System.out.println("started at:" + cs.getTime());

			// and the other observers
			final DebriefReplayObserver dro = new DebriefReplayObserver("test_reports", "investigate_tracks.rep", false,
					false, true, null, "plotter", true, null);
			final DebriefReplayObserver dr2 = new DebriefReplayObserver("test_reports",
					"investigate_helo_decisions.rep", false, true, false, new TargetType(Category.Type.HELO),
					"decisions", true, null);
			final TrackPlotObserver tpo = new TrackPlotObserver("test_reports", 300, 400, "investigate_mult_search.png",
					null, false, true, false, "tester", true);

			dro.setup(cs);
			dr2.setup(cs);
			tpo.setup(cs);

			dro.outputThisArea(theArea);
			dro.outputTheseLocations(ladder.getRoute());

			// ok. we've found it. check that we do transition to detected
			int counter = 0;
			while ((counter++ < 1200)) {
				cs.step();
			}

			dro.tearDown(cs);
			dr2.tearDown(cs);
			tpo.tearDown(cs);

			// hack: suspect we shouldn't be allowing relaxed number of remembered
			// contacts.
			final InvestigateStore theStore = Investigate._invData.firstStore();
			assertEquals("remembered contacts", 3, theStore.countDoneTargets());

			// check we've found one or more of correct targets
			assertTrue("we haven't found hostile target", theStore.hasBeenDone(15));
			assertTrue("we haven't found hostile target", theStore.hasBeenDone(10));
			// assertTrue("we haven't found hostile target",
			// investigate._targetsDone.contains(new Integer(11)));

			// also check that we haven't detected the friendly targets
			assertTrue("we've found friendly target", !theStore.hasBeenDone(123));

			// so, we should have cleared our tartget
			// assertNull("still have link to target",
			// investigate._invData.get(theMonitor).getCurrentTarget(12));

		}

		public void testStatic() {
			final TargetType theTarget = new TargetType(Category.Force.RED);
			final Investigate investigate = new Investigate("investigating red targets", theTarget,
					DetectionEvent.IDENTIFIED, null);

			final int TGT_ID = 567;
			final int NOT_TGT_ID = 123;

			final Status myStat = new Status(12, 0);
			myStat.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
			myStat.setLocation(new WorldLocation(2, 2, 2));

			final Status tgtStat = new Status(222, 0);
			tgtStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			tgtStat.setLocation(new WorldLocation(2, 2, 2));
			tgtStat.setCourse(22);

			final MovementCharacteristics theChars = HeloMovementCharacteristics.getSampleChars();
			final SimpleDemandedStatus theDemStat = new SimpleDemandedStatus(12, 12000);
			DetectionList theDetections = null;

			final LookupSensor duffSensor = new RadarLookupSensor(12, 0, 0, 0, 0, null, 0, null, 0);
			final Surface target = new Surface(TGT_ID, null, null, "fisher");
			target.setStatus(tgtStat);

			final ScenarioActivityMonitor theMonitor = new ScenarioActivityMonitor() {
				@Override
				public void createParticipant(final ParticipantType newPart) {
				}

				@Override
				public void detonationAt(final int id, final WorldLocation loc, final double power) {
				}

				@Override
				public Integer[] getListOfParticipants() {
					return null;
				}

				@Override
				public ParticipantType getThisParticipant(final int id) {
					return target;
				}
			};

			DemandedStatus res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			SimpleDemandedStatus simple = (SimpleDemandedStatus) res;
			assertNull("null dem stat when null detections", res);

			// ok. now use zero length detections
			theDetections = new DetectionList();
			final WorldDistance dist1 = new WorldDistance(1, WorldDistance.MINUTES);

			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when empty detections", res);

			// and put in an invalid detection
			final Category tgtCategory = new Category(Category.Force.BLUE, Category.Environment.SURFACE,
					Category.Type.FISHING_VESSEL);
			final double target_brg = 12;
			final DetectionEvent de = new DetectionEvent(1200, 12, null, duffSensor, dist1, null, new Float(target_brg),
					null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target, DetectionEvent.DETECTED);
			theDetections.add(de);

			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when invalid target", res);
			final InvestigateStore theStore = Investigate._invData.get(theMonitor, 12,
					investigate.isCollaborativeSearch());
			assertNull("inv target still empty", theStore.getCurrentTarget(12));

			// now try a valid target
			tgtCategory.setForce(Category.Force.RED);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);

			assertNotNull("inv target not still empty", theStore.getCurrentTarget(12));

			// ok. let's lose the target and see what happens
			theDetections.clear();
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("null dem stat when target lost", res);
			assertNotNull("remembered target", theStore.getCurrentTarget(12));
			assertEquals("got correct tgt id", TGT_ID, theStore.getCurrentTarget(12).intValue());

			// and offer another target
			theDetections.add(de);
			de.setTarget(NOT_TGT_ID);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("new dem stat when valid target", res);
			assertEquals("got new tgt id", NOT_TGT_ID, theStore.getCurrentTarget(12).intValue());

			// back to our target
			de.setTarget(TGT_ID);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			simple = (SimpleDemandedStatus) res;
			assertNotNull("new dem stat when valid target", res);
			assertTrue("on a good bearing", simple.getCourse() > 0);
			assertEquals("got new tgt id", TGT_ID, theStore.getCurrentTarget(12).intValue());

			// check that we can tick off target when found
			de.setDetectionState(DetectionEvent.CLASSIFIED);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNotNull("dem stat when valid target", res);
			assertEquals("got new tgt id", TGT_ID, theStore.getCurrentTarget(12).intValue());

			// check that we can tick off target when found
			de.setDetectionState(DetectionEvent.IDENTIFIED);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("no dem status", res);
			assertNull("ditched current target", theStore.getCurrentTarget(12));
			assertEquals("got something in found targets", 1, theStore.countDoneTargets());

			// check that we can tick off target when found
			de.setDetectionState(DetectionEvent.CLASSIFIED);
			res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
			assertNull("no dem status when only existing target found", res);
			assertNull("ditched current target", theStore.getCurrentTarget(12));
			assertEquals("still got something in found targets", 1, theStore.countDoneTargets());

		}

		// public void testReadFromCommandLine()
		// {
		// String test_path = "../src/java/ASSET_SRC/ASSET/Util/MonteCarlo/";
		// String scen_file = test_path + "test_variance_scenario_area.xml";
		// String var_file = test_path + "test_variance_area.xml";
		//
		// CommandLine.main(new String[]{" " + scen_file, var_file});
		// }
		//

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean _speedChangeAllowed = true;

	/**
	 * keep track of how everybody's getting on
	 *
	 */
	protected static InvestigateStore.MappedStores _invData = new InvestigateStore.MappedStores();

	/**
	 * the type of target to investigate
	 */
	private TargetType _myTarget = new TargetType();

	/**
	 * type of target we're protecting
	 *
	 */
	private TargetType _watchType = null;

	/**
	 * the desired level of detection required to satisfy investigation
	 *
	 * @see ASSET.Models.Detection.DetectionEvent.CLASSIFIED et.al
	 */
	private int _detectionLevel;

	/**
	 * a local copy of our editable object
	 */
	private InvestigateInfo _myEditor = null;

	/****************************************************
	 * constructor
	 ***************************************************/

	/**
	 * whether to conduct a collaborative investigation
	 *
	 */
	protected boolean _collaborativeSearch;

	/**
	 * the height at which we conduct investigation
	 */
	private WorldDistance _investigateHeight;

	/**
	 * convenience value for who we're currently looking for
	 *
	 */
	private Integer _currentTarget;

	/**
	 * how we decide if investigation is complete
	 *
	 */
	private InvestigatePattern _myPattern;

	/**
	 * constructor.
	 *
	 * @param myName         the name of this behaviour
	 * @param myTarget       the target type to investigate
	 * @param detectionLevel the level of detection to achieve
	 */
	public Investigate(final String myName, final TargetType myTarget, final int detectionLevel,
			final WorldDistance investigateHeight) {
		this(myName, myTarget, null, detectionLevel, investigateHeight);
	}

	/**
	 * constructor.
	 *
	 * @param myName         the name of this behaviour
	 * @param myTarget       the target type to investigate
	 * @param watchType      the type of target we're defending
	 * @param detectionLevel the level of detection to achieve
	 */
	public Investigate(final String myName, final TargetType myTarget, final TargetType watchType,
			final int detectionLevel, final WorldDistance investigateHeight) {
		super(myName);
		_myTarget = myTarget;
		_detectionLevel = detectionLevel;
		_investigateHeight = investigateHeight;
		_watchType = watchType;
	}

	/**
	 * decide
	 *
	 * @param status parameter for decide
	 * @param time   parameter for decide
	 * @return the returned ASSET.Participants.DemandedStatus
	 */
	@Override
	public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
			final ASSET.Models.Movement.MovementCharacteristics chars,
			final ASSET.Participants.DemandedStatus demStatus, final ASSET.Models.Detection.DetectionList detections,
			final ScenarioActivityMonitor monitor, final long time) {

		SimpleDemandedStatus res = null;

		DetectionEvent validDetection = null;

		String activity = "";

		final Integer myId = status.getId();
		final InvestigateStore store = _invData.get(monitor, myId, isCollaborativeSearch());

		// do we have any detections?
		if (detections != null) {
			// get bearing to first detection
			final int len = detections.size();
			if (len > 0) {
				// first see if we are still in contact with our existing target
				// do we have an existing target?
				final Integer myTarget = store.getCurrentTarget(myId);
				if (myTarget != null) {

					// see if we can find it
					final DetectionList dl = detections.getDetectionsOf(myTarget);
					if (dl != null) {
						// of the detections of our current target. what's our best quality
						// detection?
						validDetection = dl.getBestDetection();

						if (getPattern().invComplete(validDetection, _detectionLevel))

						{
							// done. ready to move onto another
							// note that we check for greater or equal than detection state =
							// just in case
							// we've jumped a detection state
							store.addDoneTarget(myId, myTarget);

							// ok, output any results needed
							handleInvestigationComplete(validDetection, time, monitor);

							activity += "Current investigation complete. ";

							// and forget the current target
							store.clearCurrentTarget(myId);

							// and clear our local copy
							_currentTarget = null;

							// just reinforce the fact that we need to look for another target
							validDetection = null;

							// also output a message to any listeners - saying what we've done
							if (_myEditor != null) {
								if (_myEditor.hasReportListeners()) {
									final String msg = store.countDoneTargets() + " Targets identified";
									_myEditor.fireReport(this, msg);
								}
							}
						}

					} // if we have are still in contact with our target
					else {
						// oh well, current target lost. output message as necessary
						handleTargetLost(time, monitor, store.getCurrentTarget(myId));

						activity += "Current target lost. ";
					}
				} // whether we have a current target

				// do we still need to look for another target?
				if (validDetection == null) {
					// hmm, where are we?
					final WorldLocation myLoc = status.getLocation();

					// yes, go and find one amongst the remaining targets
					validDetection = findNewTarget(len, detections, time, monitor, store, myLoc);

					if (validDetection != null) {
						activity += "New target found.";
					}

				} // whether we've found the existing target
			} // if we have any detections
		} // if the detections object was received

		// ok. did we find a new target?
		if (validDetection != null) {
			// NO, GENERATE INTERCEPT BEARING
			// yes, calc the course to it
			final NetworkParticipant pt = monitor.getThisParticipant(validDetection.getTarget());

			// just check the participant is still live!!
			if (pt != null) {
				final Status tgtStat = pt.getStatus();
				res = Intercept.calculateInterceptCourseFor(status, tgtStat, time);

				// did it work?
				if (res == null) {
					// no, do we let ourselves change speed?
					if (_speedChangeAllowed) {
						final Status newStat = new Status(status);
						newStat.setSpeed(chars.getMaxSpeed());

						// and try again
						res = Intercept.calculateInterceptCourseFor(newStat, tgtStat, time);

						// did this work?
						if (res != null) {
							// yes, just try to increase speed
							res.setSpeed(chars.getMaxSpeed());
						}
					}
				}

				// just check we're capable of catching him
				if (res != null) {

					// ok, do we have a demanded height?
					if (_investigateHeight != null)
						res.setHeight(_investigateHeight);

					activity += "Turning towards target:" + pt.getName() + ". Dem brg:"
							+ MWC.Utilities.TextFormatting.GeneralFormat.formatBearing(res.getCourse());

					// see if there's any other demStatus setting to do
					res = getPattern().amendDemStatus(res, status, tgtStat);

					// output results to any listeners
					handleNewDemCourseChange((float) res.getCourse(), time, monitor, validDetection.getTarget());

					// and remember that we're heading for it
					store.setCurrentTarget(myId, validDetection.getTarget());

					// take copy, just for convenience
					_currentTarget = validDetection.getTarget();

				}
			}
		}

		if (activity == "")
			activity = "Inactive";

		super.setLastActivity(activity);

		// ok done. If we didn't find a valid target we aren't active.
		return res;
	}

	/**
	 * determine how far it is to the target
	 *
	 * @param de      the detection we're looking at
	 * @param monitor the accessor for the list of participants
	 * @param myLoc   my location
	 * @return
	 */
	private Double effectivenessOfThisTarget(final DetectionEvent de, final ScenarioActivityMonitor monitor,
			final WorldLocation myLoc) {
		Double res = null;
		if (_watchType != null) {
			// right, we're not assessing the range from ourselves, but from the
			// watched platform.

			// find a matching participant
			NetworkParticipant watched = null;

			final Integer[] parts = monitor.getListOfParticipants();
			for (int i = 0; i < parts.length; i++) {
				final int thisId = parts[i];

				final NetworkParticipant thisPart = monitor.getThisParticipant(thisId);
				if (_watchType.matches(thisPart.getCategory())) {
					watched = thisPart;
					break;
				}
			}

			if (watched != null) {
				// find tgt log
				final NetworkParticipant tgt = monitor.getThisParticipant(de.getTarget());
				final WorldLocation tgtLoc = tgt.getStatus().getLocation();

				// and the location of who I'm defending
				final WorldLocation watchLoc = watched.getStatus().getLocation();

				final double toWatch = tgtLoc.rangeFrom(watchLoc);
				final double toMe = tgtLoc.rangeFrom(myLoc);
				double outsideTargetPath = 1;
				double headingAwayFromTarget = 1;

				// sort out how to produce cost function that includes the respective
				// paths
				double watchPath = watched.getStatus().getCourse();
				double tgtToWatch = Math.toDegrees(tgtLoc.bearingFrom(watchLoc));

				// put them into some comfortable domain
				while (watchPath > 180)
					watchPath -= 360;
				while (tgtToWatch > 180)
					tgtToWatch -= 360;

				if (Math.abs(tgtToWatch - watchPath) < 45) {
					outsideTargetPath = 0.6;
				}
				;

				watchPath = tgt.getStatus().getCourse();
				tgtToWatch = Math.toDegrees(watchLoc.bearingFrom(tgtLoc));

				// put them into some comfortable domain
				while (watchPath > 180)
					watchPath -= 360;
				while (tgtToWatch > 180)
					tgtToWatch -= 360;

				if (Math.abs(tgtToWatch - watchPath) < 45) {
					headingAwayFromTarget = 0.6;
				}
				;

				// and calculate the range using a cost function
				res = (toMe * 0.05 + toWatch) * outsideTargetPath * headingAwayFromTarget;
			}
		} else {
			res = de.getRange().getValueIn(WorldDistance.DEGS);
		}

		return res;
	}

	/**
	 * scan through the available detections, to see if there's a new, valid one
	 *
	 * @param len        how many detections there are
	 * @param detections our current set of detetions
	 * @param time       the current time
	 * @param monitor    our scenario activity monitor
	 * @param myLoc      ownship location
	 * @return a contact representing our new target
	 */
	protected DetectionEvent findNewTarget(final int len, final DetectionList detections, final long time,
			final ScenarioActivityMonitor monitor, final InvestigateStore store, final WorldLocation myLoc) {
		DetectionEvent res = null;
		Double tmpScore = null;

		THROUGH_DETECTIONS: for (int i = 0; i < len; i++) {
			final DetectionEvent de = detections.getDetection(i);
			// do we know the range
			final Float brg = de.getBearing();
			if (brg != null) {
				// is this of our target type
				final Category thisTarget = de.getTargetType();
				if (_myTarget.matches(thisTarget)) {

					// have we found it already?
					final Integer tgtId = de.getTarget();

					final boolean beenDone = store.hasBeenDone(tgtId);
					final boolean isCurrent = store.isACurrentTarget(tgtId);

					if (beenDone || isCurrent) {
						// already found it. continue
					} else {
						// aah, just check that it hasn't already been removed from the
						// scenario
						if (monitor.getThisParticipant(de.getTarget()) != null) {
							// right, work out the range to the target
							final Double thisScore = effectivenessOfThisTarget(de, monitor, myLoc);

							// did we find a range?
							if (thisScore != null) {
								// is this our first match
								if (res != null) {
									if ((tmpScore == null) || (tmpScore > thisScore)) {
										// new target closer. switch to it
										res = de;
										tmpScore = thisScore;
									}
								} else {
									res = de;
									tmpScore = thisScore;
								}
							}
						}
					}
				} else {
					// drop out to the next detections
					continue THROUGH_DETECTIONS;
				}

			} // if we know the bearing
		} // looping through the detections

		// have we got a new target?
		if (res != null) {
			// ok, output any message as necessary
			handleNewTarget(res, time, monitor);
		}

		return res;
	}

	/**
	 * convenience method to get who we're looking for
	 *
	 * @return
	 */
	public Integer getCurrentTarget() {
		return _currentTarget;
	}

	/**
	 * return the required level of detection
	 *
	 * @return
	 * @see DetectionEvent.DETECTED
	 */
	public int getDetectionLevel() {
		return _detectionLevel;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new InvestigateInfo(this);

		return _myEditor;
	}

	/**
	 * get the (possibly null) height at which to conduct investigation
	 *
	 * @return height
	 */
	public WorldDistance getInvestigateHeight() {
		return _investigateHeight;
	}

	protected InvestigatePattern getPattern() {
		if (_myPattern == null)
			_myPattern = new InvestigatePatternFactory.MatchDetectionLevel();

		return _myPattern;
	}

	/**
	 * getTargetToEvade
	 *
	 * @return the returned TargetType
	 */
	public TargetType getTargetType() {
		return _myTarget;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: Investigate.java,v $
	 * Revision 1.1  2006/08/08 14:21:34  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:42  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.34  2004/11/03 09:54:49  Ian.Mayo
	 * Allow search speed to be set
	 *
	 * Revision 1.33  2004/11/01 15:54:55  Ian.Mayo
	 * Reflect new signature of Track Plot Observer
	 * <p/>
	 * Revision 1.32  2004/10/16 14:10:40  ian
	 * Implement progress reporting to editable subject
	 * <p/>
	 * Revision 1.31  2004/10/16 13:49:26  ian
	 * Implement firing report when we find a new target (viewable from editor pane)
	 * <p/>
	 * Revision 1.30  2004/10/15 11:11:30  Ian.Mayo
	 * Configure proper property editor for target category
	 * <p/>
	 * Revision 1.29  2004/09/27 14:53:56  Ian.Mayo
	 * Reflect fact that we now use intercept course rather than just head down bearing to tgt
	 * <p/>
	 * Revision 1.28  2004/09/24 11:08:10  Ian.Mayo
	 * Tidy test names
	 * <p/>
	 * Revision 1.27  2004/09/03 15:10:30  Ian.Mayo
	 * Output correct status messaging
	 * <p/>
	 * Revision 1.26  2004/09/02 15:59:02  Ian.Mayo
	 * Fix bug introduced in refactoring, then fix test
	 * <p/>
	 * Revision 1.25  2004/09/02 13:19:04  Ian.Mayo
	 * Tidy up, use DetectionList convenience methods
	 * <p/>
	 * Revision 1.24  2004/08/31 09:36:20  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.23  2004/08/26 16:27:02  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.22  2004/08/25 11:20:35  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.21  2004/08/20 13:32:27  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.20  2004/08/18 07:27:47  Ian.Mayo
	 * Don't run through huge scenario generation sample
	 * <p/>
	 * Revision 1.19  2004/08/17 14:22:04  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.18  2004/08/12 11:09:22  Ian.Mayo
	 * Respect observer classes refactored into tidy directories
	 * <p/>
	 * Revision 1.17  2004/08/10 14:00:32  Ian.Mayo
	 * Ditch the narrative observer, it was absorbed into Debrief Replay observer
	 * <p/>
	 * Revision 1.16  2004/08/10 13:51:44  Ian.Mayo
	 * Provide better activity message
	 * <p/>
	 * Revision 1.15  2004/08/10 08:50:06  Ian.Mayo
	 * Change functionality of Debrief replay observer so that it can record decisions & detections aswell.  Also include ability to track particular type of target
	 * <p/>
	 * Revision 1.14  2004/08/09 15:27:35  Ian.Mayo
	 * More testing
	 * <p/>
	 * Revision 1.13  2004/08/09 13:13:45  Ian.Mayo
	 * Getting closer with testing
	 * <p/>
	 * Revision 1.12  2004/08/06 12:52:01  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.11  2004/08/06 11:14:23  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.10  2004/08/06 10:27:45  Ian.Mayo
	 * Manage investigate height
	 * <p/>
	 * Revision 1.9  2004/08/05 15:20:14  Ian.Mayo
	 * Update Activity descriptor, change how we drop out of loop on finding matching contact, tidying.
	 * <p/>
	 * Revision 1.8  2004/08/04 14:26:33  Ian.Mayo
	 * Switch to ladder search, more tidying
	 * <p/>
	 * Revision 1.7  2004/08/04 09:52:10  Ian.Mayo
	 * Improve tests, test for height transition
	 * <p/>
	 * Revision 1.6  2004/08/03 15:18:59  Ian.Mayo
	 * Refactor to tidy message output
	 * <p/>
	 * Revision 1.5  2004/08/03 10:27:54  Ian.Mayo
	 * (Further) relax tests
	 * <p/>
	 * Revision 1.4  2004/08/02 15:31:19  Ian.Mayo
	 * Relax tests
	 * <p/>
	 * Revision 1.3  2004/08/02 15:00:53  Ian.Mayo
	 * Try to get comments working
	 * <p/>
	 * Revision 1.2  2004/08/02 10:05:04  Ian.Mayo
	 * Minor mod.
	 * <p/>
	 * Revision 1.1  2004/05/24 15:56:14  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.9  2004/05/05 20:39:58  ian
	 * Record demanded course
	 * <p/>
	 * Revision 1.8  2004/05/05 20:26:56  ian
	 * Switch to new narrative recorder
	 * <p/>
	 * Revision 1.7  2004/05/05 19:14:18  ian
	 * Use more typical flight height
	 * <p/>
	 * Revision 1.6  2004/04/28 21:39:04  ian
	 * Lots more changes during development
	 * <p/>
	 * Revision 1.5  2004/04/22 21:36:50  ian
	 * Tidying
	 * <p/>
	 * Revision 1.4  2004/04/19 19:00:12  ian
	 * add a note
	 * <p/>
	 * Revision 1.3  2004/04/15 21:59:41  ian
	 * Lost more
	 * <p/>
	 * Revision 1.2  2004/04/13 21:41:41  ian
	 * Getting closer
	 * <p/>
	 * Revision 1.1  2004/04/13 21:10:53  ian
	 * First version
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	public TargetType getWatchType() {
		return _watchType;
	}

	/**
	 * provide any necessary reporting of investigation complete
	 *
	 * @param de      the target we were investigating
	 * @param time    the time at which investigation was complete
	 * @param monitor the scenario activity monitor
	 */
	protected void handleInvestigationComplete(final DetectionEvent de, final long time,
			final ScenarioActivityMonitor monitor) {
	}

	/**
	 * provide any requested reporting of new demanded course
	 *
	 * @param brgToTarget the new bearing to head down
	 * @param time        the current time
	 * @param monitor     the scenario activity monitor
	 * @param targetId
	 */
	protected void handleNewDemCourseChange(final float brgToTarget, final long time,
			final ScenarioActivityMonitor monitor, final int targetId) {
	}

	/**
	 * provide any necessary reporting for we have identified a new target
	 *
	 * @param de      the new target we're going to head for
	 * @param time    the current time
	 * @param monitor the scenario activity monitor
	 */
	protected void handleNewTarget(final DetectionEvent de, final long time, final ScenarioActivityMonitor monitor) {
		// ok, clear the detection helper
		getPattern().newTarget(de.getTarget());
	}

	/**
	 * provide any necessary reporting for target not being found amongst current
	 * contacts
	 *
	 * @param time    the current time
	 * @param monitor the scenario activity monitor
	 */
	protected void handleTargetLost(final long time, final ScenarioActivityMonitor monitor, final int currentTarget) {
	}

	// ////////////////////////////////////////////////////////////////////
	// editable data
	// ////////////////////////////////////////////////////////////////////
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

	// //////////////////////////////////////////////////////////
	// model support
	// //////////////////////////////////////////////////////////

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 *
	 * @param currentStatus
	 */
	@Override
	public void interrupted(final Status currentStatus) {
		// ignore.
	}

	/**
	 * whether to collaboratively search (agree who's spotting which targets)
	 *
	 * @return yes/no
	 */
	public boolean isCollaborativeSearch() {
		return _collaborativeSearch;
	}

	/**
	 * reset this decision model
	 */
	@Override
	public void restart() {
		// clear teh store
		_invData.clearStores();
	}

	/**
	 * whether to collaboratively search (agree who's spotting which targets)
	 *
	 * @return yes/no
	 */
	public void setCollaborativeSearch(final boolean collaborativeSearch) {
		_collaborativeSearch = collaborativeSearch;
	}

	/**
	 * set the required level of detection
	 *
	 * @param detectionLevel
	 */
	public void setDetectionLevel(final int detectionLevel) {
		this._detectionLevel = detectionLevel;
	}

	/**
	 * set the (optional) height at which to conduct investigation
	 *
	 * @param investigateHeight
	 */
	public void setInvestigateHeight(final WorldDistance investigateHeight) {
		this._investigateHeight = investigateHeight;
	}

	/**
	 * setTargetToEvade
	 *
	 * @param target parameter for setTargetToEvade
	 */
	public void setTargetType(final TargetType target) {
		_myTarget = target;
	}

}