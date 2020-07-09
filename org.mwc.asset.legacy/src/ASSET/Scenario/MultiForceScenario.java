
package ASSET.Scenario;

import java.util.HashMap;
import java.util.Iterator;
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

import ASSET.ParticipantType;
import ASSET.ScenarioType;

public class MultiForceScenario extends CoreScenario {

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public static class MFScenarioTest extends junit.framework.TestCase {
		protected class createdListener implements ParticipantsChangedListener {
			/**
			 * the indicated participant has been added to the scenario
			 */
			@Override
			public void newParticipant(final int index) {
				createdCounter++;
			}

			/**
			 * the indicated participant has been removed from the scenario
			 */
			@Override
			public void participantRemoved(final int index) {
				destroyedCounter++;
			}

			/**
			 * the scenario has restarted
			 */
			@Override
			public void restart(final ScenarioType scenario) {
			}

		}

		protected class startStopListener implements ScenarioRunningListener {
			@Override
			public void finished(final long elapsedTime, final String reason) {
				lastStartState = new Boolean(false);
			}

			@Override
			public void newScenarioStepTime(final int val) {
				newStepTime = val;
			}

			@Override
			public void newStepTime(final int val) {
				newStepTime = val;
			}

			/**
			 * the scenario has stopped running on auto
			 */
			@Override
			public void paused() {
				// let's not worry about this little thing
			}

			/**
			 * the scenario has restarted
			 */
			@Override
			public void restart(final ScenarioType scenario) {
			}

			@Override
			public void started() {
				lastStartState = new Boolean(true);
			}

		}

		protected class stepListener implements ScenarioSteppedListener {
			/**
			 * the scenario has restarted
			 */
			@Override
			public void restart(final ScenarioType scenario) {
			}

			@Override
			public void step(final ScenarioType scenario, final long newTime) {
				lastTime = newTime;
				stepCounter++;
			}

		}

		static public final String TEST_ALL_TEST_TYPE = "UNIT";
		protected int stepCounter = 0;
		protected long lastTime = 0;
		protected Boolean lastStartState = null;

		protected int createdCounter = 0;

		protected int destroyedCounter = 0;

		int newStepTime;

		public MFScenarioTest(final String val) {
			super(val);
		}

		public void testScenarioParticipants() {

			//
			// // create server
			// final MultiForceScenario srv = new MultiForceScenario();
			//
			// // add as listener
			// final createdListener cl = new createdListener();
			// srv.addParticipantsChangedListener(cl);
			//
			// SSMovementCharacteristics ssm = new SSMovementCharacteristics("blank",
			// 1, 1, 0.0, 20, 1, 300, 1, 1, 100, 1);
			// SurfaceMovementCharacteristics sur = new
			// SurfaceMovementCharacteristics("surf",
			// 1, 1, 0.0, 22, 1, 399);
			//
			// // create the red participants
			// final ASSET.Participants.Status stat = new ASSET.Participants.Status(2, 12);
			// final MWC.GenericData.WorldLocation origin = new
			// MWC.GenericData.WorldLocation(1, 1, 1);
			// stat.setLocation(origin);
			// stat.setCourse(0);
			// stat.setSpeed(new WorldSpeed(3, WorldSpeed.M_sec));
			// final ASSET.ParticipantType ssn = new ASSET.Models.Vessels.SSN(2, stat, null,
			// "SSN");
			// ssn.setMovementChars(ssm);
			// final ASSET.ParticipantType frig = new ASSET.Models.Vessels.Surface(3, stat,
			// null,
			// "Frigate");
			// frig.setMovementChars(sur);
			//
			// // create the blue participants
			// final ASSET.ParticipantType ssk1 = new ASSET.Models.Vessels.SSK(11, stat,
			// null, "SSK1");
			// ssk1.setMovementChars(ssm);
			// final ASSET.ParticipantType ssk2 = new ASSET.Models.Vessels.SSK(12, stat,
			// null, "SSK2");
			// ssk2.setMovementChars(ssm);
			// final ASSET.ParticipantType ssk3 = new ASSET.Models.Vessels.SSK(13, stat,
			// null, "SSK3");
			// ssk3.setMovementChars(ssm);
			//
			// // add the blues
			// srv.addBlueParticipant(ssn.getId(), ssn);
			// srv.addBlueParticipant(frig.getId(), frig);
			//
			// // give the ssn a sensor
			// final ASSET.Models.Sensor.CoreSensor sensor = new
			// ASSET.Models.Sensor.Initial.BroadbandSensor(22);
			// ssn.addSensor(sensor);
			//
			// // give the ssn a sensor
			// ssk1.addSensor(sensor);
			//
			// // add the blues
			// srv.addParticipant(ssk1.getId(), ssk1);
			// srv.addParticipant(ssk2.getId(), ssk2);
			// srv.addParticipant(ssk3.getId(), ssk3);
			//
			// // check the sizes
			// final Integer[] blues = srv.getListOfBlueParticipants();
			// assertEquals("blue participant size", 2, blues.length);
			//
			// final Integer[] reds = srv.getListOfRedParticipants();
			// assertEquals("red participant size", 3, reds.length);
			//
			// // try to remove a blue
			// srv.removeParticipant(ssk1.getId());
			// assertEquals("red participant size", 2,
			// srv.getListOfRedParticipants().length);
			//
			// final int lenBefore = srv.getListOfBlueParticipants().length;
			// srv.removeParticipant(ssn.getId());
			// assertEquals("blue participant size", lenBefore - 1,
			// srv.getListOfBlueParticipants().length);
			//
			//
			// // check stepping through
			// srv.step();

			// todo: reinstate tests
		}

	}

	/**
	 * **************************************************************** wrapper to
	 * make MultiForce act like normal Scenario
	 * ****************************************************************
	 */
	private class ScenarioBlueWrapper extends MultiForceScenario {
		private final MultiForceScenario _scenario;

		public ScenarioBlueWrapper(final MultiForceScenario scenario) {
			_scenario = scenario;
		}

		/**
		 * Provide a list of id numbers of Participant we contain
		 *
		 * @return list of ids of Participant we contain
		 */
		@Override
		public Integer[] getListOfParticipants() {
			return _scenario.getListOfParticipants();
		}

		/**
		 * Return a particular Participant - so that the Participant can be controlled
		 * directly. Listeners added/removed. Participants added/removed, etc.
		 */
		@Override
		public ParticipantType getThisParticipant(final int id) {
			return _scenario.getThisParticipant(id);
		}
	}

	/**
	 * **************************************************************** wrapper to
	 * make MultiForce act like normal Scenario
	 * ****************************************************************
	 */
	private class ScenarioRedWrapper extends MultiForceScenario {
		private final MultiForceScenario _scenario;

		public ScenarioRedWrapper(final MultiForceScenario scenario) {
			_scenario = scenario;
		}

		/**
		 * Provide a list of id numbers of Participant we contain
		 *
		 * @return list of ids of Participant we contain
		 */
		@Override
		public Integer[] getListOfParticipants() {
			return _scenario.getListOfBlueParticipants();
		}

		/**
		 * Return a particular Participant - so that the Participant can be controlled
		 * directly. Listeners added/removed. Participants added/removed, etc.
		 */
		@Override
		public ParticipantType getThisParticipant(final int id) {
			return _scenario.getThisParticipant(id);
		}
	}

	/**
	 * the name of this scenario type
	 */
	public static final String TYPE = "SuperSearch";

	/**
	 * keep track of the blue force, the CoreScenario stores the red force
	 */
	private HashMap<Integer, ParticipantType> _blueForce;

	/**
	 * keep track of the "full" participants, from both sides (which we use for the
	 * Blue stepping forward)
	 */
	private final HashMap<Integer, ParticipantType> _fullForce;

	/**
	 * the list of blue participants changed listeners
	 */
	private Vector<ParticipantsChangedListener> _blueParticipantListeners;

	/**
	 * our blue wrapper, to make this look like a normal scenario for red
	 * participants
	 */
	private ScenarioBlueWrapper _blueWrapper = null;

	/**
	 * our red wrapper, to make this look like a normal scenario for red
	 * participants
	 */
	private ScenarioRedWrapper _redWrapper = null;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */

	public MultiForceScenario() {
		_fullForce = new HashMap<Integer, ParticipantType>();
	}

	/***********************************************************************
	 *
	 ***********************************************************************/

	/**
	 * method to add a participant to the "blue" force
	 */
	public void addBlueParticipant(int index, final ASSET.ParticipantType participant) {

		if (index == 0)
			index = ASSET.Util.IdNumber.generateInt();

		// do we have list?
		if (_blueForce == null) {
			_blueForce = new java.util.HashMap<Integer, ParticipantType>();
		}

		// store it
		_blueForce.put(new Integer(index), participant);

		// also add it to the "full force" listing
		_fullForce.put(new Integer(index), participant);

		// fire new scenario event
		this.fireBlueParticipantChanged(index, true);
	}

	/////////////////////////////////////////////////////
	// manage our blue listeners
	/////////////////////////////////////////////////////
	public void addBlueParticipantsChangedListener(final ParticipantsChangedListener list) {
		if (_blueParticipantListeners == null)
			_blueParticipantListeners = new Vector<ParticipantsChangedListener>(1, 2);

		_blueParticipantListeners.add(list);
	}

	/**
	 * back door, for reloading a scenario from file)
	 */
	@Override
	public void addParticipant(int index, final ParticipantType participant) {
		if (index == 0)
			index = ASSET.Util.IdNumber.generateInt();

		// and add it to our "full" listing
		_fullForce.put(new Integer(index), participant);

		// add the particicpant to the parents (red) listing
		super.addParticipant(index, participant);
	}

	/**
	 * create the red force using the characteristics provided
	 */
	public void createRedForce(final String file, final String variance) {
		final ASSET.Scenario.SuperSearch.SuperSearch generator = new ASSET.Scenario.SuperSearch.SuperSearch();
		generator.setTemplate(file);
		generator.setVariance(variance);
		generator.setIdStart(1000);

		final ASSET.ParticipantType[] lst = generator.build();

		// store the time step
		this.setScenarioStepTime(generator.getTimeStep());

		// now add these participants to the red force
		for (int i = 0; i < lst.length; i++) {
			final ASSET.ParticipantType thisP = lst[i];
			thisP.setName(thisP.getName() + ":" + i);

			// initialise the time in the new participant
			thisP.getStatus().setTime(this.getTime());
			addParticipant(thisP.getId(), thisP);

		}
	}

	private void fireBlueParticipantChanged(final int index, final boolean added) {
		if (_blueParticipantListeners != null) {
			final Iterator<ParticipantsChangedListener> it = _blueParticipantListeners.iterator();
			while (it.hasNext()) {
				final ParticipantsChangedListener pcl = it.next();
				if (added)
					pcl.newParticipant(index);
				else
					pcl.participantRemoved(index);
			}
		}
	}

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	protected Integer[] getListOfBlueParticipants() {
		Integer[] res = new Integer[0];

		if (_blueForce != null) {
			final java.util.Collection<Integer> vals = _blueForce.keySet();
			res = vals.toArray(res);
		}

		return res;
	}

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	@Override
	public Integer[] getListOfParticipants() {
		Integer[] res = new Integer[_fullForce.size()];

		if (_blueForce != null) {
			final java.util.Collection<Integer> vals = _fullForce.keySet();
			res = vals.toArray(res);
		}

		return res;
	}

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	protected Integer[] getListOfRedParticipants() {
		return super.getListOfParticipants();
	}

	////////////////////////////////////////////////////////
	// participant-related
	////////////////////////////////////////////////////////
	/**
	 * Return a particular Participant - so that the Participant can be controlled
	 * directly. Listeners added/removed. Participants added/removed, etc.
	 */
	@Override
	public ParticipantType getThisParticipant(final int id) {
		ParticipantType res = null;
		if (_fullForce != null)
			res = _fullForce.get(new Integer(id));

		return res;
	}

	/**
	 * Return a particular Participant - so that the Participant can be controlled
	 * directly. Listeners added/removed. Participants added/removed, etc.
	 */
	public ParticipantType getThisRedParticipant(final int id) {
		return super.getThisParticipant(id);
	}

	public void removeBlueParticipantsChangedListener(final ParticipantsChangedListener list) {
		_blueParticipantListeners.remove(list);
	}

	/**
	 * remove the indicated participant
	 */
	@Override
	public void removeParticipant(final int index) {
		// try to do it from the parent
		super.removeParticipant(index);

		// and try to do it from our full force

		// now remove it (try the full force first)
		final Integer bigI = new Integer(index);
		_fullForce.remove(bigI);

		// now remove it (try the blue force)
		final Object res2 = _blueForce.remove(bigI);
		if (res2 != null) {
			// fire new scenario event
			this.fireBlueParticipantChanged(index, false);
		}
	}

	/**
	 * Move the scenario through a single step
	 */
	@Override
	public void step() {

		if (_blueWrapper == null) {
			_blueWrapper = new ScenarioBlueWrapper(this);
			_redWrapper = new ScenarioRedWrapper(this);
		}

		final long oldTime = _myTime;

		// move time forward
		_myTime += _myScenarioStepTime;

		// move the blue participants forward
		if (_blueForce != null) {
			//////////////////////////////////////////////////
			// first the decision
			//////////////////////////////////////////////////
			java.util.Iterator<ParticipantType> iter = _blueForce.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				// pass it the parent's listof participants
				pt.doDecision(oldTime, _myTime, _blueWrapper);
			}

			//////////////////////////////////////////////////
			// now the movement
			//////////////////////////////////////////////////
			iter = _blueForce.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				// pass it the parent's listof participants
				pt.doMovement(oldTime, _myTime, _blueWrapper);
			}

			//////////////////////////////////////////////////
			// now the detection
			//////////////////////////////////////////////////
			iter = _blueForce.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				// pass it the parent's listof participants
				pt.doDetection(oldTime, _myTime, _blueWrapper);
			}
		}

		// now move the red participants forward
		if (_myVisibleParticipants != null) {
			//////////////////////////////////////////////////
			// first the decision
			//////////////////////////////////////////////////
			java.util.Iterator<ParticipantType> iter = _myVisibleParticipants.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				pt.doDecision(oldTime, _myTime, _redWrapper);
			}

			//////////////////////////////////////////////////
			// now the movement
			//////////////////////////////////////////////////
			iter = _myVisibleParticipants.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				pt.doMovement(oldTime, _myTime, _redWrapper);
			}

			//////////////////////////////////////////////////
			// now the detection
			//////////////////////////////////////////////////
			iter = _myVisibleParticipants.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				pt.doDetection(oldTime, _myTime, _redWrapper);
			}
		}

		// fire messages
		this.fireScenarioStepped(_myTime);
	}

}