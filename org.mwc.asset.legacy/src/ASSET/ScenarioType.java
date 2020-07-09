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

package ASSET;

import java.util.Collection;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Scenario.ParticipantsChangedListener;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GenericData.Duration;

public interface ScenarioType extends ScenarioActivityMonitor {

	/**
	 * ************************************************************** names of
	 * properties which may be listened to
	 * **************************************************************
	 */
	public static final String NAME = "NAME";
	public static final String UNKNOWN = "UNKNOWN";

	/**
	 * null-value for participant id's
	 */
	public static final int INVALID_ID = -1;

	////////////////////////////////////////////////////////
	// listener-related
	////////////////////////////////////////////////////////
	public void addParticipantsChangedListener(ParticipantsChangedListener listener);

	// property change listeners
	public void addPropertyChangeListener(String property_name, java.beans.PropertyChangeListener listener);

	public void addScenarioRunningListener(ScenarioRunningListener listener);

	public void addScenarioSteppedListener(ScenarioSteppedListener listener);

	/**
	 * Shut down this scenario. Close the participants, etc
	 */
	void close();

	/**
	 * Create a new Participant. The external client can then request the
	 * Participant itself to perform any edits
	 *
	 * @param participant_type the type of Participant the client wants
	 * @return the id of the new Participant
	 */
	int createNewParticipant(String participant_type);

	/**
	 * find out what is plotted in the background
	 *
	 * @return
	 */
	public Layer getBackdrop();

	/**
	 * retreive the case this auto-generated scenario represents
	 *
	 * @return
	 */
	String getCaseId();

	/**
	 * collection of settings related to GUI display
	 *
	 * @param subject the particular setting to retrieve
	 * @return the current value of that setting (or null)
	 */
	public String getDisplaySettingFor(String subject);

	/**
	 * get the environment for this model
	 */
	public EnvironmentType getEnvironment();

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	@Override
	public Integer[] getListOfParticipants();

	/**
	 * Provide a list of id numbers of visible Participants we contain (in monte
	 * carlo, the multiple instances can't see each other)
	 *
	 * @return list of ids of Participant we contain
	 */
	public Collection<ParticipantType> getListOfVisibleParticipants();

	/**
	 * get the name of this scenario
	 */
	String getName();

	/**
	 * @return the scenario time step
	 */
	public int getScenarioStepTime();

	/**
	 * @return the step time for auto-stepping (or zero to run on)
	 */
	int getStepTime();

	/**
	 * Return a particular Participant - so that the Participant can be controlled
	 * directly. Listeners added/removed. Participants added/removed, etc.
	 */
	@Override
	ParticipantType getThisParticipant(int id);

	/**
	 * get the current scenario time
	 */
	public long getTime();

	/**
	 * find out if the scenario is currently auto-stepping
	 *
	 * @return yes/no, of course
	 */
	public boolean isRunning();

	/**
	 * pause the auto-play through a scenario
	 */
	public void pause();

	/**
	 * remove the indicated participant
	 */
	public void removeParticipant(int index);

	public void removeParticipantsChangedListener(ParticipantsChangedListener listener);

	public void removePropertyChangeListener(String property_name, java.beans.PropertyChangeListener listener);

	public void removeScenarioRunningListener(ScenarioRunningListener listener);

	public void removeScenarioSteppedListener(ScenarioSteppedListener listener);

	/**
	 * restart the scenario. this will reset all participants together with their
	 * sensors, movement models and behaviours
	 */
	public void restart();

	/**
	 * set what we see in the backdrop
	 *
	 * @param layer
	 */
	public void setBackdrop(BaseLayer layer);

	/**
	 * set the case this auto-generated scenario represents
	 *
	 * @param myCaseId
	 */
	void setCaseId(String myCaseId);

	////////////////////////////////////////////////////////
	// participant-related
	////////////////////////////////////////////////////////

	/**
	 * set the environment for this model
	 */
	public void setEnvironment(EnvironmentType theEnv);

	/**
	 * set the name of this scenario
	 */
	void setName(String val);

	/**
	 * set the scenario time step
	 *
	 * @param step_size time to step scenario forward at each step
	 */
	public void setScenarioStepTime(Duration step_size);

	/**
	 * set the scenario time step
	 *
	 * @param step_size millis to step scenario forward at each step
	 */
	public void setScenarioStepTime(int step_size);

	/**
	 * initialise the randomiser
	 *
	 * @param theSeed
	 */
	public void setSeed(Integer theSeed);

	/**
	 * set the size of the time delay (or zero to run to completion)
	 *
	 * @param step_size time to pause before step (or zero to run)
	 */
	void setStepTime(Duration step_size);

	/**
	 * set the size of the time delay (or zero to run to completion)
	 *
	 * @param step_size time to pause before step (or zero to run)
	 */
	void setStepTime(int step_size);

	/**
	 * set the current scenario time
	 */
	public void setTime(long time);

	/**
	 * Start the scenario auto-stepping through itself. If the step time is set to
	 * zero, it will automatically step after the completion of the previous step.
	 */
	public void start();

	/**
	 * Move the scenario through a single step
	 */
	public void step();

	/**
	 * Finish the scenario, stopping recording, dead.
	 *
	 * @param reason why the scenario has been stopped
	 */
	public void stop(String reason);

}
