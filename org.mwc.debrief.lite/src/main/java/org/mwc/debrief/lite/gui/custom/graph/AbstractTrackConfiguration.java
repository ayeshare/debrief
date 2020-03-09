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

package org.mwc.debrief.lite.gui.custom.graph;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
import Debrief.Wrappers.TrackWrapper;

public interface AbstractTrackConfiguration {
	public void addPropertyChangeListener(final PropertyChangeListener listener);

	public CalculationHolder getOperation();

	public TrackWrapper getPrimaryTrack();

	public List<AbstractSelection<TrackWrapper>> getTracks();

	public boolean isRelativeEnabled();

	public void setActiveTrack(final TrackWrapper track, final boolean check);

	public void setOperation(final CalculationHolder calculation);

	public void setPrimaryTrack(final TrackWrapper track);

	/**
	 *
	 * @param tracks Tracks to assign
	 * @return true if it was actually assigned. If they are the same, they are not
	 *         assigned.
	 */
	public boolean setTracks(final List<TrackWrapper> tracks);
}
