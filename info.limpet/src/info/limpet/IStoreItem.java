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
package info.limpet;

import java.util.UUID;

public interface IStoreItem {

	void addChangeListener(IChangeListener listener);

	/**
	 * add a change listener that should not be persisted to file
	 *
	 * @param listener
	 */
	void addTransientChangeListener(IChangeListener listener);

	/**
	 * tell listeners that it's about to be deleted
	 *
	 */
	public void beingDeleted();

	/**
	 * indicate that the collection has changed Note: both registered listeners
	 * and dependents are informed of the change
	 */
	void fireDataChanged();

	String getName();

	/**
	 * find the layer that contains this collection (or null if applicable)
	 *
	 * @return parent collection, or null
	 */
	IStoreGroup getParent();

	UUID getUUID();

	void removeChangeListener(IChangeListener listener);

	void removeTransientChangeListener(IChangeListener collectionChangeListener);

	/**
	 * set the parent object for this collection
	 *
	 * @param parent
	 */
	void setParent(IStoreGroup parent);
}
