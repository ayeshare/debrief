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

import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Sensor.DeployableSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * Look out for deployable sensors, and track their location to a Replay file.
 *
 * PlanetMayo Ltd. 2003 User: Ian.Mayo Date: 03-Sep-2003 Time: 09:55:35 Log:
 * $Log: DebriefDeployableSensorLocationObserver.java,v $ Revision 1.1
 * 2006/08/08 14:22:08 Ian.Mayo Second import
 *
 * Revision 1.1 2006/08/07 12:26:16 Ian.Mayo First versions
 *
 * Revision 1.1 2004/08/12 11:06:51 Ian.Mayo Refactor observers into tidier
 * packages
 *
 * Revision 1.10 2004/08/12 09:21:55 Ian.Mayo Refactor all observers, to give
 * CoreObserver greater responsibility, and to tidy up writing to file
 *
 * Revision 1.9 2004/08/10 08:50:10 Ian.Mayo Change functionality of Debrief
 * replay observer so that it can record decisions & detections aswell. Also
 * include ability to track particular type of target
 *
 * Revision 1.8 2004/08/09 15:50:49 Ian.Mayo Refactor category types into Force,
 * Environment, Type sub-classes
 *
 * Revision 1.7 2004/05/24 16:07:08 Ian.Mayo Commit updates from home
 *
 * Revision 1.2 2004/04/08 20:27:54 ian Restructured contructor for CoreObserver
 *
 * Revision 1.1.1.1 2004/03/04 20:30:56 ian no message
 * <p/>
 * Revision 1.6 2004/02/16 13:46:51 Ian.Mayo Minor mods
 * <p/>
 * Revision 1.5 2003/09/18 14:13:00 Ian.Mayo Update with new World Speed class
 * <p/>
 * Revision 1.4 2003/09/12 13:15:29 Ian.Mayo Pass scenario time to recorders
 * <p/>
 * Revision 1.3 2003/09/03 14:00:45 Ian.Mayo And again
 * <p/>
 * Revision 1.2 2003/09/03 14:00:26 Ian.Mayo correct macro keyword
 */
public class DebriefDeployableSensorLocationObserver extends DebriefReplayObserver {

	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////
	/**
	 * the list of sensors we're watching
	 */
	private Vector<SensorType> _mySensors = null;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	/**
	 * constructor
	 *
	 * @param directoryName
	 * @param fileName
	 * @param recordDetections
	 */
	public DebriefDeployableSensorLocationObserver(final String directoryName, final String fileName,
			final boolean recordDetections, final String name, final boolean isActive) {
		super(directoryName, fileName, recordDetections, name, isActive);
	}

	/**
	 * ok, add this sensor and create the list if we have to
	 *
	 * @param sensor
	 */
	private void addSensor(final SensorType sensor) {
		if (_mySensors == null)
			_mySensors = new Vector<SensorType>(0, 1);

		_mySensors.add(sensor);
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 *
	 * @param scenario the scenario we're closing from
	 */
	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {
		// and clear our list
		if (_mySensors != null) {
			_mySensors.removeAllElements();
		}

		super.performCloseProcessing(scenario); // To change body of overridden methods use File |
												// Settings | File Templates.
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
		super.performSetupProcessing(scenario); // To change body of overridden methods use File |
												// Settings | File Templates.

		// ok, look for any sensors in the scenario

		// first step through the particiaapnts
		final Integer[] parts = scenario.getListOfParticipants();
		for (int i = 0; i < parts.length; i++) {
			final Integer part = parts[i];
			final ParticipantType thisP = scenario.getThisParticipant(part.intValue());

			// now the sensors
			final SensorList sl = thisP.getSensorFit();
			final Iterator<SensorType> theSensors = sl.getSensors().iterator();
			while (theSensors.hasNext()) {
				final SensorType sensor = theSensors.next();
				if (sensor instanceof DeployableSensor) {
					addSensor(sensor);
				}
			}
		}
	}

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////
	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {
		if (!isActive())
			return;

		if (_mySensors != null) {

			// get the positions of the participants
			for (int i = 0; i < _mySensors.size(); i++) {
				final DeployableSensor deployableSensor = (DeployableSensor) _mySensors.elementAt(i);
				final SensorType cs = (SensorType) deployableSensor;

				// just check we have a host id
				final int id = deployableSensor.getHostId();
				if (id != -1) {
					final NetworkParticipant pt = _myScenario.getThisParticipant(id);
					WorldLocation loc = pt.getStatus().getLocation();
					loc = deployableSensor.getLocation(loc);

					// wrap the sensor location in a status
					final Status stat = new Status(0, newTime);
					stat.setLocation(loc);
					stat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));

					// and the core participant

					final ParticipantType cp = new SSN(cs.getId());
					cp.setName(cs.getName());
					cp.setCategory(
							new Category(Category.Force.BLUE, Category.Environment.CROSS, Category.Type.SONAR_BUOY));

					// ok, now output these details in our special format
					writeThesePositionDetails(loc, stat, cp, newTime);
				}

			}
		}

	}

}
