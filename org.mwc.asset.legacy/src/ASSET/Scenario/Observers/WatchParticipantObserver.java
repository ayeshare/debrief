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

package ASSET.Scenario.Observers;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.MultiForceScenario;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.BatchCollatorHelper;

/**
 * Parent class used to maintain a list of participants matching supplied
 * category. Provides hooks for implementing classes to start/stop listening to
 * existing a new participants, and batch processing support skeleton.
 */
abstract public class WatchParticipantObserver extends CoreObserver
		implements ASSET.Scenario.ParticipantsChangedListener, BatchCollator {
	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	/**
	 * the type of vessel we are watching
	 */
	private final TargetType _watchVessel;
	/**
	 * the vessels we have added ourselves to
	 */
	private final Vector<ParticipantType> _watchedVessels = new Vector<ParticipantType>(0, 1);
	/**
	 * our batch collator
	 */
	protected BatchCollatorHelper _batcher = null;
	/**
	 * whether to override (cancel) writing per-scenario results to file
	 */
	protected boolean _onlyBatch = false;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	protected WatchParticipantObserver(final String myName, final boolean isActive, final TargetType watchVessel) {
		super(myName, isActive);

		// remember what we're going to look out for
		_watchVessel = watchVessel;
	}

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {

		// and register as a listener with the scenario
		_myScenario.addParticipantsChangedListener(this);

		// and just check if this is a multi-force scenario, in which case we have to
		// register
		// for the blue vessels aswell
		if (_myScenario instanceof MultiForceScenario) {
			final MultiForceScenario mfs = (MultiForceScenario) _myScenario;
			mfs.addBlueParticipantsChangedListener(this);
		}
	}

	/**
	 * listen to this participant, if required
	 */
	private void checkParticipant(final ASSET.ParticipantType newPart) {

		// is this our watch type?
		if (_watchVessel.matches(newPart.getCategory())) {
			// ok, listen to it
			listenTo(newPart);

			// add to our list
			_watchedVessels.add(newPart);
		}

	}

	//////////////////////////////////////////////////
	// inter-scenario observer methods
	//////////////////////////////////////////////////
	@Override
	public void finish() {
		if (_batcher != null) {
			// ok, get the batch thingy to do it's stuff
			_batcher.writeOutput(getHeaderInfo());
		}
	}

	/**
	 * accessor to retrieve batch processing settings
	 */
	@Override
	public BatchCollatorHelper getBatchHelper() {
		return _batcher;
	}

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 *
	 * @return whether to override batch processing
	 */
	@Override
	public boolean getBatchOnly() {
		return _onlyBatch;
	}

	/**
	 * we've finish running, get the results value to pass to the batch-processor
	 *
	 * @return the value to insert into the stats
	 */
	abstract protected Number getBatchResult();

	public Vector<ParticipantType> getWatchedVessels() {
		return _watchedVessels;
	}

	/**
	 * get the types of vessel we are monitoring
	 */
	public TargetType getWatchType() {
		return _watchVessel;
	}

	@Override
	public void initialise(final File outputDirectory) {
		// set the output directory for the batch collator
		if (_batcher != null)
			_batcher.setDirectory(outputDirectory);
	}

	/**
	 * this vessel matches what we're looking for. start listening to it
	 *
	 * @param newPart the new (matching) participant
	 */
	abstract protected void listenTo(final ParticipantType newPart);

	/**
	 * the indicated participant has been added to the scenario
	 */
	@Override
	public void newParticipant(final int index) {
		// is this of our type?
		checkParticipant(_myScenario.getThisParticipant(index));
	}

	/**
	 * the indicated participant has been removed from the scenario
	 */
	@Override
	public void participantRemoved(final int id) {
		final ParticipantType oldPart = _myScenario.getThisParticipant(id);

		final int index = _watchedVessels.indexOf(oldPart);
		if (index != -1) {
			_watchedVessels.removeElementAt(index);
			stopListeningTo(oldPart);
		}
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 *
	 * @param scenario the scenario we're closing from
	 */
	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {

		if (_batcher != null) {
			_batcher.submitResult(scenario.getName(), scenario.getCaseId(), getBatchResult());
		}
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {

		// clear anything we're currently listening to (we don't do this in the close,
		// since
		// we may wish to continue plotting our data after the scenario is complete
		_watchedVessels.removeAllElements();

		// get the participants from this scenario
		final Integer[] pList = scenario.getListOfParticipants();

		for (int thisI = 0; thisI < pList.length; thisI++) {
			final Integer integer = pList[thisI];
			if (integer != null) {
				final ASSET.ParticipantType pt = scenario.getThisParticipant(integer.intValue());

				checkParticipant(pt);
			}
		}

	}

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		// remove ourselves from our vessels
		final Iterator<ParticipantType> it = _watchedVessels.iterator();

		while (it.hasNext()) {
			final ASSET.ParticipantType part = it.next();
			stopListeningTo(part);
		}

		// and un-register as a listener with the scenario
		_myScenario.removeParticipantsChangedListener(this);

		// and just check if this is a multi-force scenario, in which case we have to
		// register
		// for the blue vessels aswell
		if (_myScenario instanceof MultiForceScenario) {
			final MultiForceScenario mfs = (MultiForceScenario) _myScenario;
			mfs.removeBlueParticipantsChangedListener(this);
		}

	}

	/**
	 * configure the batch processing
	 *
	 * @param fileName          the filename to write to
	 * @param collationMethod   how to collate the data
	 * @param perCaseProcessing whether to collate the stats on a per-case basis
	 * @param isActive          whether this collator is active
	 */
	@Override
	public void setBatchCollationProcessing(String fileName, final String collationMethod,
			final boolean perCaseProcessing, final boolean isActive) {
		_batcher = new BatchCollatorHelper(getName(), perCaseProcessing, collationMethod, isActive, "count");

		// do we have a filename?
		if (fileName == null)
			fileName = getName() + "." + "csv";

		_batcher.setFileName(fileName);
	}

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 *
	 * @param override
	 */
	@Override
	public void setBatchOnly(final boolean override) {
		_onlyBatch = override;
	}

	/**
	 * method indicating that we are to stop listening to this participant instance
	 *
	 * @param thisPart
	 */
	abstract protected void stopListeningTo(ParticipantType thisPart);
}
