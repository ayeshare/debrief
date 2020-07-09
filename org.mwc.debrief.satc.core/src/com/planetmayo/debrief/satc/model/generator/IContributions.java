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

package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeListener;
import java.util.SortedSet;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

/**
 * interface to manage contributions and perfom actions on contributions set
 *
 */
public interface IContributions extends Iterable<BaseContribution> {

	/**
	 * add contribution which will be used
	 */
	void addContribution(BaseContribution contribution);

	/**
	 * subscribe to contribution set events
	 */
	void addContributionsChangedListener(IContributionsChangedListener listener);

	/**
	 * add property listener to all contributions
	 */
	void addPropertyListener(String property, PropertyChangeListener listener);

	/**
	 * removes all contributions
	 */
	void clear();

	/**
	 * indicates if this contribution is already loaded
	 *
	 * @param contribution
	 * @return
	 */
	boolean contains(BaseContribution contribution);

	/**
	 * returns contributions set
	 */
	SortedSet<BaseContribution> getContributions();

	/**
	 * returns contribution which should be processed after specified one
	 */
	BaseContribution nextContribution(BaseContribution current);

	/**
	 * remove contribution
	 */
	void removeContribution(BaseContribution contribution);

	/**
	 * unsubscribe from contribution set events
	 */
	void removeContributionsChangedListener(IContributionsChangedListener listener);

	/**
	 * remove property listener from all contributions
	 */
	void removePropertyListener(String property, PropertyChangeListener listener);

	/**
	 * returns how many contributions do we have now
	 */
	int size();
}
