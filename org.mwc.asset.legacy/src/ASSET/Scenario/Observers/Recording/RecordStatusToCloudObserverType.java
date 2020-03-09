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

package ASSET.Scenario.Observers.Recording;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.UserControl;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Sensor.Initial.OpticSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.CoreObserver;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.Track;
import MWC.Utilities.ReaderWriter.json.GNDDocHandler;
import MWC.Utilities.ReaderWriter.json.GNDStore;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 28-Oct-2003 Time: 14:17:34 To
 * change this template use Options | File Templates.
 */
public class RecordStatusToCloudObserverType extends CoreObserver implements ASSET.Scenario.ScenarioSteppedListener {
	// private static final String POSITION_FORMAT = "data.pos";
	//
	// private static final String NARRATIVE_FORMAT = "data.narr";
	//
	// private static final String DETECTION_FORMAT = "data.detect";
	//
	// private static final String DEV_PASSWORD = "MISSING";

	static public class RecordToCloudObserverInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param obj the Layers themselves
		 */
		public RecordToCloudObserverInfo(final RecordStatusToCloudObserverType obj) {
			super(obj, obj.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Active", "Whether this observer is active"),
						prop("Name", "The name of this observer"), };

				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static class RecToCloudTest extends TestCase {
		String _buildDate;
		String _headerDetails;
		boolean _detectionDetailsWritten;
		boolean _positionDetailsWritten;
		boolean _decisionDetailsWritten;

		// TODO FIX-TEST
		public void NtestToCloud() {
			final RecordStatusToCloudObserverType observer = new RecordStatusToCloudObserverType(true, true, true,
					new TargetType(Category.Type.SUBMARINE), "trial", true, "http://gnd.iriscouch.com", "tracks");
			assertNotNull("observer wasn't created", observer);

			observer.setActive(true);

			// and the scenario
			final CoreScenario cs = new CoreScenario();
			cs.setName("RecordTrial");
			cs.setTime(new Date().getTime());
			cs.setScenarioStepTime(new Duration(5, Duration.MINUTES));

			// add a participant
			final SSN ssn = new SSN(12);
			ssn.setName("Subject98");
			ssn.setCategory(
					new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));
			ssn.setDecisionModel(new ASSET.Models.Decision.Tactical.Wait(new Duration(12, Duration.HOURS), "do wait"));
			final OpticSensor sampleSensor = new OpticSensor(12) {
				private static final long serialVersionUID = 1L;

				// what is the detection strength for this target?
				@Override
				protected DetectionEvent detectThis(final EnvironmentType environment, final ParticipantType host,
						final ParticipantType target1, final long time, final ScenarioType scenario) {
					final DetectionEvent de = new DetectionEvent(12l, 12, null, this, null, null, null, null, null,
							null, null, null, ssn);
					return de;
				}
			};
			ssn.addSensor(sampleSensor);
			final HeloMovementCharacteristics moveChars = (HeloMovementCharacteristics) HeloMovementCharacteristics
					.getSampleChars();
			moveChars.setTurnRate(0.02);
			moveChars.setDecelRate(new WorldAcceleration(0.05, WorldAcceleration.Kts_sec));
			moveChars.setAccelRate(new WorldAcceleration(0.05, WorldAcceleration.Kts_sec));
			moveChars.setMinSpeed(new WorldSpeed(4, WorldSpeed.Kts));
			ssn.setMovementChars(moveChars);
			final Status theStat = new Status(12, 12);
			theStat.setTime(new Date().getTime());
			theStat.setLocation(new WorldLocation(18, -23, 0));
			theStat.setSpeed(new WorldSpeed(32, WorldSpeed.Kts));
			theStat.setCourse(335);
			ssn.setStatus(theStat);

			final UserControl userControl = new UserControl(330d, new WorldSpeed(18, WorldSpeed.Kts),
					new WorldDistance(0, WorldDistance.METRES));
			ssn.setDecisionModel(userControl);

			cs.addParticipant(12, ssn);

			// initialise results instances
			_buildDate = null;
			_headerDetails = null;
			_detectionDetailsWritten = false;
			_positionDetailsWritten = false;
			_decisionDetailsWritten = false;

			// and do the setup
			observer.setup(cs);

			// do a step
			cs.step();

			// do lots more steps
			for (int i = 0; i < 50; i++) {
				// do a step
				cs.step();
			}

			userControl.setCourse(313);
			userControl.setSpeed(new WorldSpeed(3, WorldSpeed.Kts));

			// do lots more steps
			for (int i = 0; i < 50; i++) {
				// do a step
				cs.step();
			}

			userControl.setCourse(166);
			userControl.setSpeed(new WorldSpeed(19, WorldSpeed.Kts));

			// do lots more steps
			for (int i = 0; i < 50; i++) {
				// do a step
				cs.step();
			}

			userControl.setCourse(44);
			userControl.setSpeed(new WorldSpeed(9, WorldSpeed.Kts));

			for (int i = 0; i < 200; i++) {
				// do a step
				cs.step();
			}

			userControl.setCourse(200);
			userControl.setSpeed(new WorldSpeed(19, WorldSpeed.Kts));

			for (int i = 0; i < 300; i++) {
				// do a step
				cs.step();
			}

			// and the close
			observer.tearDown(cs);
		}

		public void testDummy() {

		}
	}

	/**
	 * keep track of whether the analyst wants detections recorded
	 */
	protected boolean _recordDetections = false;

	/**
	 * keep track of whether the analyst wants decisions recorded
	 */
	private boolean _recordDecisions;

	// private Connection _conn;

	/**
	 * keep track of whether the analyst wants positions recorded
	 */
	private boolean _recordPositions;
	/**
	 * keep track of what target the analyst wants recorded
	 */
	private TargetType _subjectToTrack;

	private HashMap<NetworkParticipant, Track> _tracks;

	private HashMap<NetworkParticipant, List<NarrativeEntry>> _narratives;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	private final String _url;

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	private final String _database;

	/**
	 * an observer which wants to record it's data to file
	 *
	 * @param directoryName    the directory to output to
	 * @param fileName         the filename to output to
	 * @param recordDetections whether to record detections
	 * @param recordDecisions  whether to record decisions
	 * @param recordPositions  whether to record positions
	 * @param subjectToTrack   the type of target to track (or null for all targets)
	 * @param observerName     what to call this narrative observer
	 * @param isActive         whether this observer is active
	 * @param datasetPrefix
	 */
	public RecordStatusToCloudObserverType(final boolean recordDetections, final boolean recordDecisions,
			final boolean recordPositions, final TargetType subjectToTrack, final String observerName,
			final boolean isActive, final String storeURL, final String databaseNAme) {
		super(observerName, isActive);

		_recordDetections = recordDetections;
		_recordDecisions = recordDecisions;
		_recordPositions = recordPositions;
		_subjectToTrack = subjectToTrack;

		_url = storeURL;
		_database = databaseNAme;

		_tracks = new HashMap<NetworkParticipant, Track>();
		_narratives = new HashMap<NetworkParticipant, List<NarrativeEntry>>();

	}

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		_myScenario.addScenarioSteppedListener(this);
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new RecordToCloudObserverInfo(this);

		return _myEditor;
	}

	public boolean getRecordDecisions() {
		return _recordDecisions;
	}

	// private void createConnection()
	// {
	// try
	// {
	// String url = "jdbc:postgresql://86.134.91.5:5432/gnd";
	// _conn = DriverManager.getConnection(url, "dev", DEV_PASSWORD);
	// }
	// catch (SQLException e)
	// {
	// System.err.println("failed to create connection");
	// e.printStackTrace();
	// }
	// }

	public boolean getRecordDetections() {
		return _recordDetections;
	}

	public boolean getRecordPositions() {
		return _recordPositions;
	}

	// ////////////////////////////////////////////////
	// member getter/setters
	// ////////////////////////////////////////////////

	public TargetType getSubjectToTrack() {
		return _subjectToTrack;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {
		GNDStore store = null;

		final ArrayList<JsonNode> theDocs = new ArrayList<JsonNode>();
		try {
			// do we have any tracks?
			Collection<NetworkParticipant> keys = _tracks.keySet();
			for (final Iterator<NetworkParticipant> iterator = keys.iterator(); iterator.hasNext();) {
				final NetworkParticipant key = iterator.next();
				final Track track = _tracks.get(key);

				final JsonNode js = new GNDDocHandler().toJson(key.getName(), track, key.getName(),
						key.getCategory().getType(), "ASSET_SENSOR", "ASSET_POSITION", scenario.getName());

				theDocs.add(js);
			}

			// do we have any?
			if (theDocs.size() > 0) {
				// ok, do bulk submit
				if (store == null)
					store = new GNDStore(_url, _database);

				if (store != null)
					store.bulkPut(theDocs, 5);
			}

			// clear the list of tracks
			theDocs.clear();

			// and the decisions
			// do we have any tracks?
			keys = _narratives.keySet();
			for (final Iterator<NetworkParticipant> iterator = keys.iterator(); iterator.hasNext();) {
				final NetworkParticipant key = iterator.next();
				final List<NarrativeEntry> thisNarr = _narratives.get(key);

				final JsonNode js = new GNDDocHandler().toJson(key.getName(), thisNarr, key.getName(),
						key.getCategory().getType(), "ASSET_SENSOR", "ASSET_DECISION", scenario.getName());

				theDocs.add(js);
			}

			// do we have any?
			if (theDocs.size() > 0) {
				// ok, do bulk submit
				if (store == null)
					store = new GNDStore(_url, _database);

				if (store != null)
					store.bulkPut(theDocs, 5);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
		// ok, clear the lists
		if (_tracks != null)
			_tracks.clear();
		if (_narratives != null)
			_narratives.clear();

	}

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		_myScenario.removeScenarioSteppedListener(this);
	}

	public void setRecordDecisions(final boolean recordDecisions) {
		this._recordDecisions = recordDecisions;
	}

	public void setRecordDetections(final boolean recordDetections) {
		this._recordDetections = recordDetections;
	}

	public void setRecordPositions(final boolean recordPositions) {
		this._recordPositions = recordPositions;
	}

	public void setSubjectToTrack(final TargetType subjectToTrack) {
		this._subjectToTrack = subjectToTrack;
	}

	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {
		if (!isActive())
			return;

		// just check that/if we have an output file
		// if (_conn == null)
		// {
		// // ok, better sort out the output files.
		// createConnection();
		// }
		//
		// // still failing?
		// if(_conn == null)
		// return;
		//
		// get the positions of the participants
		final Integer[] lst = _myScenario.getListOfParticipants();
		for (int thisIndex = 0; thisIndex < lst.length; thisIndex++) {
			final Integer integer = lst[thisIndex];
			if (integer != null) {
				final ASSET.ParticipantType pt = _myScenario.getThisParticipant(integer.intValue());

				// is this a target of interest?
				if ((_subjectToTrack == null) || (_subjectToTrack.matches(pt.getCategory()))) {
					if (getRecordPositions()) {
						final ASSET.Participants.Status stat = pt.getStatus();
						final MWC.GenericData.WorldLocation loc = stat.getLocation();

						// ok, now output these details in our special format
						writeThesePositionDetails(loc, stat, pt, newTime);
					}

					if (getRecordDetections()) {
						// get the list of detections
						final DetectionList list = pt.getNewDetections();
						writeTheseDetectionDetails(pt, list, newTime);
					}

					if (getRecordDecisions()) {
						// get the current activity
						final String thisActivity = pt.getActivity();
						writeThisDecisionDetail(pt, thisActivity, newTime);
					}

				}

			}
		}

	}

	/**
	 * ok. ready to start writing. get on with it
	 *
	 * @param title      the title of this run
	 * @param currentDTG the current time (not model time)
	 * @throws IOException
	 */
	protected void writeFileHeaderDetails(final String title, final long currentDTG) {

	}

	/**
	 * write these detections to file
	 *
	 * @param pt         the participant we're on about
	 * @param detections the current set of detections
	 * @param dtg        the dtg at which the detections were observed
	 */
	protected void writeTheseDetectionDetails(final NetworkParticipant pt, final DetectionList detections,
			final long dtg) {
		// TODO: implement sending detections to file

		// try
		// {
		// // check there are some decisions
		// if (detections.size() == 0)
		// return;
		//
		// // see if we've loaded this participant
		// Integer theIndex = _participantIds.get(pt.getName() + DETECTION_FORMAT);
		// PreparedStatement stP;
		//
		// if (theIndex == null)
		// {
		// theIndex = getDatasetIndexFor(pt.getName(), DETECTION_FORMAT);
		// _participantIds.put(pt.getName() + DETECTION_FORMAT, theIndex);
		// }
		//
		// stP = _conn
		// .prepareStatement("INSERT INTO dataItems(datasetid, dtg, summary) VALUES (?,
		// ?, ?)");
		// stP.setInt(1, theIndex.intValue());
		// stP.setTimestamp(2, new Timestamp(dtg));
		//
		// Iterator<DetectionEvent> iter = detections.iterator();
		// while (iter.hasNext())
		// {
		// DetectionEvent de = iter.next();
		// String detStr = "";
		// Float brg = de.getBearing();
		// if (brg != null)
		// detStr += "Brg:" + brg.floatValue();
		// WorldDistance dist = de.getRange();
		// if (dist != null)
		// detStr += " Rng:" + dist.toString();
		// detStr += " " + de.getTargetType().toString();
		// stP.setString(3, detStr);
		// stP.executeUpdate();
		// }
		//
		// stP.close();
		// }
		// catch (SQLException e)
		// {
		// e.printStackTrace();
		// }
	}

	/**
	 * write this set of details to file
	 *
	 * @param loc  the current location
	 * @param stat the current status
	 * @param pt   the participant in question
	 */
	protected void writeThesePositionDetails(final WorldLocation loc, final Status stat, final NetworkParticipant pt,
			final long newTime) {
		if (_tracks == null)
			_tracks = new HashMap<NetworkParticipant, Track>();

		// find hte track
		Track theTrack = _tracks.get(pt);

		if (theTrack == null) {
			theTrack = new Track();
			theTrack.setName(pt.getName());
			_tracks.put(pt, theTrack);
		}

		final Fix newF = new Fix(new HiResDate(newTime), stat.getLocation(), stat.getCourse(),
				stat.getSpeed().getValueIn(WorldSpeed.M_sec));
		theTrack.addFix(newF);

	}

	// ////////////////////////////////////////////////////////////////////
	// editable properties
	// ////////////////////////////////////////////////////////////////////

	/**
	 * write the current decision description to file
	 *
	 * @param pt       the participant we're looking at
	 * @param activity a description of the current activity
	 * @param dtg      the dtg at which the description was recorded
	 */
	protected void writeThisDecisionDetail(final NetworkParticipant pt, final String activity, final long dtg) {

		if (_narratives == null)
			_narratives = new HashMap<NetworkParticipant, List<NarrativeEntry>>();

		// find hte track
		List<NarrativeEntry> thisNarr = _narratives.get(pt);

		if (thisNarr == null) {
			thisNarr = new ArrayList<NarrativeEntry>();
			_narratives.put(pt, thisNarr);
		}

		thisNarr.add(new NarrativeEntry(pt.getName(), new HiResDate(dtg), activity));
	}
}
