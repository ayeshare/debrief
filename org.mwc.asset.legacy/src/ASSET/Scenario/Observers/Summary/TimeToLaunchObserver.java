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

package ASSET.Scenario.Observers.Summary;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.NumberFormat;

import ASSET.NetworkParticipant;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Participants.Category;
import ASSET.Scenario.ParticipantsChangedListener;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.ScenarioObserver;
import MWC.GenericData.Duration;

/**
 * listener which looks out for a particular type of participant being created
 * (launched)
 */
public class TimeToLaunchObserver extends CoreObserver
		implements ScenarioObserver.ScenarioReferee, ParticipantsChangedListener, ScenarioSteppedListener {

	//////////////////////////////////////////////////
	// editable properties
	//////////////////////////////////////////////////
	static public class TimeToLaunchObserverInfo extends EditorType {

		/**
		 * constructor for editable details
		 *
		 * @param data the object we're going to edit
		 */
		public TimeToLaunchObserverInfo(final TimeToLaunchObserver data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this observer"),
						prop("TargetType", "the type of participant to look out for"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * the time we started (to calculate the elapsed time)
	 */
	private long _startTime;

	/**
	 * the current recorded time
	 */
	private long _currentTime;

	/**
	 * running tally of fitness score
	 */
	private double _myScore = -1;

	/**
	 * the target type for target vessels for the target we're watching
	 */
	private TargetType _targetType = null;

	/***************************************************************
	 * member methods
	 ***************************************************************/

	private EditorType _myEditor;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public TimeToLaunchObserver(final TargetType targetType, final String name, final boolean isActive) {
		super(name, isActive);
		// remember the target types
		_targetType = targetType;
	}

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		// and become a listener
		_myScenario.addScenarioSteppedListener(this);

		// listen to the participants being crated
		_myScenario.addParticipantsChangedListener(this);
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new TimeToLaunchObserverInfo(this);

		return _myEditor;
	}

	/**
	 * return how well this scenario performed, according to this referee
	 */
	@Override
	public ScenarioRunner.ScenarioOutcome getOutcome() {
		ScenarioRunner.ScenarioOutcome res = null;

		// did we decide the outcome?
		if (_myScore != -1) {
			res = new ScenarioRunner.ScenarioOutcome();
			final Duration elapsedD = new Duration(_myScore, Duration.MILLISECONDS);
			res.summary = "Launched after " + elapsedD;
			res.score = _myScore;
		}

		return res;
	}

	/**
	 * return a description of the performance of this run
	 *
	 */
	/**
	 * get a text description of the outcome
	 */
	public String getSummary() {
		// set the attributes
		final Duration duration = new Duration((long) _myScore, Duration.MILLISECONDS);

		// and get value
		final double value = duration.getValueIn(Duration.HOURS);
		final NumberFormat nf = new java.text.DecimalFormat("0.0");
		final String res = nf.format(value);

		return "Launch after:" + res + " hours";
	}

	/**
	 * get the types of vessel whose proximity we are checking for (targets)
	 */
	public TargetType getTargetType() {
		return _targetType;
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
	 * the indicated participant has been added to the scenario
	 */
	@Override
	public void newParticipant(final int index) {
		// get the participant
		final NetworkParticipant pt = _myScenario.getThisParticipant(index);

		// get it's type
		final Category theCat = pt.getCategory();

		// does it match?
		if (_targetType.matches(theCat)) {
			// we've won!
			_myScore = _currentTime - _startTime;

			// and finish!
			_myScenario.stop("Stopped on launch:" + getName());
		}
	}

	/**
	 * the indicated participant has been removed from the scenario
	 */
	@Override
	public void participantRemoved(final int index) {
		// hey, don't worry
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 *
	 * @param scenario the scenario we're closing from
	 */
	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {
		// reset the score
		_myScore = -1;
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
		_myScenario = scenario;

		// store the current time
		_startTime = _myScenario.getTime();
		_currentTime = _myScenario.getTime();
	}

	//////////////////////////////////////////////////
	// property editing
	//////////////////////////////////////////////////

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		// remove ourselves as a listener
		_myScenario.removeScenarioSteppedListener(this);
		_myScenario.removeParticipantsChangedListener(this);
	}

	/**
	 * the scenario has restarted
	 */
	@Override
	public void restart(final ScenarioType scenario) {
		super.restart(scenario);

		_myScore = -1;
	}

	public void setTargetType(final TargetType targetType) {
		this._targetType = targetType;
	}

	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {
		_currentTime = newTime;
	}

}
